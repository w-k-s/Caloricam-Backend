package com.wks.calorieapp.servlets.admin.error;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AdminErrorRequestDecorator {

    private final HttpServletRequest request;

    private AdminErrorRequestDecorator(HttpServletRequest request) {
        this.request = request;
    }

    public static AdminErrorRequestDecorator of(HttpServletRequest request) {
        return new AdminErrorRequestDecorator(request);
    }

    public AdminErrorRequestDecorator setMessage(String message) {
        request.setAttribute("error", message);
        return this;
    }

}
