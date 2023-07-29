package com.wks.calorieapp.servlets.admin.login;

import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final LoginSessionDecorator loginSession = LoginSessionDecorator.of(request.getSession());

        if (!loginSession.isAuthenticated()) {
            LOGGER.info("Admin Index. Page requested. User not authenticated");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        LOGGER.info(String.format("'%s' request by '%s'", request.getServletPath(), loginSession.getUsername()));
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
