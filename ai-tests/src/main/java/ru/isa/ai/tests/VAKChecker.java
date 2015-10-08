package ru.isa.ai.tests;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aleksandr Panov on 26.08.2015.
 */
public class VAKChecker implements Runnable {
    private static final Logger logger = LogManager.getLogger(VAKChecker.class.getSimpleName());
    private String checkString = "21 сентября 2015";
    private ScheduledExecutorService scheduler;
    private boolean toExit = false;

    public VAKChecker() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this, 1, 10, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws IOException, EmailException {
        new VAKChecker();
    }

    @Override
    public void run() {
        try {
            String value = checkReady();
            //String value = checkOrder();
            if (value != null) {
                Email email = new SimpleEmail();
                email.setHostName("smtp.yandex.com");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("panov.ai", "vjzgalactica13"));
                email.setSSLOnConnect(true);
                email.setFrom("panov.ai@ya.ru");
                email.setSubject("Приказ ВАК");
                email.setMsg("Вышел приказ или сведение о готовности ВАК:\n" + value);
                email.addTo("panov.ai@ya.ru");
                email.send();

                if(toExit)
                    scheduler.shutdownNow();
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private String checkReady() throws IOException {
        Document doc = Jsoup.connect("http://vak.ed.gov.ru/119").get();
        Elements tables = doc.select(".table_diplom");
        Elements spans = doc.select(".datelabel");
        Element table = tables.get(0);
        Element span = spans.get(0);

        String value = span.html();
        String text = table.html();
        if(!value.contains(checkString)){
            checkString = value;
            return text;
        }else{
            return null;
        }
    }

    private String checkOrder() throws IOException {
        Document doc = Jsoup.connect("http://vak.ed.gov.ru/121").get();
        Elements divs = doc.select("#layout-column_column-2  .journal-content-article");
        Element element = divs.get(0);
        String value = element.html();
        Elements as = element.select("a");
        String text = as.last().html();

        if(!text.contains(checkString)){
            logger.info("New files are detected:\n" + text);

            checkString = text;
            return value;
        }else{
            return null;
        }
    }
}
