package com.exttech.security.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxingyu
 * Date: 1/19/13
 * Time: 2:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller("relayController")
public class RelayController {

    @RequestMapping("relay")
    public void relay(HttpServletRequest request, HttpServletResponse response){

    }

}
