package com.wks.calorieapp.resources.admin.login;

import javax.servlet.http.HttpSession;

public class LoginSessionDecorator {

    private final HttpSession session;

    private LoginSessionDecorator(HttpSession session) {
        this.session = session;
    }

    public static LoginSessionDecorator of(HttpSession session) {
        return new LoginSessionDecorator(session);
    }

    public LoginSessionDecorator setUsername(String username) {
        session.setAttribute("username", username);
        return this;
    }

    public String getUsername() {
        return (String) session.getAttribute("username");
    }

    public boolean isAuthenticated() {
        final Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        return Boolean.TRUE.equals(authenticated);
    }

    public LoginSessionDecorator setAuthenticated() {
        session.setAttribute("authenticated", true);
        return this;
    }

    public LoginSessionDecorator removeAuthenticated() {
        session.removeAttribute("authenticated");
        return this;
    }
}
