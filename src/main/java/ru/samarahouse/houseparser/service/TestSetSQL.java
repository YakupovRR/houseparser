package ru.samarahouse.houseparser.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.samarahouse.houseparser.model.House;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TestSetSQL {

    static String url = "jdbc:postgresql://localhost:5432/cottages";
    static String username = "admin";
    static String password = "admin";

    public static void saveProjectDb() throws SQLException {
        House testHouse = new House();
        testHouse.setId(1);
        testHouse.setTitle("Пробный");
        testHouse.setTitleEng("Proba");
        testHouse.setDescription("Описание");
        testHouse.setSquare(100.2);
        testHouse.setWidth(10.12);
        testHouse.setLength(10.0);


        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO schema.project (id, title, " +
                    "title_eng, description, square) VALUES (?, ?, ?, ?, ?)");
            statement.setInt(1, testHouse.getId());
            statement.setString(2, testHouse.getTitle());
            statement.setString(3, testHouse.getTitleEng());
            statement.setString(4, testHouse.getDescription());
            statement.setDouble(5, testHouse.getSquare());
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void saveImageDb() throws SQLException {

        int projectId = 1;


        List<String> images = new ArrayList<>();
        images.add("картинка 1");
        images.add("картинка 2");
        images.add("картинка 3");


        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO schema.image (project_id, path)" +
                    " VALUES (?, ?)");
            statement.setInt(1,1);
            statement.setString(2, "путь 1");
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

// добавить таблицу с картинками этаже, дописать класс
    // подумать как будет сохраняться адрес пути при сохранении картинки


}
