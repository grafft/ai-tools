package ru.isa.ai.linguistic;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractLinguisticAnalyzer<T> implements ILinguisticAnalyzer<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLinguisticAnalyzer.class);

    protected ActiveXComponent lexics = null;
    protected ActiveXComponent syntax = null;
    protected ActiveXComponent semantics = null;
    protected ActiveXComponent roleSemantics = null;
    protected ActiveXComponent semanticParser = null;

    protected Set<String> keywords = new HashSet<>();
    protected boolean inited = false;

    private SNCProgressor progressor;

    public AbstractLinguisticAnalyzer() {
        logger.info("Loading COM objects...");
        try {
            lexics = new ActiveXComponent("PFU.Lexics");
            syntax = new ActiveXComponent("PFU.Syntax");
            semantics = new ActiveXComponent("PFU.Semantics");
            roleSemantics = new ActiveXComponent("PFU.RoleSemantics");
            semanticParser = new ActiveXComponent("PFU.SemanticParser");

            inited = true;
        } catch (Exception e) {
            logger.error("Error during COM initializing", e);
        }
    }

    @Override
    public void analyze(SNCPrimer primer) throws LinguisticsAnalyzingException {
        if (inited) {
            logger.info("Start text analizing: " + primer.getTextName() + ", " + primer.getParts().size() + " parts");

            long textStarted = System.currentTimeMillis();
            for (int i = 0; i < primer.getParts().size(); i++) {
                String analizing = primer.getParts().get(i);
                logger.info("Analyzing " + (i + 1) + " of " + primer.getParts().size() + " part of text (length="
                        + analizing.length() + ")...");

                long currTime = System.currentTimeMillis();
                try {
                    Dispatch.call(semanticParser, "Text2Sem", analizing, lexics, syntax, semantics, roleSemantics);
                    logger.debug("=====Parsed: " + LinguisticUtils.getFormattedIntervalFromCurrent(currTime));
                    currTime = System.currentTimeMillis();

                    analyzeStub(primer.getTextName());

                    logger.debug("=====Saved: " + LinguisticUtils.getFormattedIntervalFromCurrent(currTime));
                } catch (LinguisticsAnalyzingException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error("Error during part #" + i + " analyzing", e);
                }

                if (progressor != null) {
                    progressor.markProgress((i + 1.0) / primer.getParts().size());
                }

            }
            logger.info("End text analyzing: " + primer.getTextName() + " - " + LinguisticUtils.getFormattedIntervalFromCurrent(textStarted));
        } else {
            logger.warn("Analyzer was closed");
        }
    }

    public abstract void analyzeStub(String textName) throws LinguisticsAnalyzingException;

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public void setProgressor(SNCProgressor progressor) {
        this.progressor = progressor;
    }

    @Override
    public void close() {
        try {
            semanticParser.safeRelease();
            roleSemantics.safeRelease();
            semantics.safeRelease();
            syntax.safeRelease();
            lexics.safeRelease();

            inited = false;
        } catch (Exception e) {
            logger.error("Error during analyzer closing", e);
        }
    }

}
