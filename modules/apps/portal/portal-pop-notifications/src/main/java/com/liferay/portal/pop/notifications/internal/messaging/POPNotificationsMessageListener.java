/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.pop.notifications.internal.messaging;

import com.liferay.mail.kernel.model.Account;
import com.liferay.mail.kernel.service.MailService;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.pop.MessageListener;
import com.liferay.portal.kernel.pop.MessageListenerException;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;
import com.liferay.portal.kernel.scheduler.SchedulerEntryImpl;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.pop.notifications.internal.MessageListenerWrapper;
import com.liferay.portal.util.PropsValues;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Brian Wing Shun Chan
 */
@Component(service = {})
public class POPNotificationsMessageListener extends BaseMessageListener {

	@Activate
	protected void activate(BundleContext bundleContext) {
		if (PropsValues.POP_SERVER_NOTIFICATIONS_ENABLED) {
			_messageListenerWrappers = ServiceTrackerListFactory.open(
				bundleContext, MessageListener.class, null,
				new ServiceTrackerCustomizer
					<MessageListener, MessageListenerWrapper>() {

					@Override
					public MessageListenerWrapper addingService(
						ServiceReference<MessageListener> serviceReference) {

						return new MessageListenerWrapper(
							bundleContext.getService(serviceReference));
					}

					@Override
					public void modifiedService(
						ServiceReference<MessageListener> serviceReference,
						MessageListenerWrapper messageListenerWrapper) {
					}

					@Override
					public void removedService(
						ServiceReference<MessageListener> serviceReference,
						MessageListenerWrapper sessageListenerWrapper) {

						bundleContext.ungetService(serviceReference);
					}

				});

			Class<?> clazz = getClass();

			String className = clazz.getName();

			Trigger trigger = _triggerFactory.createTrigger(
				className, className, null, null, 1, TimeUnit.MINUTE);

			SchedulerEntry schedulerEntry = new SchedulerEntryImpl(
				className, trigger);

			_schedulerEngineHelper.register(
				this, schedulerEntry, DestinationNames.SCHEDULER_DISPATCH);
		}
	}

	@Deactivate
	protected void deactivate() {
		if (PropsValues.POP_SERVER_NOTIFICATIONS_ENABLED) {
			_schedulerEngineHelper.unregister(this);

			_messageListenerWrappers.close();
		}
	}

	@Override
	protected void doReceive(
			com.liferay.portal.kernel.messaging.Message message)
		throws MessagingException {

		Store store = null;

		try {
			store = _getStore();

			Folder inboxFolder = _getInboxFolder(store);

			if (inboxFolder == null) {
				return;
			}

			try {
				Message[] messages = inboxFolder.getMessages();

				if (messages == null) {
					return;
				}

				if (_log.isDebugEnabled()) {
					_log.debug("Deleting messages");
				}

				inboxFolder.setFlags(
					messages, new Flags(Flags.Flag.DELETED), true);

				_notifyMessageListeners(messages);
			}
			finally {
				inboxFolder.close(true);
			}
		}
		finally {
			if (store != null) {
				store.close();
			}
		}
	}

	private String _getEmailAddress(Address[] addresses) {
		if (ArrayUtil.isEmpty(addresses)) {
			return StringPool.BLANK;
		}

		InternetAddress internetAddress = (InternetAddress)addresses[0];

		return internetAddress.getAddress();
	}

	private List<String> _getEmailAddresses(Address[] addresses) {
		if (ArrayUtil.isEmpty(addresses)) {
			return new ArrayList<>();
		}

		List<String> emailAddresses = new ArrayList<>();

		for (Address address : addresses) {
			InternetAddress internetAddress = (InternetAddress)address;

			emailAddresses.add(internetAddress.getAddress());
		}

		return emailAddresses;
	}

	private Folder _getInboxFolder(Store store) throws MessagingException {
		Folder defaultFolder = store.getDefaultFolder();

		Folder[] folders = defaultFolder.list();

		if (folders.length == 0) {
			throw new MessagingException("Inbox not found");
		}

		Folder inboxFolder = folders[0];

		inboxFolder.open(Folder.READ_WRITE);

		return inboxFolder;
	}

	private Store _getStore() throws MessagingException {
		Session session = _mailService.getSession();

		String storeProtocol = GetterUtil.getString(
			session.getProperty("mail.store.protocol"));

		if (!storeProtocol.equals(Account.PROTOCOL_POPS)) {
			storeProtocol = Account.PROTOCOL_POP;
		}

		Store store = session.getStore(storeProtocol);

		String prefix = "mail." + storeProtocol + ".";

		String host = session.getProperty(prefix + "host");

		String user = session.getProperty(prefix + "user");

		if (Validator.isNull(user)) {
			user = session.getProperty("mail.smtp.user");
		}

		String password = session.getProperty(prefix + "password");

		if (Validator.isNull(password)) {
			password = session.getProperty("mail.smtp.password");
		}

		store.connect(host, user, password);

		return store;
	}

	private void _notifyMessageListeners(Message[] messages)
		throws MessagingException {

		if (_log.isDebugEnabled()) {
			_log.debug("Messages " + messages.length);
		}

		for (Message message : messages) {
			if (_log.isDebugEnabled()) {
				_log.debug("Message " + message);
			}

			String from = _getEmailAddress(message.getFrom());

			List<String> recipients = _getEmailAddresses(
				message.getRecipients(Message.RecipientType.TO));

			if (_log.isDebugEnabled()) {
				_log.debug("From " + from);
				_log.debug("Recipients " + recipients.toString());
			}

			for (MessageListener messageListener : _messageListenerWrappers) {
				try {
					if (messageListener.accept(from, recipients, message)) {
						messageListener.deliver(from, recipients, message);
					}
				}
				catch (MessageListenerException messageListenerException) {
					_log.error(messageListenerException);
				}
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		POPNotificationsMessageListener.class);

	@Reference
	private MailService _mailService;

	private ServiceTrackerList<MessageListenerWrapper> _messageListenerWrappers;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	@Reference
	private TriggerFactory _triggerFactory;

}