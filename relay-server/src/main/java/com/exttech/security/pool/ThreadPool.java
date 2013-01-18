package com.exttech.security.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.exttech.security.relay.BoxResponse;
import com.exttech.security.relay.Cache;
import com.exttech.security.relay.RelayServiceManagerTest;
import com.exttech.security.relay.RelayTaskTest;

public class ThreadPool {

	private static final Logger log = Logger.getLogger(ThreadPool.class);

	public static final long TIMEOUT = 91;

	private static ThreadPool instance = new ThreadPool();

	private ExecutorService pool;

	private ThreadPool() {
		pool = Executors.newCachedThreadPool();
	}

	public static ThreadPool getInstance() {
		return instance;
	}

	public BoxResponse execute(String user, String uuid) {
		BoxResponse response = new BoxResponse();
		try {
			response = pool.submit(new ProcessThread(user, uuid)).get(TIMEOUT, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			response.setContent("relay timeout.".getBytes());
		} catch (Exception e) {
			response.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setReason(e.getMessage());
		}

		return response;
	}

	private static class ProcessThread implements Callable<BoxResponse> {
		private String user;
		private String uuid;

		public ProcessThread(String user, String uuid) {
			this.user = user;
			this.uuid = uuid;
		}

		@Override
		public BoxResponse call() throws Exception {
			BoxResponse response = null;
			try {
				RelayServiceManagerTest.sendMessage(user, uuid, "".getBytes());
				long startTime = System.currentTimeMillis();
				while ((response = Cache.responses.get(uuid)) == null) {
					try{
					Thread.sleep(10);
					}catch(Exception e){
					}
					if ((System.currentTimeMillis() - startTime) > TIMEOUT) {
						response = new BoxResponse();
						response.setContent("relay timeout.".getBytes());
					}
				}
			} finally {
				Cache.responses.remove(uuid);
			}
			return response;
		}

	}
}
