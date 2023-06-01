package ru.samarahouse.houseparser.service.save;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;


@Slf4j
public class SavePage {

    private static final String fileName = "project";
    private static final String fileFormat = ".html";


    public void savePage(String link) throws IOException {
        WebClient webClient = createWebClient();
        File file = new File(fileName);
//удаляем старый файл
        Path path = Paths.get(fileName +  fileFormat);
        try {
            boolean result = Files.deleteIfExists(path);
            if (result) {
                log.info("Файл старого проекта удалён.");
            } else {
                log.info("Ошибка удаления файла старого проекта.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //удаляем старую папку
        try {
            FileUtils.deleteDirectory(new File(fileName));
            log.info("Папка старого проекта удалена.");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //сохраняем новый проект
        try {
            HtmlPage page = webClient.getPage(link);
            page.save(file);
            log.info("сохранили страницу проекта " + page.getTitleText());
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
