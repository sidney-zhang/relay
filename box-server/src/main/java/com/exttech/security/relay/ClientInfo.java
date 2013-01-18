package com.exttech.security.relay;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户资源信息
 * @author zhaolin
 *
 */
public class ClientInfo {
	/**
	 * 最后更新日期
	 */
    public Date lastUpdateTime = new Date();
    /**
     * 账号下所有的在线资源
     */
    public ConcurrentHashMap<String, ClientIPInfo> clients = new ConcurrentHashMap<String, ClientIPInfo>();
}
