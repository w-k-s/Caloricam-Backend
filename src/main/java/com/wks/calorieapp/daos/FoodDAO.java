package com.wks.calorieapp.daos;

import com.wks.calorieapp.entities.FoodEntry;
import com.wks.calorieapp.utils.DatabaseUtils;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class FoodDAO {

    private static final String TABLE_FOODS = "foods";
    private static final String COLUMN_FOOD_ID = "food_id";
    private static final String COLUMN_FOOD_NAME = "name";

    private static final String CREATE_QUERY = "INSERT INTO " + TABLE_FOODS + " ( " + COLUMN_FOOD_NAME + ") VALUES (?)";
    private static final String READ_QUERY = "SELECT " + COLUMN_FOOD_ID + ", " + COLUMN_FOOD_NAME + " FROM "
            + TABLE_FOODS;
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_FOODS + " SET " + COLUMN_FOOD_NAME + "=? WHERE "
            + COLUMN_FOOD_ID + " = ?";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE_FOODS + " WHERE " + COLUMN_FOOD_ID + " = ?";
    private static final String FIND_BY_ID_QUERY = "SELECT " + COLUMN_FOOD_ID + ", " + COLUMN_FOOD_NAME + " FROM "
            + TABLE_FOODS + " WHERE " + COLUMN_FOOD_ID + " = ?";

    private static final String FIND_BY_NAME_QUERY = "SELECT " + COLUMN_FOOD_ID + ", " + COLUMN_FOOD_NAME + " FROM "
            + TABLE_FOODS + " WHERE " + COLUMN_FOOD_NAME + " = ?";


    @Resource(name = "jdbc/main")
    DataSource dataSource;

    public long create(FoodEntry item) throws DataAccessObjectException {
        // Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = dataSource.getConnection().prepareStatement(CREATE_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, item.getName());
            statement.execute();

            result = statement.getGeneratedKeys();
            if (result != null && result.next()) {
                return result.getLong(1);
            } else
                return -1L;
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement, result);
        }

    }

    public List<FoodEntry> read() throws DataAccessObjectException {
        // Connection connection = null;
        PreparedStatement statement = null;
        ResultSet results = null;

        List<FoodEntry> foodItems = new ArrayList<FoodEntry>();
        FoodEntry foodItem = null;

        try {
            // connection = DatabaseUtils.getConnection();
            statement = dataSource.getConnection().prepareStatement(READ_QUERY);
            statement.execute();

            results = statement.getResultSet();
            while (results != null && results.next()) {
                foodItem = new FoodEntry();
                foodItem.setFoodId(results.getLong(COLUMN_FOOD_ID));
                foodItem.setName(results.getString(COLUMN_FOOD_NAME));
                foodItems.add(foodItem);
            }
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement, results);
        }

        return foodItems;

    }

    public FoodEntry read(long id) throws DataAccessObjectException {
        PreparedStatement statement = null;
        ResultSet result = null;
        FoodEntry foodItem = null;

        try {

            statement = dataSource.getConnection().prepareStatement(FIND_BY_ID_QUERY);
            statement.setLong(1, id);
            statement.execute();

            result = statement.getResultSet();
            if (result != null && result.next()) {
                foodItem = new FoodEntry();
                foodItem.setFoodId(result.getLong(COLUMN_FOOD_ID));
                foodItem.setName(result.getString(COLUMN_FOOD_NAME));
            }
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement, result);
        }

        return foodItem;
    }

    public FoodEntry read(String name) throws DataAccessObjectException {
        PreparedStatement statement = null;
        ResultSet result = null;
        FoodEntry foodItem = null;

        try {
            statement = dataSource.getConnection().prepareStatement(FIND_BY_NAME_QUERY);
            statement.setString(1, name);
            statement.execute();

            result = statement.getResultSet();
            if (result != null && result.next()) {
                foodItem = new FoodEntry();
                foodItem.setFoodId(result.getLong(COLUMN_FOOD_ID));
                foodItem.setName(result.getString(COLUMN_FOOD_NAME));
            }
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement, result);
        }

        return foodItem;
    }

    public boolean update(FoodEntry item) throws DataAccessObjectException {
        // Connection connection = null;
        PreparedStatement statement = null;

        try {
            // connection = DatabaseUtils.getConnection();
            statement = dataSource.getConnection().prepareStatement(UPDATE_QUERY);
            statement.setString(1, item.getName());
            statement.setLong(2, item.getFoodId());
            statement.execute();
            return true;
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement);
        }

    }

    public boolean delete(FoodEntry entry) throws DataAccessObjectException {
        // Connection connection = null;
        PreparedStatement statement = null;

        try {
            // connection = DatabaseUtils.getConnection();
            statement = dataSource.getConnection().prepareStatement(DELETE_QUERY);
            statement.setLong(1, entry.getFoodId());
            statement.execute();
            return true;
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement);
        }

    }


    public void close() {
        try {
            DatabaseUtils.close(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
