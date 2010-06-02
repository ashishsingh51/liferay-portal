/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.cache.memcached.factory;

import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.net.InetSocketAddress;

import java.util.ArrayList;
import java.util.List;

import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;

/**
 * <a href="DefaultMemcachedClientFactory.java.html"><b><i>View Source</i></b></a>
 *
 * @author Michael C. Han
 */
public class DefaultMemcachedClientFactory implements MemcachedClientFactory {

	public MemcachedClientIF getMemcachedClient() throws Exception {
		MemcachedClientIF memCachedClient = new MemcachedClient(
			_memcachedConnectionFactory, _memcachedAddresses);

		return memCachedClient;
	}

	public void returnMemcachedObject(MemcachedClientIF memcachedClient)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	public void invalidateMemcachedClient(MemcachedClientIF memcachedClient)
		throws Exception {

		throw new UnsupportedOperationException();
	}

	public int getNumIdle() {

		throw new UnsupportedOperationException();
	}

	public int getNumActive() {

		throw new UnsupportedOperationException();
	}

	public void clear() throws Exception {
	}

	public void close() throws Exception {

	}

	public void setMemcachedConnectionFactory(
		ConnectionFactory memcachedConnectionFactory) {
		_memcachedConnectionFactory = memcachedConnectionFactory;
	}

	public void setMemcachedAddresses(List<String> memcachedAddresses) {

		for (String memcachedAddress : memcachedAddresses) {
			String[] hostAndPort = StringUtil.split(
				memcachedAddress, StringPool.COLON);

			String hostName = hostAndPort[0];
			int port = _DEFAULT_MEMCACHED_PORT;
			if (hostAndPort.length == 2) {
				port = Integer.parseInt(hostAndPort[1]);
			}

			InetSocketAddress inetSocketAddress =
				new InetSocketAddress(hostName, port);

			_memcachedAddresses.add(inetSocketAddress);
		}
	}

	private static final int _DEFAULT_MEMCACHED_PORT = 11211;

	private ConnectionFactory _memcachedConnectionFactory;
	private List<InetSocketAddress> _memcachedAddresses =
		new ArrayList<InetSocketAddress>();

}