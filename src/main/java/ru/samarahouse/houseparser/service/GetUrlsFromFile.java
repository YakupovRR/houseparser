package ru.samarahouse.houseparser.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetUrlsFromFile {
    private static final File file = new File("url.csv");

    public static ArrayList<String> getUrls() throws IOException {
        ArrayList<String> urls = new ArrayList();

        BufferedReader csvReader = new BufferedReader(new FileReader(file));
        String row;
        while ((row = csvReader.readLine()) != null) {
            urls.add(row);
        }
        csvReader.close();
        return urls;
    }


}
