package ru.isa.ai.psyta.page.errors;


import org.apache.wicket.request.http.WebResponse;
import ru.isa.ai.psyta.page.MessagePage;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA. User: GraffT Date: 14.12.10 Time: 10:53 To change this template use File | Settings | File
 * Templates.
 */
public class InternalErrorPage extends MessagePage {

   public InternalErrorPage() {
      super();
   }

   @Override
   protected void setHeaders(final WebResponse response) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
   }

   @Override
   public boolean isVersioned() {
      return false;
   }

   @Override
   public boolean isErrorPage() {
      return true;
   }
}
