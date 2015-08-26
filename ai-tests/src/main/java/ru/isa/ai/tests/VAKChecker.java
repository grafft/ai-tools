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
    private ScheduledExecutorService scheduler;
    private final ScheduledFuture<?> beeperHandle;

    public VAKChecker(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        beeperHandle= scheduler.scheduleAtFixedRate(this, 1, 10, TimeUnit.SECONDS);
    }
    public static void main(String[] args) throws IOException, EmailException {
        new VAKChecker();
    }

    @Override
    public void run() {
        logger.info("Check");
        try {
            Document doc = Jsoup.connect("http://vak.ed.gov.ru/121").get();
            Elements divs = doc.select("#layout-column_column-2  .journal-content-article");
            Element element = divs.get(0);
            String value = element.html();
            System.out.println(value);
            if (!value.contains("24 июля 2015")) {
                System.out.println("!!!");
                Email email = new SimpleEmail();
                email.setHostName("smtp.yandex.com");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("panov.ai", "vjzgalactica13"));
                email.setSSLOnConnect(true);
                email.setFrom("panov.ai@ya.ru");
                email.setSubject("Приказ ВАК");
                email.setMsg("Вышел приказ ВАК:\n" + element.html());
                email.addTo("panov.ai@ya.ru");
                email.send();

                beeperHandle.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
