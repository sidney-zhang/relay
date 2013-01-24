package com.exttech.security.web.controller;

import com.exttech.security.relay.RelayRequestProcessor;
import com.exttech.security.util.RelayConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

@Controller("relayController")
public class RelayController {
    private static final Logger log = Logger.getLogger(RelayController.class);

    @Autowired
    private RelayRequestProcessor relayRequestProcessor;

    private static ConcurrentHashMap<String, Long> times = new ConcurrentHashMap<String, Long>();

    @RequestMapping("relay")
    public void relay(HttpServletRequest request, HttpServletResponse response) {
        relayRequestProcessor.process(request, response);
    }

    @RequestMapping("relaySleep")
    public void relaySleep(HttpServletRequest request, HttpServletResponse response) throws IOException {
        relayRequestProcessor.download(request, response);
    }

    @RequestMapping("upload")
    public void upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream in = request.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int contentLength = request.getContentLength();
        while (out.size() < contentLength) {
            byte[] tmp = new byte[in.available()];
            in.read(tmp);
            out.write(tmp);
        }
        relayRequestProcessor.onReceived(request.getHeader("uuid"), out.toByteArray());
        out.close();
    }

    private static final byte[] msg = new byte[RelayConfig.FILE_SIZE];

    @RequestMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition ", "attachment; filename=test500k.zip");
        response.setHeader("Content-Length ", String.valueOf(RelayConfig.FILE_SIZE));
        ServletOutputStream out = response.getOutputStream();
        out.write(msg);
        out.flush();
    }

}
