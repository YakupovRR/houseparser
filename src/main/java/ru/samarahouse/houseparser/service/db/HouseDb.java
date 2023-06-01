package ru.samarahouse.houseparser.service.db;

import lombok.extern.slf4j.Slf4j;
import ru.samarahouse.houseparser.model.Floors;
import ru.samarahouse.houseparser.model.House;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


// ToDo: создать таблицу с ошибками (не совпадает кол-во картинок)
/*


 */
@Slf4j
public class HouseDb {

    private DataSource dataSource;

    public void saveProjectDb(House house) throws SQLException {


        //начало тестовой части
       /*
        House house = new House();
        List<String> testTags = new ArrayList<>();
        List<String> testFeatures = new ArrayList<>();
        LinkedList<String> testLayoutPath = new LinkedList<>();
        LinkedList<String> testExteriorPath = new LinkedList<>();

        testTags.add("тег 1");
        testTags.add("тег 2");
        testFeatures.add("фича 1");
        testFeatures.add("фича 2");

        house.setId(getLastProjectId() + 1);
        house.setTitle("Пробный");
        house.setTitleEng("Proba");
        house.setDescription("Описание");
        house.setSquare(100.2);
        house.setRooms(4);
        house.setWidth(10.12);
        house.setLength(10.0);
        house.setFloors(Floors.ONEPLUSMANSARD);
        house.setTags(testTags);
        house.setFeatures(testFeatures);
        house.setGroundFloor(true);


        testExteriorPath.add(house.getId() + "/" + house.getTitleEng() + "_exterior_1");
        testExteriorPath.add(house.getId() + "/" + house.getTitleEng() + "/_exterior_2");
        testExteriorPath.add(house.getId() + "/" + house.getTitleEng() + "/_exterior_3");

        testLayoutPath.add(house.getId() + "/" + house.getTitleEng() + "/_layout_1");
        testLayoutPath.add(house.getId() + "/" + house.getTitleEng() + "/_layout_2");
        testLayoutPath.add(house.getId() + "/" + house.getTitleEng() + "/_layout_3");
        // testLayoutPath.add(house.getTitleEng() + "/4_layout_4");

        try {
            log.info("Размер листа экстерьеров " + testExteriorPath.size());
            house.setExteriorPath(testExteriorPath);
        } catch (NullPointerException e) {
            log.info("Не удалось записать лист этажей в проект дома, возможно он null");
        }

        try {
            log.info("Размер листа этажей " + testLayoutPath.size());
            house.setLayoutPath(testLayoutPath);
        } catch (NullPointerException e) {
            log.info("Не удалось записать лист этажей в проект дома, возможно он null");
        }
*/
        // конец тестовой части


        String sqlHouse = "INSERT INTO schema.project (projectId, title, titleeng, description, square, rooms, width," +
                " length, floors, tags, features) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlExterior = "INSERT INTO schema.exterior (projectId, path, base) VALUES (?, ?, ?)";
        String sqlLayout = "INSERT INTO schema.layout (projectId, path, floor) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statementHouse = connection.prepareStatement(sqlHouse);
             PreparedStatement statementExterioir = connection.prepareStatement(sqlExterior);
             PreparedStatement statementLayout = connection.prepareStatement(sqlLayout);
        ) {

            //Запись в БД самого проекта
            statementHouse.setInt(1, house.getId());
            statementHouse.setString(2, house.getTitle());
            statementHouse.setString(3, house.getTitleEng());
            statementHouse.setString(4, house.getDescription());
            statementHouse.setDouble(5, house.getSquare());
            statementHouse.setInt(6, house.getRooms());
            statementHouse.setDouble(7, house.getWidth());
            statementHouse.setDouble(8, house.getLength());
            statementHouse.setString(9, String.valueOf(house.getFloors()));
            statementHouse.setString(10, String.valueOf(house.getTags()));
            statementHouse.setString(11, String.valueOf(house.getFeatures()));
            statementHouse.executeUpdate();


            // Запись в БД экстерьеров
            try {
                boolean isBaseExterior = true;   // предполагается, что основная картинка идет первой
                for (int i = 0; i < house.getExteriorPath().size(); i++) {
                    statementExterioir.setInt(1, house.getId());
                    statementExterioir.setString(2, house.getExteriorPath().get(i));
                    statementExterioir.setBoolean(3, isBaseExterior);
                    statementExterioir.executeUpdate();
                    isBaseExterior = false;
                }
            } catch (NullPointerException e) {
                log.info("Проект id " + house.getId() + " Не удалось записать лист экстерьеров в БД, возможно он null");
            }


            // Запись в БД планировок
            try {
                int minusGroundFloor = 0;
                if (house.isGroundFloor()) minusGroundFloor = 1;
                if (isCorrectLengthListLayout(house)) {
                    log.info("Количество картинок в листе корректно");
                    for (int i = 0; i < (house.getLayoutPath().size() - minusGroundFloor); i++) {
                        statementLayout.setInt(1, house.getId());
                        statementLayout.setString(2, house.getLayoutPath().get(i));
                        statementLayout.setString(3, getFloorByNumber(i, house.getFloors()));
                        statementLayout.executeUpdate();
                    }
                    if (house.isGroundFloor()) {
                        statementLayout.setInt(1, house.getId());
                        statementLayout.setString(2, house.getLayoutPath().get(
                                (house.getLayoutPath().size() - 1)));
                        statementLayout.setString(3, String.valueOf(Floors.BASEMENT));
                        statementLayout.executeUpdate();
                    }
                } else {
                    log.info("Количество картинок в листе НЕ корректно");
                    for (int i = 0; i < house.getLayoutPath().size(); i++) {
                        statementLayout.setInt(1, house.getId());
                        statementLayout.setString(2, house.getLayoutPath().get(i));
                        statementLayout.setString(3, "unknown");  //потом править только ручками
                        statementLayout.executeUpdate();
                    }
                }
            } catch (NullPointerException e) {
                log.info("Проект id " + house.getId() + "Не удалось записать лист этажей в БД, возможно он null");
            }
        }
    }

    public Integer getLastProjectId() throws SQLException {
        Integer lastProjectId = 0;
        String sqlRequestId = "SELECT MAX(projectId) FROM schema.project";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statementId = connection.prepareStatement(sqlRequestId);
             ResultSet resultSet = statementId.executeQuery()) {
            if (resultSet.next()) {
                lastProjectId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastProjectId;
    }

    private boolean isCorrectLengthListLayout(House house) {
        int plusGroundFloor = 0;
        log.info("Значение  house.isGroundFloor() = " + house.isGroundFloor());
        if (house.isGroundFloor()) {
            plusGroundFloor = 1;
        }
        log.info("Переменная plusGroundFloor = " + plusGroundFloor);
        int expectedListLength = -1;

        switch (house.getFloors()) {
            case ONE:
                expectedListLength = (1 + plusGroundFloor);
                break;
            case TWO:
                expectedListLength = (2 + plusGroundFloor);
                break;
            case ONEPLUSMANSARD:
                expectedListLength = (2 + plusGroundFloor);
                break;
            case THERE:
                expectedListLength = (3 + plusGroundFloor);
                break;
            case TWOPLUSMANSARD:
                expectedListLength = (3 + plusGroundFloor);
                break;
        }

        log.info("Ожидаемый размер листа = " + expectedListLength);


        return (expectedListLength == house.getLayoutPath().size());

    } // что у двухэтажного дома будет 2 картинки и т.п.

    private String getFloorByNumber(int i, Floors floor) {
        String floorString = null;
        switch (i) {
            case 0:
                floorString = String.valueOf(Floors.ONE);
                break;
            case 1:
                if (floor.equals(Floors.ONEPLUSMANSARD)) {
                    floorString = String.valueOf(Floors.ONEPLUSMANSARD);
                } else {
                    floorString = String.valueOf(Floors.TWO);
                }
                break;
            case 2:
                if (floor.equals(Floors.TWOPLUSMANSARD)) {
                    floorString = String.valueOf(Floors.TWOPLUSMANSARD);
                } else {
                    floorString = String.valueOf(Floors.THERE);
                }
                break;
        }
        return floorString;
    }  // получение название этажа по порядковому номеру картинки
}









