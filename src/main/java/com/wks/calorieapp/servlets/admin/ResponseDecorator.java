package com.wks.calorieapp.servlets.admin;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseDecorator {

    public enum View {
        LOGIN("/WEB-INF/login.jsp"),
        ADMIN("/WEB-INF/admin.jsp"),

        INDEXES("/WEB-INF/indexes.jsp"),
        IMAGES("/WEB-INF/images.jsp"),
        ERROR("/WEB-INF/error.jsp");

        private final String serverPage;

        View(String serverPage) {
            this.serverPage = serverPage;
        }
    }

    public enum Path {
        LOGIN("/login"),
        ADMIN("/admin");

        private final String value;

        Path(String value) {
            this.value = value;
        }
    }

    private HttpServletResponse response;

    private HttpServletRequest request;

    private ResponseDecorator(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public static ResponseDecorator of(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseDecorator(request, response);
    }

    public void forwardTo(View view) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(view.serverPage);
        dispatcher.forward(request, response);
    }

    public void redirectTo(Path path) throws IOException {
        response.sendRedirect(request.getContextPath() + path.value);
    }
}
