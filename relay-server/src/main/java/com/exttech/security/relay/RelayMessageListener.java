package com.exttech.security.relay;

import javax.jms.TextMessage;

public interface RelayMessageListener {
	/**
	 * 收到消息时候的回调
	 * @param message
	 */
	void onMessage(TextMessage message);
}
