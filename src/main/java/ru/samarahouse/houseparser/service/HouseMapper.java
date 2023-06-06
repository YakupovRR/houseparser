package ru.samarahouse.houseparser.service;


import com.ibm.icu.text.Transliterator;
import lombok.extern.slf4j.Slf4j;
import ru.samarahouse.houseparser.model.Floors;
import ru.samarahouse.houseparser.model.House;
import org.apache.commons.lang3.StringUtils;
import ru.samarahouse.houseparser.service.save.SaveImages;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
public class HouseMapper {

    // TODO: 04.03.2023
    // Сделать через stitch case парсинг поля хаус
    // сделать подключение к БД (логи, пароль и т.д.) через app.pro
    // переделать английское название - просто вытягивать с урла
    // сделать проверку, что поле ещё не найдено через !null

    // description у Лесстроя нет
    private String str;
    private StringBuffer stringBuffer;
    private final String fileName = "project.html";

    private final List<String> requiredParameters = new ArrayList<>();
    private final Map<String, String> findParameters = new HashMap<>();

    private SaveImages saveImages = new SaveImages();


    public House projectMapper(Integer id) {
        if (requiredParameters.isEmpty()) setRequiredParameters();
        clearFoundParameters();
        House house = new House();
        house.setId(id);
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            while ((str = in.readLine()) != null) {
                if (str.contains("<title>")) {
                    String title = findName(in);
                    house.setTitle(title);
                    house.setTitleEng(transliterator(title));
                }
                if (str.contains("Общая площадь")) {
                    Double s = Double.valueOf(findSquare(in));
                    house.setSquare(s);
                }
                if (str.contains("Жилых комнат")) {
                    Integer rooms = findRooms(in);
                    house.setRooms(rooms);
                }
                if (str.contains("Габариты")) {
                    List<Integer> sizes = findSizes(in);
                    house.setWidth(Double.valueOf(sizes.get(0)));
                    house.setLength(Double.valueOf(sizes.get(1)));
                }
             /*
              // старый номер проекта, решили пока не надо
              if (str.contains("Номер проекта")) {
                    Integer oldId = findOldId(in);
                    house.setOldId(oldId);
                }
                 */
                if (str.contains("Категория:")) {
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
        return house;
    }

    private String findName(BufferedReader in) throws IOException {
        str = in.readLine();
        String name = String.valueOf(in);
        try {
            String[] l = str.split("«");
            String[] ll = l[1].split("»");
            name = ll[0];
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        return name;
    }

    private Integer findRooms(BufferedReader in) throws IOException {
        str = in.readLine();
        str = in.readLine();
        String[] l = str.split(" ");
        return Integer.parseInt(l[0]);


        /*
        Exception in thread "main" java.lang.NumberFormatException: For input string: "6+"
	at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:67)

         */
    }

    private Integer findSquare(BufferedReader in) throws IOException {
        str = in.readLine();
        str = in.readLine();
        String[] l = str.split(" "); //читаем до пробела
        return Integer.parseInt(l[0]);
    }

    private List<Integer> findSizes(BufferedReader in) throws IOException {
        List<Integer> sizes = new ArrayList<>();
        str = in.readLine();
        str = in.readLine();
        String[] l = str.split(" "); //читаем до пробела
        log.info("Массив с размерами " + l.toString());
        Integer a = Integer.parseInt(l[0]);

        String stringB = l[1].substring(1);
        Integer b = Integer.parseInt(stringB);
        sizes.add(a);
        sizes.add(b);
        return sizes;
    }

    private Integer findOldId(BufferedReader in) throws IOException {
        str = in.readLine();
        str = in.readLine();
        String[] l = str.split(" ");
        return Integer.parseInt(l[0]);
    }

    private List<String> findTags(BufferedReader in) throws IOException {
        List<String> tags = new ArrayList<>();
        str = in.readLine();
        String[] stringWithMainTag = str.split("</a>");
        String mainTag = stringWithMainTag[0].trim();         //он в дивах, поэтому отдельно
        tags.add(mainTag);
        str = in.readLine();
        str = in.readLine();
        str = in.readLine();
        str = in.readLine();
        while (!str.contains("</div>")) {
            str = in.readLine();
            String firsChar = String.valueOf(str.charAt(0));
            if (!firsChar.equals("<") && !(firsChar.equals(" "))) {
                String[] stringWithTag = str.split("</span>");
                String tag = stringWithTag[0].trim();
                tags.add(tag);
            }
        }
        return tags;
    }

    private LinkedList<String> findPlanImagesUrls(BufferedReader in) throws IOException {
        LinkedList<String> foundPlanImagesUrls = new LinkedList<>();
        str = in.readLine();
        while (!str.contains("Построенные объекты")) {
            str = in.readLine();
            if (str.contains("href")) {
                String[] stringWithTag = str.split("\"");
                String url = stringWithTag[1].trim();
                foundPlanImagesUrls.add(url);
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
                String[] stringWithTag = str.split("\"");
                String url = stringWithTag[1].trim();
                houseImagesUrls.add(url);
            }
        }
        log.info("Размер листа с экстерьерами после мапинга равен " + houseImagesUrls.size());
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
            if (!firsChar.equals("<") && !(firsChar.equals(" "))) {
                String[] stringWithFiature = str.split("</li>");
                String fiature = stringWithFiature[0].trim();
                features.add(fiature);
            }
        }
        return features;
    }

    private void setRequiredParameters() {
        requiredParameters.add("Общая площадь");
    }

    private void clearFoundParameters() {
        findParameters.clear();
        for (String i : requiredParameters) {
            findParameters.put(i, null);
        }
    }

    public String transliterator(String rusText) {
        Transliterator transliterator = Transliterator.getInstance("Russian-Latin/BGN");
        String engText = transliterator.transliterate(rusText);
        return StringUtils.stripAccents(engText);
    }
}
