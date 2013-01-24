package com.exttech.security.relay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface RelayRequestProcessor {

    void process(HttpServletRequest request, HttpServletResponse response);

    void onReceived(String uuid, byte[] msg);

    void download(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
