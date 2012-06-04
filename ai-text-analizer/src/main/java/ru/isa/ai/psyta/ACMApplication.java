package ru.isa.ai.psyta;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebResponse;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.file.Folder;
import ru.isa.ai.psyta.page.HomePage;
import ru.isa.ai.psyta.page.errors.ExpiredSessionPage;
import ru.isa.ai.psyta.page.errors.InternalErrorPage;
import ru.isa.ai.psyta.page.errors.PageNotFoundPage;

import javax.servlet.http.HttpServletResponse;

public class ACMApplication extends WebApplication {

   private Folder uploadFolder = null;
   private Folder resultFolder = null;

   public ACMApplication() {
   }

   public void init() {
      super.init();
      getComponentInstantiationListeners().add(new SpringComponentInjector(this));
      getDebugSettings().setAjaxDebugModeEnabled(Configuration.isDebug());
      getRequestLoggerSettings().setRequestLoggerEnabled(Configuration.isProduction());

      getRequestCycleSettings().setResponseRequestEncoding("UTF-8");
      getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
      getMarkupSettings().setStripWicketTags(true);
      getMarkupSettings().setStripComments(true);

      mountPage("/psyta", getHomePage());

      getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_INTERNAL_ERROR_PAGE);
      getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
      getApplicationSettings().setPageExpiredErrorPage(ExpiredSessionPage.class);
      mountPage("/404", PageNotFoundPage.class);
      mountPage("/expired", ExpiredSessionPage.class);
      mountPage("/error", InternalErrorPage.class);

      getApplicationSettings().setUploadProgressUpdatesEnabled(true);
      uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "acm-uploads");
      resultFolder = new Folder(System.getProperty("java.io.tmpdir"), "acm-results");
      // Ensure folder exists
      uploadFolder.mkdirs();
      resultFolder.mkdirs();
   }

   public Class<HomePage> getHomePage() {
      return HomePage.class;
   }

   @Override
   public RuntimeConfigurationType getConfigurationType() {
      return Configuration.isDebug() ? RuntimeConfigurationType.DEVELOPMENT : RuntimeConfigurationType.DEPLOYMENT;
   }

   public Folder getUploadFolder() {
      return uploadFolder;
   }

   public Folder getResultFolder() {
      return resultFolder;
   }

   @Override
   public Session newSession(Request request, Response response) {
      return new ACMSession(request);
   }


   // SEO - remove jsessionid for bots
   @Override
   protected WebResponse newWebResponse(final WebRequest webRequest, final HttpServletResponse httpServletResponse) {
      return new ServletWebResponse((ServletWebRequest) webRequest, httpServletResponse) {

         @Override
         public String encodeURL(CharSequence url) {
            final String agent = webRequest.getHeader("User-Agent");
            return isAgent(agent) ? url.toString() : super.encodeURL(url);
         }
      };
   }

   private static final String[] botAgents = {
           "googlebot", "msnbot", "slurp", "jeeves"
           /*
       * "appie", "architext", "jeeves", "bjaaland", "ferret", "gulliver",
       * "harvest", "htdig", "linkwalker", "lycos_", "moget", "muscatferret",
       * "myweb", "nomad", "scooter", "yahoo!\\sslurp\\schina", "slurp",
       * "weblayers", "antibot", "bruinbot", "digout4u", "echo!", "ia_archiver",
       * "jennybot", "mercator", "netcraft", "msnbot", "petersnews",
       * "unlost_web_crawler", "voila", "webbase", "webcollage", "cfetch",
       * "zyborg", "wisenutbot", "robot", "crawl", "spider"
       */};


   public static boolean isAgent(final String agent) {
      if (agent != null) {
         final String lowerAgent = agent.toLowerCase();
         for (final String bot : botAgents) {
            if (lowerAgent.contains(bot)) {
               return true;
            }
         }
      }
      return false;
   }
}
