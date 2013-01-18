package com.exttech.security.relay;

/**
 * Client IP Information
 * @author zhaolin
 *
 */
public class ClientIPInfo {
	/**
	 * 资源本地ip
	 */
    public String localIp = null;
    /**
     * 资源本地端口
     */
    public int localPort = -1;
    /**
     * 客户端出口的ip
     */
    public String ip = null;
    //xmpp (openfire id)
    public String jid = null;
    
    public String serialID = null;
    
    /**
     * nat映射之后的ip
     */
    public String natIp = null;
    /**
     * nat之后的port
     */
    public int natPort = -1;
}