package com.exttech.security.relay;

import com.exttech.security.util.FileCache;
import com.exttech.security.util.RelayConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component("relayRequestProcessor")
public class DefaultRelayRequestProcessor implements RelayRequestProcessor {

    private static final Logger log = Logger.getLogger(DefaultRelayRequestProcessor.class);

    @Autowired
    private RelayNotifier relayNotifier;

    //    private Executor processPool = Executors.newCachedThreadPool();
    private Executor processPool = new ThreadPoolExecutor(RelayConfig.POOL_CORE_SIZE, Integer.MAX_VALUE,
            0L, TimeUnit.NANOSECONDS,
            new SynchronousQueue<Runnable>());

    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) {
        ProcessThread processThread = new ProcessThread(relayNotifier, request, response);
        new ProcessThread(relayNotifier, request, response).run();
    }

    @Override
    public void onReceived(String uuid, byte[] msg) {
        lock.lock();
        try {
            FileCache.bytesCache.put(uuid, msg);
            condition.signalAll();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpClientRequest = new HttpGet(RelayConfig.URL_DOWNLOAD_BOX);
        constructRequestHeaders(httpClientRequest, request);
        HttpResponse httpResponse = httpClient.execute(httpClientRequest);
        log.info("response:" + httpResponse.getStatusLine().toString());
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode >= 500) {
            //TODO resend ?
            log.warn("Faild to notify box. url:" + RelayConfig.URL_BOX + ",responseCode:" + statusCode);
        } else {
            BufferedHttpEntity entity = new BufferedHttpEntity(httpResponse.getEntity());
            InputStream in = entity.getContent();
            long length = entity.getContentLength();
            OutputStream out = response.getOutputStream();
            ByteArrayOutputStream receiveBytes = new ByteArrayOutputStream();
            long startTime = System.currentTimeMillis();
            while (receiveBytes.size() < length) {
                if (in.available() > 0) {
                    byte[] body = new byte[in.available()];
                    in.read(body);
                    receiveBytes.write(body);
                    log.info("received bytes: " + body.length);
                }
                log.warn("sleeping");
                if((System.currentTimeMillis() - startTime) > RelayConfig.TIMEOUT){
                    response.getOutputStream().write("TIMEOUT-download!".getBytes());
                    response.getOutputStream().flush();
                }
            }
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition ", "attachment; filename=test500k.zip");
            response.setHeader("Content-Length ", String.valueOf(receiveBytes.size()));
            out.write(receiveBytes.toByteArray());
            out.flush();
            receiveBytes.close();
        }
        httpClientRequest.abort();
    }


    private void constructRequestHeaders(HttpGet httpClientRequest, HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            httpClientRequest.setHeader(headerName, request.getHeader(headerName));
        }
    }


    private static class ProcessThread {
        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final RelayNotifier relayNotifier;

        public ProcessThread(RelayNotifier relayNotifier, HttpServletRequest request, HttpServletResponse response) {
            this.relayNotifier = relayNotifier;
            this.request = request;
            this.response = response;
        }

        //        @Override
        public void run() {
            String uuid = UUID.randomUUID().toString();
            relayNotifier.notifier(uuid, request, response);
            long startTime = System.currentTimeMillis();
            long remainTime = 0;
            while ((remainTime = System.currentTimeMillis() - startTime) < RelayConfig.TIMEOUT) {
                if (!FileCache.bytesCache.containsKey(uuid)) {
                    lock.lock();
                    try {
                        condition.await(remainTime, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        log.error("", e);
                        try {
                            response.getOutputStream().write(("ERROR!" + e.getMessage()).getBytes());
                            response.getOutputStream().flush();
                        } catch (IOException ioe) {
                            log.error("", ioe);
                        }
                    } finally {
                        lock.unlock();
                    }
                } else {
                    break;
                }
            }
            try {
                process(uuid);
            } catch (IOException e) {
                log.error("", e);
            }
        }

        private void process(String uuid) throws IOException {
            log.info("in process. uuid=" + uuid);
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            response.setHeader("Content-Disposition ", "attachment; filename=test500k.zip");
            response.setHeader("Content-Length ", String.valueOf(FileCache.bytesCache.get(uuid).length));
            response.getOutputStream().write(FileCache.bytesCache.remove(uuid));
            response.getOutputStream().flush();
        }

        private void sleep(long sleepTime) {
            try {
                Thread.currentThread().sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
    }
}
