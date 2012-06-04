package ru.isa.ai.linguistic;

public interface ILinguisticAnalizer<T> {
	void open();

	void analize(SNCPrimer prm) throws LinguisticsAnalizingException;

	public void analize() throws LinguisticsAnalizingException;
	
	void close();

   T getResult();
}
