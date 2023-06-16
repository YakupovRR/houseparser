package ru.samarahouse.houseparser;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HouseparserApplicationTests {

	@Test
	void contextLoads() {
	}

       /*
        House house = new House();
        List<String> testTags = new ArrayList<>();
        List<String> testFeatures = new ArrayList<>();
        LinkedList<String> testLayoutPath = new LinkedList<>();
        LinkedList<String> testExteriorPath = new LinkedList<>();

        testTags.add("тег 1");
        testTags.add("тег 2");
        testFeatures.add("фича 1");
        testFeatures.add("фича 2");

        house.setId(getLastProjectId() + 1);
        house.setTitle("Пробный");
        house.setTitleEng("Proba");
        house.setDescription("Описание");
        house.setSquare(100.2);
        house.setRooms(4);
        house.setWidth(10.12);
        house.setLength(10.0);
        house.setFloors(Floors.ONEPLUSMANSARD);
        house.setTags(testTags);
        house.setFeatures(testFeatures);
        house.setGroundFloor(true);


        testExteriorPath.add(house.getId() + "/" + house.getTitleEng() + "_exterior_1");
        testExteriorPath.add(house.getId() + "/" + house.getTitleEng() + "/_exterior_2");
        testExteriorPath.add(house.getId() + "/" + house.getTitleEng() + "/_exterior_3");

        testLayoutPath.add(house.getId() + "/" + house.getTitleEng() + "/_layout_1");
        testLayoutPath.add(house.getId() + "/" + house.getTitleEng() + "/_layout_2");
        testLayoutPath.add(house.getId() + "/" + house.getTitleEng() + "/_layout_3");
        // testLayoutPath.add(house.getTitleEng() + "/4_layout_4");

        try {
            log.info("Размер листа экстерьеров " + testExteriorPath.size());
            house.setExteriorPath(testExteriorPath);
        } catch (NullPointerException e) {
            log.info("Не удалось записать лист этажей в проект дома, возможно он null");
        }

        try {
            log.info("Размер листа этажей " + testLayoutPath.size());
            house.setLayoutPath(testLayoutPath);
        } catch (NullPointerException e) {
            log.info("Не удалось записать лист этажей в проект дома, возможно он null");
        }
*/


}
