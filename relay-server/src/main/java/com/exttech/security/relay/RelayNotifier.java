package com.exttech.security.relay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RelayNotifier {

    void notifier(String uuid, HttpServletRequest request, HttpServletResponse response);
}
