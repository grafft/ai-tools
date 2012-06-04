package ru.isa.ai.linguistic.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import ru.isa.ai.linguistic.SNCPrimer;

public class TxtFileDataLoader implements DataLoader {

   private static final Logger logger = Logger
           .getLogger(TxtFileDataLoader.class);

   private BufferedReader in;
   private String fileName;
   private String delimiter = null;

   public TxtFileDataLoader(String fileName) throws FileNotFoundException {
      this(fileName, "cp1251");
   }

   public TxtFileDataLoader(URL filePath) throws Exception {
      this(filePath, "cp1251");
   }

   public TxtFileDataLoader(URL filePath, String charsetName) throws Exception {
      this.fileName = new File(filePath.toURI()).getName();
      try {
         in = new BufferedReader(new InputStreamReader(filePath.openStream(), Charset.forName(charsetName)));
      } catch(IOException e) {
         logger.error("Error during loader initialization", e);
         throw e;
      }

   }

   public TxtFileDataLoader(String fileName, String charsetName)
           throws FileNotFoundException {
      this.fileName = fileName;
      try {
         in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName(charsetName)));
      } catch(FileNotFoundException e) {
         logger.error("Error during loader initialization", e);
         throw e;
      }
   }

   @Override
   public SNCPrimer loadData() {
      logger.info("Data loading from...");
      SNCPrimer primer = new SNCPrimer();
      primer.setTextName(fileName);
      Scanner scanner = new Scanner(in);
      scanner.useDelimiter(Pattern.compile(delimiter != null ? delimiter : "\\.\\s+"));
      while(scanner.hasNext()) {
         String sentence = scanner.next();
         primer.addPart(sentence);
      }
      return primer;
   }

   public void setDelimiter(String delimiter) {
      this.delimiter = delimiter;
   }

}
