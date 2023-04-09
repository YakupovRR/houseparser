package ru.samarahouse.houseparser.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetUrlsFromFile {
    private static final File file = new File("url.csv");

    public static List<String> getUrls() throws IOException {
        List<String> urls = new ArrayList();

        BufferedReader csvReader = new BufferedReader(new FileReader(file));
        String row;
        while ((row = csvReader.readLine()) != null) {
            urls.add(row);
        }
        csvReader.close();
        System.out.println("Получили список url размером " + urls.size());
        System.out.println("Первая " + urls.get(0));
        System.out.println("Последняя " + urls.get((urls.size() - 1)));
        return urls;
    }


}
