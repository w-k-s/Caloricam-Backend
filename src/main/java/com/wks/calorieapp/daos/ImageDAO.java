package com.wks.calorieapp.daos;

import com.wks.calorieapp.entities.ImageEntry;
import com.wks.calorieapp.utils.DatabaseUtils;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Named
public class ImageDAO {

    private static final String TABLE_IMAGES = "images";
    private static final String COLUMN_IMAGE_ID = "image_id";
    private static final String COLUMN_FOOD_ID = "food_id";
    private static final String COLUMN_SIZE = "size";
    private static final String COLUMN_IS_FINALIZED = "is_finalized";

    private static final String CREATE_QUERY = "INSERT INTO " + TABLE_IMAGES + " (" + COLUMN_IMAGE_ID + ", "
            + COLUMN_FOOD_ID + "," + COLUMN_SIZE + "," + COLUMN_IS_FINALIZED + ") VALUES (?,?,?,?)";
    private static final String READ_QUERY = "SELECT " + COLUMN_IMAGE_ID + ", " + COLUMN_FOOD_ID + ", " + COLUMN_SIZE
            + "," + COLUMN_IS_FINALIZED + " FROM " + TABLE_IMAGES;
    private static final String UPDATE_QUERY = "UPDATE " + TABLE_IMAGES + " SET " + COLUMN_FOOD_ID + "=? , "
            + COLUMN_SIZE + "=?," + COLUMN_IS_FINALIZED + "=? WHERE " + COLUMN_IMAGE_ID + " = ?";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE_IMAGES + " WHERE " + COLUMN_IMAGE_ID + " = ?";
    private static final String FIND_QUERY = "SELECT " + COLUMN_IMAGE_ID + ", " + COLUMN_FOOD_ID + ", " + COLUMN_SIZE
            + "," + COLUMN_IS_FINALIZED + " FROM " + TABLE_IMAGES + " WHERE " + COLUMN_IMAGE_ID + " = ?";

    @Resource(name = "jdbc/main")
    DataSource dataSource;


    public boolean create(ImageEntry image) throws DataAccessObjectException {
        // Connection connection = null;
        PreparedStatement statement = null;
        boolean success = false;

        try {
            statement = dataSource.getConnection().prepareStatement(CREATE_QUERY);
            statement.setString(1, image.getImageId());
            if (image.getFoodId() == 0) statement.setNull(2, java.sql.Types.NULL);
            else
                statement.setLong(2, image.getFoodId());
            statement.setLong(3, image.getSize());
            statement.setBoolean(4, image.isFinalized());
            statement.executeUpdate();
            success = true;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DataAccessObjectException("This image may already exist in the database.", e);
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement);
        }
        return success;

    }

    public List<ImageEntry> read() throws DataAccessObjectException {
        List<ImageEntry> imageItems = new ArrayList<ImageEntry>();
        ImageEntry imageItem = null;
        // Connection connection = null;
        PreparedStatement statement = null;
        ResultSet results = null;

        try {
            statement = dataSource.getConnection().prepareStatement(READ_QUERY);
            statement.execute();
            results = statement.getResultSet();

            while (results.next()) {
                imageItem = new ImageEntry();
                imageItem.setImageId(results.getString(COLUMN_IMAGE_ID));
                imageItem.setFoodId(results.getLong(COLUMN_FOOD_ID));
                imageItem.setSize(results.getLong(COLUMN_SIZE));
                imageItem.setFinalized(results.getBoolean(COLUMN_IS_FINALIZED));
                imageItems.add(imageItem);
            }

        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement, results);
        }

        return imageItems;

    }

    public boolean update(ImageEntry image) throws DataAccessObjectException {
        // Connection connection = null;
        PreparedStatement statement = null;

        try {
            // connection = DatabaseUtils.getConnection();
            statement = dataSource.getConnection().prepareStatement(UPDATE_QUERY);
            if (image.getFoodId() == 0) statement.setNull(1, java.sql.Types.NULL);
            else
                statement.setLong(1, image.getFoodId());
            statement.setLong(2, image.getSize());
            statement.setBoolean(3, image.isFinalized());
            statement.setString(4, image.getImageId());
            statement.execute();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DataAccessObjectException("This image may already exist in the database.", e);
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement);
        }
    }

    public boolean delete(String id) throws DataAccessObjectException {
        PreparedStatement statement = null;

        try {
            statement = dataSource.getConnection().prepareStatement(DELETE_QUERY);
            statement.setString(1, id);
            statement.execute();
            return true;
        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement);
        }
    }

    public ImageEntry find(String id) throws DataAccessObjectException {
        // Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ImageEntry image = null;

        try {
            statement = dataSource.getConnection().prepareStatement(FIND_QUERY);
            statement.setString(1, id);
            statement.execute();

            result = statement.getResultSet();
            if (result.next() && result != null) {
                image = new ImageEntry();
                image.setImageId(result.getString(COLUMN_IMAGE_ID));
                image.setFoodId(result.getLong(COLUMN_FOOD_ID));
                image.setSize(result.getLong(COLUMN_SIZE));
                image.setFinalized(result.getBoolean(COLUMN_IS_FINALIZED));

            }

        } catch (SQLException e) {
            throw new DataAccessObjectException(e);
        } finally {
            DatabaseUtils.close(statement, result);
        }
        return image;

    }

    public void close() {
        try {
            DatabaseUtils.close(dataSource.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}