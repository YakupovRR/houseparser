package ru.samarahouse.houseparser;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import ru.samarahouse.houseparser.model.House;
import ru.samarahouse.houseparser.service.*;
import ru.samarahouse.houseparser.service.db.HouseDb;
import ru.samarahouse.houseparser.service.mapper.HouseMapper;
import ru.samarahouse.houseparser.service.mapper.HouseMapperLesstroy;
import ru.samarahouse.houseparser.service.save.SaveImages;
import ru.samarahouse.houseparser.service.save.SavePage;

import java.util.ArrayList;

@SpringBootApplication
@Slf4j
@Service
public class HouseparserApplication {

    private static SavePage savaPage = new SavePage();
    private static HouseMapperLesstroy houseMapper = new HouseMapperLesstroy();
    private static GetUrlsFromFile getUrlsFromFile = new GetUrlsFromFile();
    private static SaveImages saveImages = new SaveImages();
    private static Integer id;
    private static HouseDb houseDb = new HouseDb();


    public static void main(String[] args) throws Exception {
        SpringApplication.run(HouseparserApplication.class, args);

        // TODO: сделать подключение к БД (логи, пароль и т.д.) через app.pro
        // TODO: сделать маппер интерфейсом, добавить имплементацию ручного добавления
        // TODO: сохранять урлы в БД и парсить оттуда?


        id = houseDb.getLastProjectId() + 1;

        ArrayList<String> links = getUrlsFromFile.getUrls();

        try {
            for (String i : links) {
                try {
                    log.info("Начинаем парсинг проекта по адресу " + i);
                    savaPage.savePage(i);
                } catch (FailingHttpStatusCodeException e) {
                    log.warn("Ошибка при сохранении страницы " + i + ": " + e.getMessage());
                    continue;
                }
                try {
                    House house = houseMapper.projectMapper(id, i);
                    house = saveImages.saveListsImages(house);
                    log.info("Дом перед сохранением в БД " + house);
                    houseDb.saveProjectDb(house);
                } catch (Exception e) {
                    log.warn("Ошибка при сохранении данных из страницы " + i + ": " + e.getMessage());
                    continue;
                }
                id++;
            }
        } catch (NullPointerException e) {
            log.info("Лист url пуст или закончился");
        }
    }
}
