package com.wks.calorieapp.filters;

import com.wks.calorieapp.resources.AdminImages;
import com.wks.calorieapp.resources.Attributes;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class);
    private static final String ATTRIBUTE_AUTHENTICATED = "authenticated";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession();
        final Boolean authenticated = (Boolean) session.getAttribute(ATTRIBUTE_AUTHENTICATED);
        final String username = (String) session.getAttribute("username");

        if (authenticated == null || !authenticated) {
            LOGGER.info("Admin Index. Page requested. User not authenticated");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        LOGGER.info(String.format("'%s' request by '%s'", request.getServletPath(), username));
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
