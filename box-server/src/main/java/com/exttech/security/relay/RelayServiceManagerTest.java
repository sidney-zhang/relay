package com.exttech.security.relay;

import it.sauronsoftware.base64.Base64;

import java.io.UnsupportedEncodingException;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;

public class RelayServiceManagerTest implements MessageListener {
	private static final Logger log = Logger.getLogger(RelayServiceManagerTest.class);
	private static final String MQ_URL = "tcp://192.168.3.35:61616";
	private static final String MQ_REQUEST_URL = "personalcloud.relayservice.hub1.request";
	private static final String MQ_RESPONSE_URL = "personalcloud.relayservice.hub1.response";
	// JMX Connection
	private Connection m_connection;
	// JMX Session
	private Session m_session;
	// Message Producer
	private MessageProducer m_producer;

	private static RelayServiceManagerTest m_instance = new RelayServiceManagerTest();

	/**
	 * 唯一标识hub server， 多个hub server集群保持唯一
	 */
	public final String HUB_ID = "hub1";

	// Singleton Model
	public static RelayServiceManagerTest getInstance() {
		return m_instance;
	}

	private RelayServiceManagerTest() {
		log.info("RelayServiceManager start");
		connect2MQ();
	}

	public void connect2MQ() {
		try {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(MQ_URL);
			m_connection = factory.createConnection();
			// no transaction mode
			m_session = m_connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// request queue
			Destination requestQueue = m_session.createQueue(MQ_REQUEST_URL);
			m_producer = m_session.createProducer(requestQueue);
			m_producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Destination responseQueue = requestQueue;
			Destination responseQueue = m_session.createQueue(MQ_RESPONSE_URL);
			MessageConsumer consumer = m_session.createConsumer(responseQueue);
			consumer.setMessageListener(this);
			m_connection.start();

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(Message message) {
		if (!(message instanceof TextMessage))
			return;
		try {
			String uuid = message.getStringProperty("connUuid");
			TextMessage msg = (TextMessage) message;
			Cache.tasks.remove(uuid).onMessage(msg);
			log.debug("onMessage:" + msg.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Boolean sendMessage0(String username, String connUuid, byte[] data) {

		log.debug("JMX sendMessage0");
		try {
			Cache.tasks.put(connUuid, new RelayTaskTest(username, connUuid, "".getBytes()));
			TextMessage msg = createTextMessage(data, username, connUuid);
			log.debug("TextMessage = " + msg);
			m_producer.send(msg);
		} catch (Exception e) {
			// 尝试重新注册activemq
			connect2MQ();
			try {
				TextMessage msg = createTextMessage(data, username, connUuid);
				log.debug("TextMessage = " + msg);
				m_producer.send(msg);
			} catch (Exception ex) {
				log.error("RelayServiceManager sendMessage0" + ex);
				return false;
			}

		}
		return true;
	}

	private TextMessage createTextMessage(byte[] data, String username, String connUuid) throws JMSException, JSONException,
			UnsupportedEncodingException {
		String dataStr = new String(Base64.encode(data), "ASCII");
		data = null;
		TextMessage msg = m_session.createTextMessage();
		msg.setStringProperty("connUuid", connUuid);
		msg.setStringProperty("username", username);
		msg.setStringProperty("upload_url", "upload-test");
		msg.setStringProperty("download_url", "download-test");
		msg.setStringProperty("hub_id", HUB_ID);
		msg.setStringProperty("querystring", "?test");
		msg.setStringProperty("method", "GET");
		msg.setStringProperty("hub_id", HUB_ID);
		msg.setStringProperty("headers", "{test headers}");
		if (dataStr != null) {
			msg.setText(dataStr);
		}
		return msg;
	}

	public static Boolean sendMessage(String username, String connUuid, byte[] data) {
		return m_instance.sendMessage0(username, connUuid, data);
	}
}
