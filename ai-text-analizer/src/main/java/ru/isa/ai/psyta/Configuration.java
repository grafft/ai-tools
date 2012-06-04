package ru.isa.ai.psyta;

import ru.paddle.common.util.Classes;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

public class Configuration {

   private static final String CONFIGURATION_FILENAME = "/application.properties";
   private static final String PRODUCTION = "production";

   private static boolean production;

   static {
      // load configuration at least once
      reload();
   }

   public static void reload() {
      try {
         Properties configProps = new Properties();
         configProps.load(Classes.getStream(CONFIGURATION_FILENAME));
         production =  Boolean.parseBoolean(configProps.getProperty(PRODUCTION));
      } catch(IOException e) {
         throw new RuntimeException("Could not load KinoKrug configuration", e);
      }
   }

   public static boolean isProduction() { return production; }

   public static boolean isDebug() { return !production; }

}
