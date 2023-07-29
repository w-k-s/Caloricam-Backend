package com.wks.calorieapp.servlets.admin.images;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AdminImagesRequestDecorator {

    private final HttpServletRequest request;

    private AdminImagesRequestDecorator(HttpServletRequest request) {
        this.request = request;
    }

    public static AdminImagesRequestDecorator of(HttpServletRequest request) {
        return new AdminImagesRequestDecorator(request);
    }

    public String getAction() {
        return request.getParameter("action");
    }

    public String getImage() {
        return request.getParameter("img");
    }

    public AdminImagesRequestDecorator setImagesList(List<String> indexFilesList) {
        request.setAttribute("images", indexFilesList);
        return this;
    }

    public List<String> getImagesFilesList() {
        return (List<String>) request.getAttribute("images");
    }
}
