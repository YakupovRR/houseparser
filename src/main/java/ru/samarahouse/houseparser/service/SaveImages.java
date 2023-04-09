package ru.samarahouse.houseparser.service;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ToString
public class SaveImages {

    private static final String DIR = "c:" + "/" + "Houses" + "/";
    private static final String DIR1 = "/" + "exterior";
    private static final String DIR2 = "/" + "plan";
    private static String pathname;

    private static String exteriorFirstPartName = "exterior_";

    private static String planFirstPartName = "plan_";

    private static final String[] secondPartName = new String[]{"one", "two", "three", "four", "five",
            "six", "seven"};      //как выяснилось, Яндекс для SEO не любит числа в url

    private static final String fileFormat = ".jpg";


    public static List<String> saveImagesBase(int type, Integer id, List<String> urls) {

        List<String> basePathnameImages = new ArrayList<>();

        switch (type) {
            case 1:
                pathname = DIR + id + DIR1;
                final File dir1 = new File(pathname);
                addDir(dir1);
                break;
            case 2:
                pathname = DIR + id + DIR2;
                final File dir2 = new File(pathname);
                addDir(dir2);
                break;
        }
        for (int i = 0; i < urls.size() && i < secondPartName.length; i++) {
            String pathnameImage = pathname + "/" + exteriorFirstPartName + secondPartName[i] + fileFormat;
            String url = urls.get(i);
            downloadImage(pathnameImage, url);
            basePathnameImages.add(pathnameImage);
        }
        return basePathnameImages;
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
        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, Paths.get(pathname));
        } catch (IOException e) {
            // handle IOException
        }
    }

}
