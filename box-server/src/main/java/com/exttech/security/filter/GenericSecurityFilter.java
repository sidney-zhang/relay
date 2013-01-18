package com.exttech.security.filter;

import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxingyu
 * Date: 9/18/12
 * Time: 11:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenericSecurityFilter extends AbstractSecurityInterceptor implements Filter {
    @Override
    public Class<?> getSecureObjectClass() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
