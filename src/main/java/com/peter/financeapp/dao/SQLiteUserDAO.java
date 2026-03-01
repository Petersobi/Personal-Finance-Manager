package com.peter.financeapp.dao;

import com.peter.financeapp.model.User;
import com.peter.financeapp.repository.UserRepository;
import com.peter.financeapp.util.DButil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SQLiteUserDAO implements UserRepository {
    public void save(User user){
        String sql = """
                INSERT INTO users(
                username,password,created_at
                ) VALUES (?,?,?)
                """;
        try (Connection connection = DButil.getConnection();
             PreparedStatement prs = connection.prepareStatement(sql)){
            prs.setString(1, user.getUsername());
            prs.setString(2, user.getPassword());
            prs.setString(3,user.getCreatedAt().toString());

            prs.executeUpdate();

            ResultSet key = prs.getGeneratedKeys();
            if (key.next()){
                user.setId(key.getLong(1));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error saving user",e);
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
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        LocalDate.parse(rs.getString("created_at"))
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding user", e);
        }
    }
    public User findById(long id){
        String sql = """
                SELECT * FROM users WHERE id =?
                """;
        try (Connection connection = DButil.getConnection();
             PreparedStatement prs = connection.prepareStatement(sql)) {
            prs.setLong(1,id);

            ResultSet rs = prs.executeQuery();
            if (rs.next()){
                return new User(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        LocalDate.parse(rs.getString("created_at"))
                );
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Error fetching user",e);
        }
    }
}
