package ru.isa.ai.linguistic.analyzers;

import com.google.common.util.concurrent.*;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isa.ai.linguistic.data.SNCPrimer;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public abstract class AbstractLinguisticAnalyzer<T, P> implements ILinguisticAnalyzer<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLinguisticAnalyzer.class);

    protected ActiveXComponent lexics = null;
    protected ActiveXComponent syntax = null;
    protected ActiveXComponent semantics = null;
    protected ActiveXComponent roleSemantics = null;
    protected ActiveXComponent semanticParser = null;

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
    public void analyze(final SNCPrimer primer, FutureCallback<T> callback) {
        if (inited) {
            ListeningExecutorService mainExecutor = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
            ListenableFuture<T> futureResult = mainExecutor.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return executeMainJob(primer);
                }
            });
            Futures.addCallback(futureResult, callback);

        } else {
            logger.warn("Analyzer was closed");
        }
    }

    private T executeMainJob(final SNCPrimer primer) {
        long textStarted = System.currentTimeMillis();

        T mainResult = null;

        for (int i = 0; i < primer.getParts().size(); i++) {
            String analyzing = primer.getParts().get(i);
            logger.info("Analyzing " + (i + 1) + " of " + primer.getParts().size() + " part of text (length="
                    + analyzing.length() + ")...");

            long currTime = System.currentTimeMillis();
            try {
                Dispatch.call(semanticParser, "Text2Sem", analyzing, lexics, syntax, semantics, roleSemantics);
                logger.debug("=====Parsed: " + LinguisticUtils.getFormattedIntervalFromCurrent(currTime));
                currTime = System.currentTimeMillis();

                P part = analyzePart(primer.getTextName());
                mainResult = collectMainResult(part, mainResult);

                logger.debug("=====Saved: " + LinguisticUtils.getFormattedIntervalFromCurrent(currTime));
            } catch (Exception e) {
                logger.error("Error during part #" + i + " analyzing", e);
            }

            if (progressor != null) {
                progressor.markProgress((i + 1.0) / primer.getParts().size());
            }
        }
        logger.info("End text analyzing: " + primer.getTextName() + " - " + LinguisticUtils.getFormattedIntervalFromCurrent(textStarted));
        return mainResult;
    }

    public abstract P analyzePart(String textName) throws LinguisticsAnalyzingException;

    public abstract T collectMainResult(P partResult, T currentResult);

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
