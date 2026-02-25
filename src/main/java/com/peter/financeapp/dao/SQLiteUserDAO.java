package com.peter.financeapp.dao;

import com.peter.financeapp.model.User;
import com.peter.financeapp.repository.UserRepository;
import com.peter.financeapp.util.DButil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteUserDAO implements UserRepository {
    public void save(User user){
        String sql = """
                INSERT INTO users(
                username,password
                ) VALUES (?,?)
                """;
        try (Connection connection = DButil.getConnection();
             PreparedStatement prs = connection.prepareStatement(sql)){
            prs.setString(1, user.getUsername());
            prs.setString(2, user.getPassword());

            prs.executeUpdate();

            ResultSet key = prs.getGeneratedKeys();
            if (key.next()){
                user.setId(key.getInt(1));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Database Error",e);
        }
    }
    public User findByUsername(String username){
        String sql = """
                SELECT * FROM users WHERE username =?
                """;
        try (Connection connection = DButil.getConnection();
        PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setString(1,username);

            ResultSet rs = prs.executeQuery();
            if (rs.next()){
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch user "+e);
        }
    }
}
