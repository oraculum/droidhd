package oraculum.droid.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class ValidationError extends Exception {
	/*
	 * classe usada para representar os erros de validação multiplos
	 * 
	 * @addMessage(String msg) usada para adicionar mais um erro
	 * 
	 * @toString() junta todos os erros em uma unica string
	 */
	private List<String> list;

	public ValidationError() {
		super("Erro de validação");
		list = new ArrayList<String>();
	}

	public void addMessage(String msg) {
		list.add(msg);
	}

	public int getLength() {
		return list.size();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next());
			if (iter.hasNext()) {
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}
}
