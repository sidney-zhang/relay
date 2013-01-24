package com.exttech.security.relay;

import com.exttech.security.util.RelayConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component("uploadListener")
public class RelayFileUploadListener implements UploadListener {

    private static final Logger log = Logger.getLogger(RelayFileUploadListener.class);

    public static byte[] bytes = new byte[RelayConfig.FILE_SIZE];

    //    private static Executor uploadPool = Executors.newCachedThreadPool();
    private static Executor uploadPool = new ThreadPoolExecutor(RelayConfig.POOL_CORE_SIZE, Integer.MAX_VALUE,
            0L, TimeUnit.NANOSECONDS,
            new SynchronousQueue<Runnable>());
    private static URI URL = null;

    public RelayFileUploadListener() {
        try {
            URL = new URI(RelayConfig.URL_RELAY_SERVER);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
//        try {
//            BufferedInputStream in = new BufferedInputStream(new FileInputStream(RelayConfig.FILE_PATH));
//            ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
//            byte[] tmp = new byte[in.available()];
//            log.info("length:" + in.available());
//            in.read(tmp);
//            out.write(tmp);
//            bytes = out.toByteArray();
//            in.close();
//            out.close();
//            in = null;
//            out = null;
//        } catch (Exception e) {
//            log.error("", e);
//        }
    }

    @Override
    public void onRequest(HttpServletRequest request, HttpServletResponse response) {
        String uuid = request.getHeader("uuid");
        uploadPool.execute(new UploadThread(uuid));
    }

    private static class UploadThread implements Runnable {
        private final String uuid;

        public UploadThread(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient();
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
            HttpPost post = new HttpPost(URL);
            post.setEntity(new InputStreamEntity(new ByteArrayInputStream(bytes), bytes.length));
            post.setHeader("uuid", uuid);
            try {

                HttpResponse httpResponse = httpClient.execute(post);
                log.info(httpResponse.getStatusLine());
                HttpEntity responseEntity = httpResponse.getEntity();
                if (responseEntity != null) {
                    log.info("chunked? " + responseEntity.isChunked());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                post.abort();
            }
        }
    }
}
