package ru.isa.ai.psyta.page;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import ru.isa.ai.psyta.service.mail.Mail;

import java.util.Calendar;
import java.util.Date;

/**
 * User: GraffT
 * Date: 14.10.11
 * Time: 19:08
 */
public class ACMPage extends WebPage {
    public ACMPage() {
        add(new BookmarkablePageLink<HomePage>("welcomePageLink", HomePage.class));
        ExternalLink link = new ExternalLink("ourEmail", "mailto:" + Mail.ACM_TEAM_EMAIL.getAddress());
        add(link);
        link.add(new Label("ourEmailLabel", Mail.ACM_TEAM_EMAIL.getAddress()).setRenderBodyOnly(true));
        add(new Label("ourCompLabel","©"+ Calendar.getInstance().get(Calendar.YEAR)+" Лаборатория 0-2 ИСА РАН").setRenderBodyOnly(false));
    }
}
