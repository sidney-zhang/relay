package com.exttech.security.util;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxingyu
 * Date: 1/22/13
 * Time: 7:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileCache {
    public static ConcurrentHashMap<String, byte[]> bytesCache = new ConcurrentHashMap<String, byte[]>();
}
