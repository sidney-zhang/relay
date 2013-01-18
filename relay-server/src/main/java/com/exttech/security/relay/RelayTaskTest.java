package com.exttech.security.relay;

import it.sauronsoftware.base64.Base64;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public class RelayTaskTest implements RelayMessageListener {
	
	private static final Logger log = Logger.getLogger(RelayTaskTest.class);


	
	/**
	 * base64 encoded string content
	 */
	private StringBuffer content = new StringBuffer();
	
	/**
	 * response headers;
	 */
	private String responseHeaders = null;

	/**
	 * response string
	 */
	private String reason = null;
	
	/**
	 * response status
	 */
	private String status = null;

	private String username;
	private String uuid;
	private byte[] data;
	
	public RelayTaskTest(String username, String uuid, byte[] data){
		this.username = username;
		this.uuid = uuid;
		this.data = data;
		
	}

	public void doing() {
		Cache.tasks.put(uuid, this);
		RelayServiceManagerTest.sendMessage(username, uuid, data);
	}

	@Override
	public void onMessage(TextMessage message) {
		BoxResponse response = new BoxResponse();
		log.debug("RealyTask onMessage");
		try {
			if(responseHeaders == null){
				log.debug("RealyTask set headers");
				response.setHeaders(message.getStringProperty("headers"));
				response.setReason(message.getStringProperty("reason"));
				response.setCode(Integer.valueOf(message.getStringProperty("status")));
			}
			
			String base64Str = message.getText();
	        if (base64Str!=null && base64Str.length() > 0) {
	               content.append(base64Str);
	        }
	        response.setContent(Base64.decode(content.toString().getBytes()));
	        
	        Cache.responses.put(uuid, response);
//	        String strIsLast = message.getStringProperty("isLast");
//			log.debug("RealyTask isLast = "+strIsLast);
//	        if(strIsLast!=null && strIsLast.length() > 0){
//	        	isRelayFinished = Boolean.parseBoolean(strIsLast);
//	        }
		} catch (JMSException e) {
			log.error("", e);
		}
	}

	public String getResponseHeaders() {
		return responseHeaders;
	}

	public void setResponseHeaders(String responseHeaders) {
		this.responseHeaders = responseHeaders;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public byte [] getContentBytes(){
		return Base64.decode(content.toString().getBytes());
	}

	public StringBuffer getContent() {
		return content;
	}

	public void setContent(StringBuffer content) {
		this.content = content;
	}

}
