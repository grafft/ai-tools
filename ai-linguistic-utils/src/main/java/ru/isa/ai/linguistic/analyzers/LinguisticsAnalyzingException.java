package ru.isa.ai.linguistic.analyzers;

public class LinguisticsAnalyzingException extends Exception {
	private static final long serialVersionUID = -1746765698641972017L;

	public LinguisticsAnalyzingException() {
		super();
	}

	public LinguisticsAnalyzingException(String message) {
		super(message);
	}

	public LinguisticsAnalyzingException(String message, Throwable cause) {
		super(message, cause);
	}

	public LinguisticsAnalyzingException(Throwable cause) {
		super(cause);
	}
}
