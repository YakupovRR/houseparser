package ru.samarahouse.houseparser.service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SavePage {

    private static final String fileName = "project";


    public void savePage(String link) throws IOException {
        WebClient webClient = createWebClient();
        File file = new File(fileName);
//удаляем старый файл
        Path path = Paths.get("project.html");
        try {
            boolean result = Files.deleteIfExists(path);
            if (result) {
                System.out.println("Файл старого проекта удалён.");
            } else {
                System.out.println("Ошибка удаления файла старого проекта.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //сохраняем новый проект
        try {
       //     String link = "https://lesstroy63.ru/proekty/maksim/";
            HtmlPage page = webClient.getPage(link);
            page.save(file);
            System.out.println("сохранили страницу проекта " + page.getTitleText());

        } catch (FailingHttpStatusCodeException | IOException e) {
            e.printStackTrace();
        } finally {
            webClient.close();
        }

    }

    private static WebClient createWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
        return webClient;
    }
}
