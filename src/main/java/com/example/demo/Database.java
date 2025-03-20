package com.example.demo;
import java.sql.*;
public class Database {
//    private static final String URL = "jdbc:postgresql://host.docker.internal:5432/postgres";
//    private static final String USER = "postgres";
//    private static final String PASSWORD = "mysecretpassword";
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USER, PASSWORD);
//    }
//
//    public static void main(String[] args) {
//        // Подключение к базе данных
//        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
//            System.out.println("Соединение с базой данных успешно установлено!");
//
//            // Выполнение SQL-запроса
//            String query = "SELECT version();"; // Простой запрос для проверки работы
//            try (Statement statement = connection.createStatement();
//                 ResultSet resultSet = statement.executeQuery(query)) {
//
//                // Обработка результатов
//                while (resultSet.next()) {
//                    String dbVersion = resultSet.getString(1);
//                    System.out.println("Версия базы данных: " + dbVersion);
//                }
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Ошибка при подключении к базе данных:");
//            e.printStackTrace();
//        }
//    }
}
