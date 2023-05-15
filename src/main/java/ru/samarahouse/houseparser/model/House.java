package ru.samarahouse.houseparser.model;

import lombok.*;

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
    boolean groundFloor;
    List<String> tags;
    List<String> features;
    List<String> planUrls;
    List<String> exteriorUrls;

}
