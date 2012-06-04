package ru.isa.ai.psyta.service.mail;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

/**
 * User: GraffT
 * Date: 14.10.11
 * Time: 19:10
 */
public class Mail {
   public static final InternetAddress ACM_TEAM_EMAIL = address("pan@isa.ru", "ACM Team");

      public static InternetAddress address(String email, String caption) {
      try {
         return new InternetAddress(email, caption, MimeType.CHARSET_NAME);
      } catch(UnsupportedEncodingException e) {
         throw new RuntimeException("Could not parse encoding", e);
      }
   }
}
