package ru.samarahouse.houseparser.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Exterior {
    Integer imageId;
    Integer projectId;
    String path;  //относительный путь
    @Builder.Default
    Boolean base = false;
}
