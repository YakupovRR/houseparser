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

@SpringBootApplication
@Slf4j
@Service
public class HouseparserApplication {

    private final static SavePage savaPage = new SavePage();
    private static HouseMapper houseMapper = new HouseMapper();
    private final static GetUrlsFromFile getUrlsFromFile = new GetUrlsFromFile();
    private final static SaveImages saveImages = new SaveImages();
    private static Integer id;
    private static HouseDb houseDb = new HouseDb();


    public static void main(String[] args) throws Exception {
        SpringApplication.run(HouseparserApplication.class, args);


        Integer id = houseDb.getLastProjectId() + 1;
        String link = "https://lesstroy63.ru/proekty/maksim/";
        savaPage.savePage(link);
        House house = houseMapper.projectMapper(id);
        houseDb.saveProjectDb(house);




        // log.info(house.toString());
        /*
        List<String> exteriorPath = saveImages.saveImagesBase(1, house.getId(), house.getExteriorUrls());
        List<String> planPath = saveImages.saveImagesBase(2, house.getId(), house.getPlanUrls());



        if (saveDb.addToDb(house)) {
            log.info("Запись в БД прошла успешно");
        } else {
            log.info("Ошибка при записи проекта в БД");

        }
*/

    }

}
