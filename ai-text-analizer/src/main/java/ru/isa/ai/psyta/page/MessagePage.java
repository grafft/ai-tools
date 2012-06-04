package ru.isa.ai.psyta.page;

import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Created by IntelliJ IDEA. User: GraffT Date: 14.12.10 Time: 11:18 To change this template use File | Settings | File
 * Templates.
 */
public class MessagePage extends ACMPage {

   public MessagePage() {
      add(new FeedbackPanel("feedback"));
   }
}
