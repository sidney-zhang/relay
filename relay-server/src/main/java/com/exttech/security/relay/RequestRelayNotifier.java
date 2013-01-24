package com.exttech.security.relay;

import com.exttech.security.util.FileCache;
import com.exttech.security.util.RelayConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.*;

@Component("relayNotifier")
public class RequestRelayNotifier implements RelayNotifier {

    private static final Logger log = Logger.getLogger(RequestRelayNotifier.class);

//    private Executor notifierPool = Executors.newCachedThreadPool();
    private Executor notifierPool = new ThreadPoolExecutor(RelayConfig.POOL_CORE_SIZE, Integer.MAX_VALUE,
        0L, TimeUnit.NANOSECONDS,
        new SynchronousQueue<Runnable>());

    @Override
    public void notifier(String uuid, HttpServletRequest request, HttpServletResponse response) {
        NotifierThread notifierThread = new NotifierThread(uuid, request, response);
        notifierPool.execute(notifierThread);
    }

    private static class NotifierThread implements Runnable {

        private final HttpServletResponse response;
        private final HttpServletRequest request;
        private final String uuid;

        public NotifierThread(String uuid, HttpServletRequest request, HttpServletResponse response) {
            this.uuid = uuid;
            this.request = request;
            this.response = response;
        }

        @Override
        public void run() {
            try {
                notifyBox();
            } catch (IOException e) {
                log.error("", e);
            }
        }

        private void notifyBox() throws IOException {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpClientRequest = new HttpGet(RelayConfig.URL_BOX);
            constructRequestHeaders(httpClientRequest);
//            log.info("connect to box server:" + RelayConfig.URL_BOX);
            httpClientRequest.setHeader("uuid", uuid);
            HttpResponse response = httpClient.execute(httpClientRequest);
            log.info("response:" + response.getStatusLine().toString());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 500) {
                //TODO resend ?
                log.warn("Faild to notify box. url:" + RelayConfig.URL_BOX + ",responseCode:" + statusCode);
            }
            httpClientRequest.abort();
        }

        private void constructRequestHeaders(HttpGet httpClientRequest) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                httpClientRequest.setHeader(headerName, request.getHeader(headerName));
            }
        }
    }
}
