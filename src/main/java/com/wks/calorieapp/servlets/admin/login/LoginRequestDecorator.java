package com.wks.calorieapp.servlets.admin.login;

import javax.servlet.http.HttpServletRequest;

public class LoginRequestDecorator {

    private final HttpServletRequest request;

    private LoginRequestDecorator(HttpServletRequest request) {
        this.request = request;
    }

    public static LoginRequestDecorator of(HttpServletRequest request) {
        return new LoginRequestDecorator(request);
    }

    public LoginRequestDecorator setFlashMessage(String flash) {
        request.setAttribute("status", flash);
        return this;
    }

    public LoginRequestDecorator removeFlashMessage() {
        request.removeAttribute("status");
        return this;
    }

    public String getUsername() {
        return request.getParameter("username");
    }

    public String getPassword() {
        return request.getParameter("password");
    }
}
