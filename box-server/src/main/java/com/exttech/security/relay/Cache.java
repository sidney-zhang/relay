package com.exttech.security.relay;

import java.util.concurrent.ConcurrentHashMap;

public class Cache {
	public static final ConcurrentHashMap<String, RelayTaskTest> tasks = new ConcurrentHashMap<String, RelayTaskTest>();
	public static final ConcurrentHashMap<String, BoxResponse> responses = new ConcurrentHashMap<String, BoxResponse>();
}
