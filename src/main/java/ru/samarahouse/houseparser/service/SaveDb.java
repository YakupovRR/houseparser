package ru.samarahouse.houseparser.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.samarahouse.houseparser.model.House;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class SaveDb {

    private JdbcTemplate jdbcTemplate;

    public SaveDb(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

     public boolean  addToDb(House house) throws Exception {


      /*
        Connection connection = null;

        String insertQuery = "INSERT INTO PROJECTS(id, title, description, square, rooms, width, length," +
                "floors, groundFloor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(insertQuery);
        statement.setInt(1, house.getId());
        statement.setString(2, house.getTitle());
        statement.setString(3, house.getDescription());
        statement.setDouble(4, house.getSquare());
        statement.setInt(5, house.getRooms());
        statement.setDouble(6, house.getWidth());
        statement.setString(7, String.valueOf(house.getFloors()));
        statement.setBoolean(8, house.isGroundFloor());

        int count = statement.executeUpdate();
        return count > 0;
   */



        String sql = "INSERT INTO PROJECTS(id, title, description, square, rooms, width, length," +
                "floors, groundFloor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, house.getId());
            statement.setString(2, house.getTitle());
            statement.setString(3, house.getDescription());
            statement.setDouble(4, house.getSquare());
            statement.setInt(5, house.getRooms());
            statement.setDouble(6, house.getWidth());
            statement.setString(7, String.valueOf(house.getFloors()));
            statement.setBoolean(8, house.isGroundFloor());
            return statement;
        }, keyHolder);
        return true;
    }
}
