package ru.isa.ai.linguistic;

import org.apache.log4j.Logger;

import org.jawin.COMException;
import org.jawin.win32.Ole32;

import ru.isa.ai.linguistic.stub.ILexics;
import ru.isa.ai.linguistic.stub.IRoleSemantics;
import ru.isa.ai.linguistic.stub.ISemanticParser;
import ru.isa.ai.linguistic.stub.ISemantics;
import ru.isa.ai.linguistic.stub.ISyntax;
import ru.isa.ai.linguistic.utils.FormatUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractLinguisticAnalizer<T> implements ILinguisticAnalizer<T> {

   private static final Logger logger = Logger.getLogger(AbstractLinguisticAnalizer.class);

   protected ILexics lexics = null;
   protected ISyntax syntax = null;
   protected ISemantics semantics = null;
   protected IRoleSemantics roleSemantics = null;
   protected ISemanticParser semanticParser = null;

   protected Set<String> keywords = new HashSet<String>();
   protected SNCPrimer primer;
   protected boolean inited = false;

   private SNCProgressor progressor;

   public AbstractLinguisticAnalizer(SNCPrimer primer) {
      this.primer = primer;
   }

   public AbstractLinguisticAnalizer() {
   }

   @Override
   public void open() {
      logger.info("Loading COM objects...");
      if (!inited) {
         try {
            Ole32.CoInitialize();

            lexics = new ILexics("PFU.Lexics");
            syntax = new ISyntax("PFU.Syntax");
            semantics = new ISemantics("PFU.Semantics");
            roleSemantics = new IRoleSemantics("PFU.RoleSemantics");
            semanticParser = new ISemanticParser("PFU.SemanticParser");

            inited = true;
         } catch (COMException e) {
            logger.error("Error during analizer initialization", e);
         }
      }
   }

   @Override
   public void analize(SNCPrimer prm) throws LinguisticsAnalizingException {
      this.primer = prm;
      analize();
   }

   @Override
   public void analize() throws LinguisticsAnalizingException {
      if (inited) {
         logger.info("Start text analizing: " + primer.getTextName() + ", " + primer.getParts().size() + " parts");

         long textStarted = System.currentTimeMillis();
         for (int i = 0; i < primer.getParts().size(); i++) {
            String analizing = primer.getParts().get(i);
            logger.info("Analizing " + (i + 1) + " of " + primer.getParts().size() + " part of text (legth="
                    + analizing.length() + ")...");

            try {
               long currTime = System.currentTimeMillis();
               semanticParser.Text2Sem(analizing, lexics, syntax, semantics, roleSemantics);
               logger.debug("=====Parsed: " + FormatUtils.getFormattedIntervalFromCurrent(currTime));
               currTime = System.currentTimeMillis();

               analizeStub(primer.getTextName());

               logger.debug("=====Saved: " + FormatUtils.getFormattedIntervalFromCurrent(currTime));
               if(progressor != null){
                  progressor.markProgress((i + 1.0) / primer.getParts().size());
               }
            } catch (COMException e) {
               logger.error("Error during part analizing: " + analizing, e);
            }

         }
         logger.info("End text analizing: " + primer.getTextName() + " - "
                 + FormatUtils.getFormattedIntervalFromCurrent(textStarted));
      }
   }

   public abstract void analizeStub(String textName)
           throws LinguisticsAnalizingException;


   public void setKeywords(Set<String> keywords) {
      this.keywords = keywords;
   }

   public void setProgressor(SNCProgressor progressor) {
      this.progressor = progressor;
   }

   @Override
   public void close() {
      try {
         semanticParser.close();
         roleSemantics.close();
         semantics.close();
         syntax.close();
         lexics.close();
         Ole32.CoUninitialize();

         inited = false;
      } catch (COMException e) {
         logger.error("Error during analizer closing", e);
      }
   }

}
