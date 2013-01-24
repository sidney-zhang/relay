package com.exttech.security.relay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxingyu
 * Date: 1/22/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public interface UploadListener {
    public void onRequest(HttpServletRequest request, HttpServletResponse response);
}
