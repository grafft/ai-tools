package ru.isa.ai.psyta.service.mail;

import ru.paddle.common.util.Charsets;

import java.nio.charset.Charset;
import java.text.MessageFormat;

/**
 * Encapsulates all mime types used.
 *
 * @author tima@paddle.ru (Timofey Basanov)
 */
public enum MimeType {

   ZIP("application/zip"),
   XLS("application/vnd.ms-excel"),
   DOC("application/msword"),
   PDF("application/pdf"),
   JPG("image/jpeg"),
   TXT("text/plain"),
   HTML("text/html"),
   BINARY("application/octet-stream"),
   UNKNOWN("application/x-unknown");

   public static final Charset CHARSET = Charsets.UTF8;
   public static final String CHARSET_NAME = CHARSET.name();

   private final String type;

   MimeType(String type) {
      this.type = type;
   }

   public String getTypeWithCharset() {
      return MessageFormat.format("{0}; charset=\"{1}\"", type, CHARSET_NAME);
   }

   /*--------------- Getters ---------------*/

   public String getType() { return type; }

}
