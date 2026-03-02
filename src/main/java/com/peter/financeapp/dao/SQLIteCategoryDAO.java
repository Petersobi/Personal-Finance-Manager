package com.peter.financeapp.dao;

import com.peter.financeapp.model.Category;
import com.peter.financeapp.model.CategoryType;
import com.peter.financeapp.repository.CategoryRepository;
import com.peter.financeapp.util.DButil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLIteCategoryDAO implements CategoryRepository {
    @Override
    public void save(Category category) {
        String sql = """
                INSERT INTO categories(
                user_id,name,type,is_delected)
                VALUES (?,?,?,0);
                """;
        try(Connection connection = DButil.getConnection();
            PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,category.getUserId());
            prs.setString(2,category.getName());
            prs.setString(3,category.getType().name());

            prs.executeUpdate();

            ResultSet keys = prs.getGeneratedKeys();
            if(keys.next()){
                category.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error Saving Category",e);
        }
    }

    @Override
    public List<Category> findByUserID(Long userId) {
        String sql = """
                SELECT * FROM categories
                WHERE user_id = ? AND is_deleted = 0
                """;
        List<Category> categories = new ArrayList<>();
        try (Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,userId);

            ResultSet rs = prs.executeQuery();
            while ((rs.next())){
                categories.add(mapRow(rs));
            }
            return categories;
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching Categories",e);
        }

    }

    @Override
    public Category findById(Long id) {
        String sql = """
                SELECT * FROM categories WHERE
                id = ? AND is_deleted = 0
                """;
        try (Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,id);

            ResultSet rs = prs.executeQuery();

            if (rs.next()){
                return mapRow(rs);
            }
            return null;
        }
        catch (SQLException e) {
            throw new DataAccessException("Error fetching Category",e);
        }

    }

    @Override
    public void softDelete(Long categoryID) {
        String sql = """
                UPDATE categories SET is_deleted = 1
                WHERE id = ?
                """;
        try (Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,categoryID);

            prs.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Error deleting Category",e);
        }
    }

    private Category mapRow(ResultSet rs) throws SQLException{
        return new Category(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("name"),
                CategoryType.valueOf(rs.getString("type")),
                rs.getInt("is_deleted")==1
        );
    }
}
