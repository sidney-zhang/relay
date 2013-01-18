package com.exttech.security.web.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.exttech.security.pool.ThreadPool;
import com.exttech.security.relay.BoxResponse;
import com.exttech.security.relay.ClientIPInfo;
import com.exttech.security.relay.ClientInfo;

@Controller("indexController")
public class IndexController {
	
	private static final Logger log = Logger.getLogger(IndexController.class);
	
	@RequestMapping("/index")
	public String index(HttpServletRequest request) {
		return "index";
	}

	@RequestMapping("/msg")
	public @ResponseBody
	byte[] message(@RequestParam String user, HttpServletRequest request, HttpServletResponse response) {
		int index = user.lastIndexOf('.');
		String res = user.substring(0, index);
		String username = user.substring(index + 1);
		
		ClientInfo clientInfo = getUserInfo(username);
		if (clientInfo == null || clientInfo.clients.size() < 1) {
			return "error_resource_offline".getBytes();
		}

		if (res == null) {
			return "error_resource_offline".getBytes();
		}

		// 全部转为小写
		res = URLDecoder.decode(res.toLowerCase()).toLowerCase();
		log.debug("res = " + res);

		ClientIPInfo singleClient = null;

		if ((singleClient = clientInfo.clients.get(res)) == null) {
			return "error_resource_offline".getBytes();
		}

		String jid = singleClient.jid;
		String key = jid + "_" + System.currentTimeMillis() + System.nanoTime();
		
		BoxResponse boxResponse = ThreadPool.getInstance().execute(jid,UUID.randomUUID().toString());
		buildHeaders(response, boxResponse);
		response.setStatus(boxResponse.getCode());
		if (boxResponse.getCode() != HttpServletResponse.SC_OK) {
			return boxResponse.getReason().getBytes();
		} else {
			return boxResponse.getContent();
		}
	}
	
	private void buildHeaders(HttpServletResponse response, BoxResponse boxResponse) {
		try {
			JSONObject jsonHeaders = new JSONObject(boxResponse.getHeaders());
			Iterator<String> itor = jsonHeaders.keys();
			while (itor.hasNext()) {
				String headerKey = itor.next();
				response.addHeader(headerKey, jsonHeaders.getString(headerKey));
			}
		} catch (JSONException e) {
			log.error("", e);
		}
	}
	
	private ClientInfo getUserInfo(String user) {
		if (user == null) {
			return null;
		}
		ClientInfo clientInfo = null;

		clientInfo = new ClientInfo();

		BufferedReader oIn = null;
		try {
			// get user info from xmpp server plugin
			URL oUrl = new URL("http://192.168.3.35:9090/plugins/relayserviceplugin/status?user=" + user);
			URLConnection oConn = oUrl.openConnection();
			if (oConn == null) {
				return null;
			}

			oIn = new BufferedReader(new InputStreamReader(oConn.getInputStream()));

			String ips = oIn.readLine();

			JSONArray jsonObj = new JSONArray(ips);
			int len = jsonObj.length();
			if (len <= 0) {
				return null;
			}

			for (int i = 0; i < len; i++) {
				JSONObject ipAndRes = jsonObj.getJSONObject(i);
				String sip = ipAndRes.getString("ip");
				String res = ipAndRes.getString("res");
				String localIP = ipAndRes.getString("localIP");
				int localPort = ipAndRes.getInt("localPort");
				String jid = ipAndRes.getString("jid");
				String sID = ipAndRes.getString("serialID");
				ClientIPInfo singleClientInfo = new ClientIPInfo();
				singleClientInfo.ip = sip;
				singleClientInfo.localIp = localIP;
				singleClientInfo.localPort = localPort;
				singleClientInfo.jid = jid;
				singleClientInfo.serialID = sID;
				if (ipAndRes.has("natIP") && ipAndRes.has("natPort")) {
					String natIP = ipAndRes.getString("natIP");
					int natPort = ipAndRes.getInt("natPort");
					singleClientInfo.natIp = natIP;
					singleClientInfo.natPort = natPort;
				}

				clientInfo.clients.put(res.toLowerCase(), singleClientInfo);
			}
			clientInfo.lastUpdateTime = new Date();
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		} finally {
			if (oIn != null) {
				try {
					oIn.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return clientInfo;
	}

}
