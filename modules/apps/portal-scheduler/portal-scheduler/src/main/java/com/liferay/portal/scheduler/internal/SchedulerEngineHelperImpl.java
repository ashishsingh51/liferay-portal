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

package com.liferay.portal.scheduler.internal;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.cluster.ClusterableContextThreadLocal;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.scheduler.JobState;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerEntry;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.StorageTypeAware;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListener;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerEventMessageListenerWrapper;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.InetAddressUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.scheduler.internal.configuration.SchedulerEngineHelperConfiguration;
import com.liferay.portal.scheduler.internal.messaging.config.ScriptingMessageListener;

import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.scheduler.configuration.SchedulerEngineHelperConfiguration",
	enabled = false, service = SchedulerEngineHelper.class
)
public class SchedulerEngineHelperImpl implements SchedulerEngineHelper {

	@Override
	public void addScriptingJob(
			Trigger trigger, StorageType storageType, String description,
			String language, String script)
		throws SchedulerException {

		Message message = new Message();

		message.put(SchedulerEngine.LANGUAGE, language);
		message.put(SchedulerEngine.SCRIPT, script);

		schedule(
			trigger, storageType, description,
			DestinationNames.SCHEDULER_SCRIPTING, message);
	}

	@Override
	public void auditSchedulerJobs(Message message, TriggerState triggerState)
		throws SchedulerException {

		if (!_schedulerEngineHelperConfiguration.auditSchedulerJobEnabled() ||
			(_auditRouter == null)) {

			return;
		}

		try {
			AuditMessage auditMessage = new AuditMessage(
				SchedulerEngine.SCHEDULER, CompanyConstants.SYSTEM, 0,
				StringPool.BLANK, SchedulerEngine.class.getName(), "0",
				triggerState.toString(), new Date(),
				_jsonFactory.createJSONObject(_jsonFactory.serialize(message)));

			auditMessage.setServerName(InetAddressUtil.getLocalHostName());
			auditMessage.setServerPort(_portal.getPortalLocalPort(false));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new SchedulerException(exception);
		}
	}

	@Override
	public void delete(SchedulerEntry schedulerEntry, StorageType storageType)
		throws SchedulerException {

		Trigger trigger = schedulerEntry.getTrigger();

		delete(trigger.getJobName(), trigger.getGroupName(), storageType);
	}

	@Override
	public void delete(String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.delete(groupName, storageType);
	}

	@Override
	public void delete(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.delete(jobName, groupName, storageType);
	}

	@Override
	public Date getEndTime(SchedulerResponse schedulerResponse) {
		Message message = schedulerResponse.getMessage();

		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		TriggerState triggerState = jobState.getTriggerState();

		if (triggerState.equals(TriggerState.NORMAL) ||
			triggerState.equals(TriggerState.PAUSED)) {

			return (Date)message.get(SchedulerEngine.END_TIME);
		}

		return jobState.getTriggerDate(SchedulerEngine.END_TIME);
	}

	@Override
	public Date getEndTime(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse = getScheduledJob(
			jobName, groupName, storageType);

		if (schedulerResponse != null) {
			return getEndTime(schedulerResponse);
		}

		return null;
	}

	@Override
	public Date getFinalFireTime(SchedulerResponse schedulerResponse) {
		Message message = schedulerResponse.getMessage();

		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		TriggerState triggerState = jobState.getTriggerState();

		if (triggerState.equals(TriggerState.NORMAL) ||
			triggerState.equals(TriggerState.PAUSED)) {

			return (Date)message.get(SchedulerEngine.FINAL_FIRE_TIME);
		}

		return jobState.getTriggerDate(SchedulerEngine.FINAL_FIRE_TIME);
	}

	@Override
	public Date getFinalFireTime(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse = getScheduledJob(
			jobName, groupName, storageType);

		if (schedulerResponse != null) {
			return getFinalFireTime(schedulerResponse);
		}

		return null;
	}

	@Override
	public TriggerState getJobState(SchedulerResponse schedulerResponse) {
		Message message = schedulerResponse.getMessage();

		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		return jobState.getTriggerState();
	}

	@Override
	public TriggerState getJobState(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse = getScheduledJob(
			jobName, groupName, storageType);

		if (schedulerResponse != null) {
			return getJobState(schedulerResponse);
		}

		return null;
	}

	@Override
	public Date getNextFireTime(SchedulerResponse schedulerResponse) {
		Message message = schedulerResponse.getMessage();

		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		TriggerState triggerState = jobState.getTriggerState();

		if (triggerState.equals(TriggerState.NORMAL) ||
			triggerState.equals(TriggerState.PAUSED)) {

			return (Date)message.get(SchedulerEngine.NEXT_FIRE_TIME);
		}

		return jobState.getTriggerDate(SchedulerEngine.NEXT_FIRE_TIME);
	}

	@Override
	public Date getNextFireTime(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse = getScheduledJob(
			jobName, groupName, storageType);

		if (schedulerResponse != null) {
			return getNextFireTime(schedulerResponse);
		}

		return null;
	}

	@Override
	public Date getPreviousFireTime(SchedulerResponse schedulerResponse) {
		Message message = schedulerResponse.getMessage();

		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		TriggerState triggerState = jobState.getTriggerState();

		if (triggerState.equals(TriggerState.NORMAL) ||
			triggerState.equals(TriggerState.PAUSED)) {

			return (Date)message.get(SchedulerEngine.PREVIOUS_FIRE_TIME);
		}

		return jobState.getTriggerDate(SchedulerEngine.PREVIOUS_FIRE_TIME);
	}

	@Override
	public Date getPreviousFireTime(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse = getScheduledJob(
			jobName, groupName, storageType);

		if (schedulerResponse != null) {
			return getPreviousFireTime(schedulerResponse);
		}

		return null;
	}

	@Override
	public SchedulerResponse getScheduledJob(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		return _schedulerEngine.getScheduledJob(
			jobName, groupName, storageType);
	}

	@Override
	public List<SchedulerResponse> getScheduledJobs()
		throws SchedulerException {

		return _schedulerEngine.getScheduledJobs();
	}

	@Override
	public List<SchedulerResponse> getScheduledJobs(StorageType storageType)
		throws SchedulerException {

		return _schedulerEngine.getScheduledJobs(storageType);
	}

	@Override
	public List<SchedulerResponse> getScheduledJobs(
			String groupName, StorageType storageType)
		throws SchedulerException {

		return _schedulerEngine.getScheduledJobs(groupName, storageType);
	}

	@Override
	public Date getStartTime(SchedulerResponse schedulerResponse) {
		Message message = schedulerResponse.getMessage();

		JobState jobState = (JobState)message.get(SchedulerEngine.JOB_STATE);

		TriggerState triggerState = jobState.getTriggerState();

		if (triggerState.equals(TriggerState.NORMAL) ||
			triggerState.equals(TriggerState.PAUSED)) {

			return (Date)message.get(SchedulerEngine.START_TIME);
		}

		return jobState.getTriggerDate(SchedulerEngine.START_TIME);
	}

	@Override
	public Date getStartTime(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		SchedulerResponse schedulerResponse = getScheduledJob(
			jobName, groupName, storageType);

		if (schedulerResponse != null) {
			return getStartTime(schedulerResponse);
		}

		return null;
	}

	@Override
	public void pause(String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.pause(groupName, storageType);
	}

	@Override
	public void pause(String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.pause(jobName, groupName, storageType);
	}

	@Override
	public void register(
		MessageListener messageListener, SchedulerEntry schedulerEntry,
		String destinationName) {

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", destinationName
			).build();

		Class<?> messageListenerClass = messageListener.getClass();

		SchedulerEventMessageListenerWrapper
			schedulerEventMessageListenerWrapper =
				new SchedulerEventMessageListenerWrapper();

		schedulerEventMessageListenerWrapper.setMessageListener(
			messageListener);

		schedulerEventMessageListenerWrapper.setSchedulerEntry(schedulerEntry);

		ServiceRegistration<SchedulerEventMessageListener> serviceRegistration =
			_bundleContext.registerService(
				SchedulerEventMessageListener.class,
				schedulerEventMessageListenerWrapper, properties);

		_serviceRegistrations.put(
			messageListenerClass.getName(), serviceRegistration);
	}

	@Override
	public void resume(String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.resume(groupName, storageType);
	}

	@Override
	public void resume(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.resume(jobName, groupName, storageType);
	}

	@Override
	public void run(String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.run(jobName, groupName, storageType);
	}

	@Override
	public void schedule(
			Trigger trigger, StorageType storageType, String description,
			String destinationName, Message message)
		throws SchedulerException {

		_schedulerEngine.validateTrigger(trigger, storageType);

		if (message == null) {
			message = new Message();
		}

		_schedulerEngine.schedule(
			trigger, description, destinationName, message, storageType);
	}

	@Override
	public void schedule(
			Trigger trigger, StorageType storageType, String description,
			String destinationName, Object payload)
		throws SchedulerException {

		Message message = new Message();

		message.setPayload(payload);

		schedule(trigger, storageType, description, destinationName, message);
	}

	@Override
	public void shutdown() throws SchedulerException {
		_schedulerEngine.shutdown();
	}

	@Override
	public void start() throws SchedulerException {
		_schedulerEngine.start();
	}

	@Override
	public void unregister(MessageListener messageListener) {
		Class<?> messageListenerClass = messageListener.getClass();

		ServiceRegistration<?> serviceRegistration =
			_serviceRegistrations.remove(messageListenerClass.getName());

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	@Override
	public void unschedule(
			SchedulerEntry schedulerEntry, StorageType storageType)
		throws SchedulerException {

		Trigger trigger = schedulerEntry.getTrigger();

		unschedule(trigger.getJobName(), trigger.getGroupName(), storageType);
	}

	@Override
	public void unschedule(String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.unschedule(groupName, storageType);
	}

	@Override
	public void unschedule(
			String jobName, String groupName, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.unschedule(jobName, groupName, storageType);
	}

	@Override
	public void update(
			String jobName, String groupName, StorageType storageType,
			String description, String language, String script)
		throws SchedulerException {

		SchedulerResponse schedulerResponse = getScheduledJob(
			jobName, groupName, storageType);

		if (schedulerResponse == null) {
			return;
		}

		Trigger trigger = schedulerResponse.getTrigger();

		if (trigger == null) {
			return;
		}

		Message message = schedulerResponse.getMessage();

		if (message == null) {
			return;
		}

		addScriptingJob(trigger, storageType, description, language, script);
	}

	@Override
	public void update(Trigger trigger, StorageType storageType)
		throws SchedulerException {

		_schedulerEngine.update(trigger, storageType);
	}

	@Activate
	protected void activate(ComponentContext componentContext)
		throws Exception {

		_schedulerEngineHelperConfiguration =
			ConfigurableUtil.createConfigurable(
				SchedulerEngineHelperConfiguration.class,
				componentContext.getProperties());

		_bundleContext = componentContext.getBundleContext();

		_registerDestination(
			_bundleContext, DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
			DestinationNames.SCHEDULER_DISPATCH);

		Destination scriptingDestination = _registerDestination(
			_bundleContext, DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
			DestinationNames.SCHEDULER_SCRIPTING);

		SchedulerEventMessageListenerWrapper
			schedulerEventMessageListenerWrapper =
				new SchedulerEventMessageListenerWrapper();

		schedulerEventMessageListenerWrapper.setMessageListener(
			new ScriptingMessageListener());

		scriptingDestination.register(schedulerEventMessageListenerWrapper);

		_serviceTracker = ServiceTrackerFactory.open(
			_bundleContext, SchedulerEventMessageListener.class,
			new SchedulerEventMessageListenerServiceTrackerCustomizer());

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				start();

				return null;
			});
	}

	@Deactivate
	protected void deactivate() {
		if (_bundleContext == null) {
			return;
		}

		if (_serviceTracker != null) {
			_serviceTracker.close();
		}

		try {
			shutdown();
		}
		catch (SchedulerException schedulerException) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to shutdown scheduler", schedulerException);
			}
		}

		for (ServiceRegistration<Destination> serviceRegistration :
				_destinationServiceRegistrations) {

			Destination destination = _bundleContext.getService(
				serviceRegistration.getReference());

			serviceRegistration.unregister();

			destination.destroy();
		}

		for (ServiceRegistration<SchedulerEventMessageListener>
				serviceRegistration : _serviceRegistrations.values()) {

			serviceRegistration.unregister();
		}

		_bundleContext = null;
	}

	@Modified
	protected void modified(Map<String, Object> properties) throws Exception {
		_schedulerEngineHelperConfiguration =
			ConfigurableUtil.createConfigurable(
				SchedulerEngineHelperConfiguration.class, properties);
	}

	private Destination _registerDestination(
		BundleContext bundleContext, String destinationType,
		String destinationName) {

		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(destinationType, destinationName);

		Destination destination = _destinationFactory.createDestination(
			destinationConfiguration);

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"destination.name", destination.getName()
			).build();

		ServiceRegistration<Destination> serviceRegistration =
			bundleContext.registerService(
				Destination.class, destination, dictionary);

		_destinationServiceRegistrations.add(serviceRegistration);

		return destination;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SchedulerEngineHelperImpl.class);

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	private volatile AuditRouter _auditRouter;

	private volatile BundleContext _bundleContext;

	@Reference
	private DestinationFactory _destinationFactory;

	private final Set<ServiceRegistration<Destination>>
		_destinationServiceRegistrations = new HashSet<>();

	@Reference
	private JSONFactory _jsonFactory;

	private final Map<String, ServiceRegistration<MessageListener>>
		_messageListenerServiceRegistrations = new ConcurrentHashMap<>();

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private Portal _portal;

	@Reference(target = "(scheduler.engine.proxy=true)")
	private SchedulerEngine _schedulerEngine;

	private volatile SchedulerEngineHelperConfiguration
		_schedulerEngineHelperConfiguration;
	private final Map
		<String, ServiceRegistration<SchedulerEventMessageListener>>
			_serviceRegistrations = new ConcurrentHashMap<>();
	private volatile ServiceTracker
		<SchedulerEventMessageListener, SchedulerEventMessageListener>
			_serviceTracker;

	private class SchedulerEventMessageListenerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<SchedulerEventMessageListener, SchedulerEventMessageListener> {

		@Override
		public SchedulerEventMessageListener addingService(
			ServiceReference<SchedulerEventMessageListener> serviceReference) {

			Bundle bundle = serviceReference.getBundle();

			BundleContext bundleContext = bundle.getBundleContext();

			SchedulerEventMessageListener schedulerEventMessageListener =
				bundleContext.getService(serviceReference);

			SchedulerEntry schedulerEntry =
				schedulerEventMessageListener.getSchedulerEntry();

			if ((schedulerEntry == null) ||
				(schedulerEntry.getTrigger() == null)) {

				return null;
			}

			StorageType storageType = StorageType.MEMORY_CLUSTERED;

			if (schedulerEntry instanceof StorageTypeAware) {
				StorageTypeAware storageTypeAware =
					(StorageTypeAware)schedulerEntry;

				storageType = storageTypeAware.getStorageType();
			}

			String destinationName = (String)serviceReference.getProperty(
				"destination.name");

			if (Validator.isNull(destinationName)) {
				destinationName = DestinationNames.SCHEDULER_DISPATCH;
			}

			ClusterableContextThreadLocal.putThreadLocalContext(
				SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, false);

			try {
				schedule(
					schedulerEntry.getTrigger(), storageType,
					schedulerEntry.getDescription(), destinationName, null);

				_messageListenerServiceRegistrations.put(
					schedulerEntry.getEventListenerClass(),
					bundleContext.registerService(
						MessageListener.class, schedulerEventMessageListener,
						HashMapDictionaryBuilder.<String, Object>put(
							"destination.name", destinationName
						).build()));

				return schedulerEventMessageListener;
			}
			catch (SchedulerException schedulerException) {
				_log.error(schedulerException);
			}
			finally {
				ClusterableContextThreadLocal.putThreadLocalContext(
					SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, true);
			}

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<SchedulerEventMessageListener> serviceReference,
			SchedulerEventMessageListener schedulerEventMessageListener) {
		}

		@Override
		public void removedService(
			ServiceReference<SchedulerEventMessageListener> serviceReference,
			SchedulerEventMessageListener schedulerEntryMessageListener) {

			Bundle bundle = serviceReference.getBundle();

			BundleContext bundleContext = bundle.getBundleContext();

			bundleContext.ungetService(serviceReference);

			SchedulerEntry schedulerEntry =
				schedulerEntryMessageListener.getSchedulerEntry();

			StorageType storageType = StorageType.MEMORY_CLUSTERED;

			if (schedulerEntry instanceof StorageTypeAware) {
				StorageTypeAware storageTypeAware =
					(StorageTypeAware)schedulerEntry;

				storageType = storageTypeAware.getStorageType();
			}

			ClusterableContextThreadLocal.putThreadLocalContext(
				SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, false);

			try {
				delete(schedulerEntry, storageType);
			}
			catch (SchedulerException schedulerException) {
				_log.error(schedulerException);
			}
			finally {
				ClusterableContextThreadLocal.putThreadLocalContext(
					SchedulerEngine.SCHEDULER_CLUSTER_INVOKING, true);
			}

			ServiceRegistration<MessageListener>
				messageListenerServiceRegistration =
					_messageListenerServiceRegistrations.remove(
						schedulerEntry.getEventListenerClass());

			messageListenerServiceRegistration.unregister();
		}

	}

}