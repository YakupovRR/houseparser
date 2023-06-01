package ru.samarahouse.houseparser.model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
public class House {
    @NonNull
    Integer id;
    @NonNull
    String title;
    String titleEng;
    String description;
    @NonNull
    Double square;
    @NonNull
    Integer rooms;
    @NonNull
    Double width;
    @NonNull
    Double length; // вглубь участка
    @NonNull
    Floors floors;
    @Builder.Default
    boolean groundFloor = false;
    List<String> tags;
    List<String> features;
    LinkedList<String> layoutUrls;  //url, откуда изображение было скачено
    LinkedList<String> exteriorUrls;
    LinkedList<String> layoutPath;
    LinkedList<String> exteriorPath;
}
