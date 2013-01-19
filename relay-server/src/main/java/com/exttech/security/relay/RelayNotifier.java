package com.exttech.security.relay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxingyu
 * Date: 1/19/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RelayNotifier {

     void notifier(String uuid, HttpServletRequest request, HttpServletResponse response);

}
