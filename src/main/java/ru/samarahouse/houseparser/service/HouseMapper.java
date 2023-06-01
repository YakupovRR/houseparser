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
    // переделать англиское название - просто вытягивать с урла
    // после сохранения можно сделать СЕЛЕКТ по проектам без этажности, и где на 1+ мансарда всего 1 картинка планировки
// description у Лесстроя нет
    private String str;
    private StringBuffer stringBuffer;
    private final String fileName = "project.html";

    private final List<String> requiredParameters = new ArrayList<>();
    private final Map<String, String> findParameters = new HashMap<>();

    private SaveImages saveImages = new SaveImages();


    public House projectMapper(Integer id) {
        int cout = 0;


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
                    house.setTags(findTags(in));
                }
                if (str.contains("Планировка")) {
                    house.setLayoutUrls(findPlanImagesUrls(in));
                }
                if (str.contains("<swiper-wrapper gallery-swiper-wrapper")) { //картинки дома
                    house.setExteriorUrls(findHouseImagesUrls(in));
                    cout++;
                    log.info("вызвали метод findHouseImagesUrls "+ cout + "раз");

                }

                if (str.contains("Особенности проекта")) {
                    List<String> features = findFeatures(in);
                    house.setFeatures(features);
                    //здесь же вытягиваем этажность
                    if (features.contains("С мансардой")) {
                        house.setFloors(Floors.ONEPLUSMANSARD);
                    } else if (features.contains("Одноэтажный")) {
                        house.setFloors(Floors.ONE);
                    } else if (features.contains("Двухэтажный")) {
                        house.setFloors(Floors.TWO);
                    } else if (features.contains("Трехэтажный")) {
                        house.setFloors(Floors.THERE);
                    }
                    // и наличие цоколя
                    if (features.contains("С цокольным этажом")) {
                        house.setGroundFloor(true);
                    }
                }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        house = saveImages.saveListsImages(house);
        return house;
    }

    private String findName(BufferedReader in) throws IOException {
        str = in.readLine();
        String[] l = str.split("«");
        String[] ll = l[1].split("»");
        return ll[0];
    }

    private Integer findRooms(BufferedReader in) throws IOException {
        str = in.readLine();
        str = in.readLine();
        String[] l = str.split(" ");
        return Integer.parseInt(l[0]);
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
            log.info("Размер листа с планировками после мапинга равен " + foundPlanImagesUrls.size());
        }


        return foundPlanImagesUrls;

    }

    private LinkedList<String> findHouseImagesUrls(BufferedReader in) throws IOException {
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
        log.info("Размер листа с планировками после мапинга равен " + houseImagesUrls.size());
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
