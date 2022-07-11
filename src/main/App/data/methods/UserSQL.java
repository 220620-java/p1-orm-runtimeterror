package main.BankApp.data.methods;

import main.BankApp.data.interfaces.UserDao;
import main.BankApp.models.Account;
import main.BankApp.models.User;
import main.BankApp.utils.ConnectUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSQL implements UserDao {
    private ConnectUtil connUtil = ConnectUtil.getConnectUtil();

    @Override
    public User create(User user) {
        try (Connection conn = connUtil.getConnection()) {
            conn.setAutoCommit(false);

            String sql = "insert into users" + "(id, full_name, username, password)" + "values (default, ?, ?, ?)";
            String[] keys = {"id"};

            PreparedStatement statement = conn.prepareStatement(sql, keys);
            statement.setString(1, user.getName());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());

            int rowsAffected = statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(resultSet.next() && rowsAffected == 1) {
                user.setId(resultSet.getInt("id"));
                conn.commit();
            } else {
                conn.rollback();
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User findByUsername(String username) {
        User user = null;

        try (Connection conn = connUtil.getConnection()) {
            conn.setAutoCommit(false);

            String sql = "select * from users where username = ?;";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String setUsername = resultSet.getString("username");
                String setPassword = resultSet.getString("password");

                user = new User(setUsername, setPassword);
            } else {
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public User getUserInfo(User user) {
        String username = user.getUsername();
        try (Connection conn = connUtil.getConnection()) {
            conn.setAutoCommit(false);

            String sql = "select * from users where username = ?;";

            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int setId = resultSet.getInt("id");
                String setName = resultSet.getString("full_name");
                String setUsername = resultSet.getString("username");
                String setPassword = resultSet.getString("password");

                user = new User(setId, setName, setUsername, setPassword);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
