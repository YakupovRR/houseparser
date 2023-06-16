package ru.samarahouse.houseparser.service.mapper;

import ru.samarahouse.houseparser.model.House;

public interface HouseMapper {

    House projectMapper(Integer id, String url);

}
