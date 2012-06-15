package ru.isa.ai.linguistic.analyzers;

import com.google.common.util.concurrent.*;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.isa.ai.linguistic.data.SNCPrimer;
import ru.isa.ai.linguistic.utils.LinguisticUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Author: Aleksandr Panov
 * Date: 15.06.12
 * Time: 12:19
 */
public abstract class AbstractMultithreadedAnalyzer<T,P> implements ILinguisticAnalyzer<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMultithreadedAnalyzer.class);

    protected ActiveXComponent lexics = null;
    protected ActiveXComponent syntax = null;
    protected ActiveXComponent semantics = null;
    protected ActiveXComponent roleSemantics = null;
    protected ActiveXComponent semanticParser = null;

    protected boolean inited = false;
    private SNCProgressor progressor;
    private int progress = 0;
    private int totalWork = 0;

    public AbstractMultithreadedAnalyzer() {
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
        progress = 0;
        totalWork = primer.getParts().size();
        final int nThreads = Runtime.getRuntime().availableProcessors();
        final int range = totalWork / nThreads;
        final int lastOverHead = totalWork - nThreads * range;

        T mainResult = null;
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        List<Future<P>> futures = new ArrayList<>(nThreads);
        logger.info("Start text analyzing: " + primer.getTextName() + ", " + totalWork + " parts," + nThreads + " threads");
        for (int i = 0; i < nThreads; i++) {
            final int index = i;
            Future<P> futurePartResult = executor.submit(new Callable<P>() {
                @Override
                public P call() throws Exception {
                    P total = null;
                    for (int i = index * range; i < (index + 1) * range + lastOverHead; i++) {
                        String analyzing = primer.getParts().get(i);
                        logger.info("Analyzing " + (i + 1) + " of " + totalWork + " part of text (length="
                                + analyzing.length() + ", thread=" + index + ")...");

                        long currTime = System.currentTimeMillis();
                        try {
                            Dispatch.call(semanticParser, "Text2Sem", analyzing, lexics, syntax, semantics, roleSemantics);
                            logger.debug("=====Parsed: " + LinguisticUtils.getFormattedIntervalFromCurrent(currTime));
                            currTime = System.currentTimeMillis();

                            P part = analyzePart(primer.getTextName());
                            collectPartResult(part, total);

                            logger.debug("=====Saved: " + LinguisticUtils.getFormattedIntervalFromCurrent(currTime));
                        } catch (Exception e) {
                            logger.error("Error during part #" + i + " analyzing", e);
                        }

                        markProgress();
                    }
                    return total;
                }

            });
            futures.add(futurePartResult);
        }
        for (Future<P> future : futures) {
            try {
                collectMainResult(future.get(), mainResult);
            } catch (Exception e) {
                logger.error("Error during executing a thread of analyzer");
            }
        }
        logger.info("End text analyzing: " + primer.getTextName() + " - " + LinguisticUtils.getFormattedIntervalFromCurrent(textStarted));
        return mainResult;
    }

    private synchronized void markProgress() {
        progress++;
        if (progressor != null) {
            progressor.markProgress((progress + 1.0) / totalWork);
        }
    }

    public abstract P analyzePart(String textName) throws LinguisticsAnalyzingException;

    public abstract T collectMainResult(P partResult, T currentResult);

    public abstract T collectPartResult(P partResult, P currentResult);

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
