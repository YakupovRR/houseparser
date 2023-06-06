package ru.samarahouse.houseparser.service;


import com.ibm.icu.text.Transliterator;
import lombok.extern.slf4j.Slf4j;
import ru.samarahouse.houseparser.model.Floors;
import ru.samarahouse.houseparser.model.House;
import org.apache.commons.lang3.StringUtils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
public class HouseMapper {


    // description у Лесстроя нет
    private String str;
    private final String fileName = "project.html";
    private boolean flagTitle = true;
    private boolean flagSquare = true;
    private boolean flagRooms = true;
    private boolean flagWidthAndLength = true;
    private boolean flagTags = true;


    public House projectMapper(Integer id) {
        House house = new House();
        house.setId(id);
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while ((str = in.readLine()) != null) {
                if (flagTitle && str.contains("<title>")) {
                    String title = findName(in);
                    house.setTitle(title);
                    house.setTitleEng(transliterator(title));
                    flagTitle = false;
                }
                if (flagSquare && str.contains("Общая площадь")) {
                    Double s = Double.valueOf(findSquare(in));
                    house.setSquare(s);
                    flagSquare = false;

                }
                if (flagRooms && str.contains("Жилых комнат")) {
                    Integer rooms = findRooms(in);
                    house.setRooms(rooms);
                    flagRooms = false;
                }
                if (flagWidthAndLength && str.contains("Габариты")) {
                    List<Integer> sizes = findSizes(in);
                    house.setWidth(Double.valueOf(sizes.get(0)));
                    house.setLength(Double.valueOf(sizes.get(1)));
                    flagWidthAndLength = false;
                }

                if (flagTags && str.contains("Категория:")) {
                    flagTags = false;
                    List<String> tags = findTags(in);
                    house.setTags(tags);
                    //здесь же вытягиваем этажность
                    if (tags.contains("С мансардой")) {
                        house.setFloors(Floors.ONEPLUSMANSARD);
                    } else if (tags.contains("Одноэтажный")) {
                        house.setFloors(Floors.ONE);
                    } else if (tags.contains("Двухэтажный")) {
                        house.setFloors(Floors.TWO);
                    } else if (tags.contains("Трехэтажный")) {
                        house.setFloors(Floors.THERE);
                    }
                    // и прочие основные тэги
                    if (tags.contains("С цокольным этажом")) {
                        house.setGroundFloor(true);
                    }
                }
                if (str.contains("plains-list-box")) {
                    house.setLayoutUrls(findPlanImagesUrls(in));
                }
                if (str.contains("swiper-container gallery-slider")) { //картинки экстерьера дома
                    house.setExteriorUrls(findExteriorUrls(in));
                }
                if (str.contains("Особенности проекта")) {
                    house.setFeatures(findFeatures(in));
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setAllFalgsTrue();
        return house;
    }

    private void setAllFalgsTrue() {
        flagTitle = true;
        flagSquare = true;
        flagRooms = true;
        flagWidthAndLength = true;
        flagTags = true;
    }

    private String findName(BufferedReader in) throws IOException {
        str = in.readLine();
        String name = null;
        try {
            String[] l = str.split("<");
            name = l[0];
            name = name.trim();
            name = name.replace("\"", "");
        } catch (ArrayIndexOutOfBoundsException e) {
            log.info("Ошибку при поиске имени");
        }
        return name;
    }

    private Integer findRooms(BufferedReader in) throws IOException {
        Integer rooms = -1;  //т.к. все что свыше 6 идет как "6+"

        try {
            str = in.readLine();
            str = in.readLine();
            String[] l = str.split(" ");
            rooms = Integer.parseInt(l[0]);
        } catch (NumberFormatException e) {
            log.warn("Не удалось считать количество комнат из файла");
        }
        return rooms;
    }

    private Integer findSquare(BufferedReader in) throws IOException {
        Integer square = -1;
        try {
            str = in.readLine();
            str = in.readLine();
            String[] l = str.split(" "); //читаем до пробела
            square = Integer.parseInt(l[0]);
        } catch (NumberFormatException e) {
            log.info("Не удалось считать площадь из файла");
        }
        return square;
    }

    private List<Integer> findSizes(BufferedReader in) throws IOException {
        List<Integer> sizes = new ArrayList<>();

        try {
            str = in.readLine();
            str = in.readLine();
            String[] l = str.split(" "); //читаем до пробела
            Integer a = Integer.parseInt(l[0]);
            String stringB = l[1].substring(1);
            Integer b = Integer.parseInt(stringB);
            sizes.add(a);
            sizes.add(b);
        } catch (NumberFormatException e) {
            log.warn("Не удалось считать количество размеры из файла");
        }
        return sizes;
    }

    private List<String> findTags(BufferedReader in) throws IOException {
        List<String> tags = new ArrayList<>();
        str = in.readLine();
        try {
            String[] stringWithMainTag = str.split("</a>");
            String mainTag = stringWithMainTag[0].trim();         //он в дивах, поэтому отдельно
            tags.add(mainTag);
        } catch (NumberFormatException e) {
            log.warn("Не удалось считать главный тэг из файла");
        }
        str = in.readLine();
        str = in.readLine();
        str = in.readLine();
        str = in.readLine();
        try {
            while (!str.contains("</div>")) {
                str = in.readLine();
                String firsChar = String.valueOf(str.charAt(0));
                if (!firsChar.equals("<") && !(firsChar.equals(" "))) {
                    String[] stringWithTag = str.split("</span>");
                    String tag = stringWithTag[0].trim();
                    tags.add(tag);
                }
            }
        } catch (NumberFormatException e) {
            log.warn("Не удалось считать тэги из файла");
        }
        return tags;
    }

    private LinkedList<String> findPlanImagesUrls(BufferedReader in) throws IOException {
        LinkedList<String> foundPlanImagesUrls = new LinkedList<>();
        str = in.readLine();
        while (!str.contains("Построенные объекты")) {
            str = in.readLine();
            if (str.contains("href")) {
                try {
                    String[] stringWithTag = str.split("\"");
                    String url = stringWithTag[1].trim();
                    foundPlanImagesUrls.add(url);
                } catch (NumberFormatException e) {
                    log.warn("Не удалось считать url планировок из файла");
                }
            }
        }
        return foundPlanImagesUrls;
    }

    private LinkedList<String> findExteriorUrls(BufferedReader in) throws IOException {
        LinkedList<String> houseImagesUrls = new LinkedList<>();
        str = in.readLine();
        while (!str.contains("project-item-like hidden-print")) {
            str = in.readLine();
            if (str.contains("href")) {
                try {
                    String[] stringWithTag = str.split("\"");
                    String url = stringWithTag[1].trim();
                    houseImagesUrls.add(url);
                } catch (NumberFormatException e) {
                    log.warn("Не удалось считать url экстерьеров из файла");
                }
            }
        }
        return houseImagesUrls;
    }

    private List<String> findFeatures(BufferedReader in) throws IOException {
        List<String> features = new ArrayList<>();
        str = in.readLine();
        str = in.readLine();
        str = in.readLine();
        while (!str.contains("</ul>")) {
            str = in.readLine();
            String firsChar = String.valueOf(str.charAt(0));
            try {
                if (!firsChar.equals("<") && !(firsChar.equals(" "))) {
                    String[] stringWithFiature = str.split("</li>");
                    String fiature = stringWithFiature[0].trim();
                    features.add(fiature);
                }
            } catch (NumberFormatException e) {
                log.warn("Не удалось считать фичи из файла");
            }
        }
        return features;
    }

    //Получение titleEng через транслитерацию
    private String transliterator(String rusText) {
        Transliterator transliterator = Transliterator.getInstance("Russian-Latin/BGN");
        String engText = transliterator.transliterate(rusText);
        return StringUtils.stripAccents(engText);
    }

    public String getTitleEngFromUrl(String url) {
        String titleEng = null;
        try {
            String[] partsUrl = url.split("/");
            titleEng = partsUrl[4];
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return titleEng;
    }

}
