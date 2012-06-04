package ru.isa.ai.linguistic;

import java.util.LinkedList;
import java.util.List;

public class SNCPrimer {
	private String textName;
	private List<String> parts = new LinkedList<String>();

	public List<String> getParts() {
		return parts;
	}

	public void setParts(List<String> sentences) {
		this.parts = sentences;
	}

	public void addPart(String sentence) {
		parts.add(sentence);
	}

	public String getTextName() {
		return textName;
	}

	public void setTextName(String textName) {
		this.textName = textName;
	}
}
