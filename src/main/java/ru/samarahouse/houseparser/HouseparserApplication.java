package ru.samarahouse.houseparser;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.samarahouse.houseparser.model.House;
import ru.samarahouse.houseparser.service.GetHouseProject;
import ru.samarahouse.houseparser.service.GetUrlsFromFile;
import ru.samarahouse.houseparser.service.SaveImages;
import ru.samarahouse.houseparser.service.SavePage;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
@Slf4j
public class HouseparserApplication {

    private final static SavePage savaPage = new SavePage();
    private final static GetHouseProject getHouseProject = new GetHouseProject();
    private final static GetUrlsFromFile getUrlsFromFile = new GetUrlsFromFile();
    private final static SaveImages saveImages = new SaveImages();
    private static Integer id;

    public static void main(String[] args) throws IOException {
        SpringApplication.run(HouseparserApplication.class, args);


        String link = "https://lesstroy63.ru/proekty/baderim/";
        savaPage.savePage(link);

        if (id == null) {
            id = 2;     // заменить на подтягивание из БД
        } else {
            id++;
        }

        House house = getHouseProject.projectMapper(id);
        log.info(house.toString());
        List<String> exteriorPath = saveImages.saveImagesBase(1, house.getId(), house.getExteriorUrls());
        List<String> planPath = saveImages.saveImagesBase(2, house.getId(), house.getPlanUrls());

    }

}
