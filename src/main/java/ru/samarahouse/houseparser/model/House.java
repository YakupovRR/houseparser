package ru.samarahouse.houseparser.model;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@ToString
public class House {
    Integer id;
    String title;
    String titleEng;
    String description;
    Double square;
    Integer rooms;
    Double width;
    Double length; // вглубь участка
    Floors floors;
    List<String> tags;
    List<String> tagsId;
    List<String> features;
    LinkedList<String> layoutUrls;  //url, откуда изображение было скачено
    LinkedList<String> exteriorUrls;
    LinkedList<String> layoutPath;
    LinkedList<String> exteriorPath;
    //тэги, которые могут повлиять на кол-во картинок или что-то ещё
    @Builder.Default
    boolean groundFloor = false;
    @Builder.Default
    boolean operatedRoof = false;

}
