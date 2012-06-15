package ru.isa.ai.linguistic.analyzers;

import com.google.common.util.concurrent.FutureCallback;
import ru.isa.ai.linguistic.data.SNCPrimer;

public interface ILinguisticAnalyzer<T> {

    void analyze(SNCPrimer prm, FutureCallback<T> callback);

    void close();

}
