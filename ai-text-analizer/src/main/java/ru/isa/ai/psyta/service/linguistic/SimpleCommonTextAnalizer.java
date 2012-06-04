package ru.isa.ai.psyta.service.linguistic;

import org.apache.commons.lang.StringUtils;
import org.jawin.COMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isa.ai.linguistic.AbstractLinguisticAnalizer;
import ru.isa.ai.linguistic.LinguisticsAnalizingException;
import ru.isa.ai.linguistic.stub.*;
import ru.isa.ai.linguistic.utils.FormatUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

/**
 * User: GraffT
 * Date: 19.10.11
 * Time: 16:04
 */
public class SimpleCommonTextAnalizer extends AbstractLinguisticAnalizer<Map<String, Integer>> {

   private static final Logger logger = LoggerFactory.getLogger(SimpleCommonTextAnalizer.class);

   private Map<String, Integer> frequencies = new HashMap<String, Integer>();

   @Override
   public void analizeStub(String textName) throws LinguisticsAnalizingException {
      try {
         ISynSentenceIterator iterator = syntax.getBegin();
         if (FormatUtils.getIdentifier(iterator) != 0) {
            do {
               ISynSentence sentence = iterator.getValue();
               ISynWordIterator wordIterator = sentence.getBegin().getValue().getBegin();
               do {
                  ISynWord word = wordIterator.getValue();
                  String dictForm = word.getLexeme().getDictForm();
                  if (word.getLexeme().getType() != ELexemeType.LT_PREPOSITION) {
                     if (keywords.contains(dictForm)) {
                        if (!frequencies.containsKey(dictForm)) {
                           frequencies.put(dictForm, 0);
                        }
                        frequencies.put(dictForm, frequencies.get(dictForm) + 1);
                     }
                  }
               } while (wordIterator.Next());
            } while (iterator.Next());
         }
      } catch (COMException e) {
         logger.error("Error during sentences extracting", e);
      }
   }

   public void resetResult() {
      frequencies.clear();
   }

   @Override
   public Map<String, Integer> getResult() {
      Map<String, Integer> result = new HashMap<String, Integer>();
      for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
         result.put(entry.getKey(), entry.getValue());
      }
      return result;
   }

   public static List<String> loadProtoSignsFromFile(String filename) throws IOException {

      ArrayList<String> array = new ArrayList<String>();
      BufferedReader in = new BufferedReader(new InputStreamReader(
              new FileInputStream(filename), Charset.forName("UTF-8")));
      Scanner scanner = new Scanner(in);
      scanner.useDelimiter(Pattern.compile("\\n"));

      while (scanner.hasNext()) {
         String line = scanner.next().trim();
         if (line.contains("/")) {
            Pattern pattern = Pattern.compile("/");
            for (String part : pattern.split(line)) {
               if (!StringUtils.isEmpty(part)) {
                  array.add(part.toLowerCase());
               }
            }

         } else {
            if (!StringUtils.isEmpty(line)) {
               array.add(line.toLowerCase());
            }
         }
      }
      scanner.close();
      return array;
   }
}
