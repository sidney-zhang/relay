package com.exttech.security.web.controller;

import com.exttech.security.relay.RelayFileUploadListener;
import com.exttech.security.relay.UploadListener;
import com.exttech.security.util.RelayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Controller
public class BoxController {

    @Autowired
    private UploadListener uploadListener;

    @RequestMapping("/uploadNofiy")
    public void uploadNofiy(HttpServletRequest request, HttpServletResponse response) {
        uploadListener.onRequest(request, response);
    }

    @RequestMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (RelayConfig.SLEEP_TIME != -1) {
            try {
                Thread.sleep(RelayConfig.SLEEP_TIME);
            } catch (InterruptedException e) {
            }
        }
        OutputStream out = response.getOutputStream();
        response.setContentLength(RelayFileUploadListener.bytes.length);
        out.write(RelayFileUploadListener.bytes);
        out.flush();
    }

}
