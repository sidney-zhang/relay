package com.exttech.security.relay;

import com.exttech.security.util.RelayCache;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service("relayRequestListener")
public class DefaultRelayRequestListener implements RelayRequestListener {

    @Autowired
    @Qualifier("RelayNotifier")
    private RelayNotifier relayNotifier;

    private Executor processPool = Executors.newCachedThreadPool();

    @Override
    public void onRequest(HttpServletRequest request, HttpServletResponse response) {
        ProcessThread processThread = new ProcessThread(relayNotifier, request, response);
        processPool.execute(processThread);
    }


    private static class ProcessThread implements Runnable {


        private final HttpServletRequest request;
        private final HttpServletResponse response;
        private final RelayNotifier relayNotifier;

        public ProcessThread(RelayNotifier relayNotifier, HttpServletRequest request, HttpServletResponse response) {
            this.relayNotifier = relayNotifier;
            this.request = request;
            this.response = response;
        }

        @Override
        public void run() {
            String uuid = UUID.randomUUID().toString();
            relayNotifier.notifier(uuid, request,response);
            while(!RelayCache.responseCache.contains(uuid)){
                sleep(5);
            }
        }

        private void sleep(long sleepTime){
            try {
                Thread.currentThread().sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
    }
}
