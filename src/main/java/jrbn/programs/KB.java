package jrbn.programs;

public class KB {

	private static KB facts;

	private KB() {
	}

	public static KB getInstance() {
		if (facts == null)
			facts = new KB();
		return facts;
	}

	public void load(String path) {

	}

}
