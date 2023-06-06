package ru.samarahouse.houseparser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import ru.samarahouse.houseparser.model.House;
import ru.samarahouse.houseparser.service.*;
import ru.samarahouse.houseparser.service.db.HouseDb;
import ru.samarahouse.houseparser.service.save.SaveImages;
import ru.samarahouse.houseparser.service.save.SavePage;

import java.util.ArrayList;

@SpringBootApplication
@Slf4j
@Service
public class HouseparserApplication {

    private static SavePage savaPage = new SavePage();
    private static HouseMapper houseMapper = new HouseMapper();
    private static GetUrlsFromFile getUrlsFromFile = new GetUrlsFromFile();
    private static SaveImages saveImages = new SaveImages();
    private static Integer id;
    private static HouseDb houseDb = new HouseDb();


    public static void main(String[] args) throws Exception {
        SpringApplication.run(HouseparserApplication.class, args);


        id = houseDb.getLastProjectId() + 1;
        ArrayList<String> links = getUrlsFromFile.getUrls();

        //  String iii = "https://lesstroy63.ru/proekty/maksim/";


        try {
            for (String i : links) {
                log.info("Начинаем парсинг проекта по адресу " + i);
                savaPage.savePage(i);
                House house = houseMapper.projectMapper(id);
                //заплатка, когда нормально прописаны url и лучше вытянуть английское название оттуда
                try {
                    house.setTitleEng(houseMapper.getTitleEngFromUrl(i));
                } catch (NullPointerException e) {
                }

                house = saveImages.saveListsImages(house);
                log.info("Дом перед сохранением в БД " + house.toString());
                houseDb.saveProjectDb(house);
                id++;
            }
        } catch (NullPointerException e) {
            log.info("Парсинг не начался, возможно лист url пуст");
        }
        log.info("Закончили парсинг списка");
    }
}
