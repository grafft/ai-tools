package ru.isa.ai.linguistic;

public interface ILinguisticAnalyzer<T> {

	void analyze(SNCPrimer prm) throws LinguisticsAnalyzingException;

	void close();

   T getResult();
}
