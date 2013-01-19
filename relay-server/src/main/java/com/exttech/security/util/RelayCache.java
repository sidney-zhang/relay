package com.exttech.security.util;

import java.util.concurrent.ConcurrentHashMap;

public class RelayCache {
    public static final ConcurrentHashMap<String, String> responseCache = new ConcurrentHashMap<String, String>();
}
