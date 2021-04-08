package com.imooc.jvm.objectpool.commonspool.jdbc;

import java.sql.*;

/**
 * JDBC测试
 */
public class JDBCTest {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/foodie_shop_dev?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true",
                "root",
                "root");
        // PreparedStatement可以防止SQL注入, Statement则不能
        PreparedStatement preparedStatement = connection.prepareStatement(
                "select * from foodie_shop_dev.orders;"
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.err.println(resultSet.getString("id"));
            System.err.println(resultSet.getString("user_id"));
        }
        resultSet.close();
        preparedStatement.close();

        // PreparedStatement可以防止SQL注入, Statement则不能
        PreparedStatement preparedStatement2 = connection.prepareStatement(
                "select * from foodie_shop_dev.items;"
        );
        ResultSet resultSet2 = preparedStatement2.executeQuery();
        while (resultSet2.next()) {
            System.err.println(resultSet2.getString(1));
            System.err.println(resultSet2.getString(2));
        }
        resultSet2.close();
        preparedStatement2.close();

        // 一个connection可以被多个preparedStatement和resultSet共享
        connection.close();
    }
}
