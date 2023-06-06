package ru.samarahouse.houseparser.service.save;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.samarahouse.houseparser.model.House;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@ToString
public class SaveImages {

    private static final String DIR = "c:" + "/" + "Houses" + "/";
    private static final String DIR1 = "/" + "exterior";
    private static final String DIR2 = "/" + "layout";
    private static String pathname;

    private static String exteriorFirstPartName = "exterior_";

    private static String layoutFirstPartName = "layout_";

    private static final String[] secondPartName = new String[]{"one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten"};      //как выяснилось, Яндекс для SEO не любит числа в url

    private static final String fileFormat = ".jpg";


    private static final String baseUrl = "https://lesstroy63.ru/";
    //ToDo как-то надо сделать изменяемым

    public static House saveListsImages(House house) {
        Integer idProject = house.getId();
        house.setExteriorPath(saveImagesBase(1, idProject, house.getExteriorUrls(), house.getTitleEng()));
        house.setLayoutPath(saveImagesBase(2, idProject, house.getLayoutUrls(), house.getTitleEng()));
        return house;
    }


    public static LinkedList<String> saveImagesBase(int typeOfImage, Integer idProject, LinkedList<String> urls,
                                                    String titleEng) { //typeOfImage - 1-exterior, 2-layout


        LinkedList<String> baseRelativePathnameImages = new LinkedList<>();
        String relativePath = null;
        String firstPartName = null;

        switch (typeOfImage) {
            case 1:
                relativePath = idProject + DIR1;
                firstPartName = exteriorFirstPartName;
                break;
            case 2:
                relativePath = idProject + DIR2;
                firstPartName = layoutFirstPartName;
                break;
        }

        pathname = DIR + relativePath;
        final File dir = new File(pathname);
        addDir(dir);


        for (int i = 0; i < urls.size() && i < secondPartName.length; i++) {
            String imageName = titleEng + "_" + firstPartName + secondPartName[i]
                    + fileFormat;
            String pathnameImage = pathname + "/" + imageName;
            String relativePathnameImage = relativePath + "/" + imageName;
            String url = urls.get(i);
            downloadImage(pathnameImage, url);
            baseRelativePathnameImages.add(relativePathnameImage);
        }

        return baseRelativePathnameImages;

    }


    private static void addDir(File dir) {
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                log.info("Kaтaлoг " + dir.getAbsolutePath()
                        + " ycпeшнo coздaн.");
            } else {
                log.info("Kaтaлoг " + dir.getAbsolutePath()
                        + " coздать нe yдaлocь.");
            }
        } else {
            log.info("Kaтaлoг " + dir.getAbsolutePath()
                    + " yжe cyщecтвyeт.");
        }

    }

    private static void downloadImage(String pathname, String url) {
        url = baseUrl + url;

        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(pathname));
        } catch (IOException e) {
            log.info("Не удалось скачать картинку с url " + url);
        }
    }

}
