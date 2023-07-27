package com.wks.calorieapp.utils;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseUtils {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection != null) return connection;

        try {
            InitialContext ctx = new InitialContext();
            DataSource ds  = (DataSource) ctx.lookup("java:/comp/env/jdbc/main");
            connection = ds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void close(Connection connection) {
        close(connection, null, null);
    }

    public static void close(Statement statement) {
        close(null, statement, null);
    }

    public static void close(ResultSet result) {
        close(null, null, result);
    }

    public static void close(Statement statement, ResultSet result) {
        close(null, statement, result);
    }

    public static void close(Connection connection, Statement statement, ResultSet result) {
        try {
            if (connection != null) connection.close();
            if (statement != null) statement.close();
            if (result != null) result.close();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}
