package com.xiaopeng.server.app.service;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @Auto:BUGPeng
 * @Date:2022/6/10 9:50
 * @ClassName:JDBCRunSqlTest
 * @Remark:  使用jdbc连接直接操作数据库
 */
public class JDBCUtil {
        private static Connection connection;
        public static void main(String[] args) throws SQLException {
            try {
                openDatabaseConnection();
                //插入数据
                System.out.println("=====插入数据开始====");
                createData(1L,"Java");
                createData(2L,"JavaScript");
                createData(3L,"C++");
                //插入数据结束
                System.out.println("=====插入数据结束====");
            } finally {
                closeDatabaseConnection();
            }
        }

    /**
     * 插入数据
     * @param id
     * @param name
     * @throws SQLException
     */
        //createData（）方法具体实现
        private static void createData(Long id, String name) throws SQLException {
            System.out.println("Create data: " );
            try (PreparedStatement statement = connection.prepareStatement(
                    "    INSERT INTO test(id, name)\n" + "    VALUES (?, ?)\n")) {
                statement.setLong(1, id);
                statement.setString(2, name);
                int rowsInserted = statement.executeUpdate();
                System.out.println("Rows inserted: " + rowsInserted);
            }
        }

    /**
     * 打开数据库连接
     * @throws SQLException
     */
    private static void openDatabaseConnection() throws SQLException{
            System.out.println("正在打开数据库连接....");

            connection = DriverManager.getConnection(
                    "jdbc:mysql://124.221.225.23:3306/test?useUnicode=true&characterEncoding=utf8&useSSL=false",
                    "root", "root"
            );

            System.out.println("Connection valid: " + connection.isValid(5));
        }

    /**
     * 关闭数据库连接
     * @throws SQLException
     */
    private static void closeDatabaseConnection() throws SQLException {
            connection.close();
            System.out.println("Connection valid: " + connection.isValid(5));
        }

    }
