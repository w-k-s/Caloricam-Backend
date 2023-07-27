package com.wks.calorieapp.daos;


import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Statement;

@Named
@ApplicationScoped
public class GeneralDAO {
    @Resource(name = "jdbc/main")
    DataSource dataSource;

    public boolean doQuery(String query) throws DataAccessObjectException {
        Statement statement = null;

        try {
            statement = dataSource.getConnection().createStatement();
            return statement.execute(query);
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        }

    }
}
