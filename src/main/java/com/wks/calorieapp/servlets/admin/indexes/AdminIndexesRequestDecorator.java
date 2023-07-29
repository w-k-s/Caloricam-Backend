package com.wks.calorieapp.servlets.admin.indexes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AdminIndexesRequestDecorator {

    private final HttpServletRequest request;

    private AdminIndexesRequestDecorator(HttpServletRequest request) {
        this.request = request;
    }

    public static AdminIndexesRequestDecorator of(HttpServletRequest request) {
        return new AdminIndexesRequestDecorator(request);
    }

    public String getAction() {
        return request.getParameter("action");
    }

    public AdminIndexesRequestDecorator setIndexFilesList(List<String> indexFilesList) {
        request.setAttribute("indexes", indexFilesList);
        return this;
    }

    public List<String> getIndexFilesList() {
        return (List<String>) request.getAttribute("indexes");
    }
}
