package exceptions;

public class SportNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SportNotFoundException(long id) {
		super(String.format("Could not find sport %s", id));
	}
}

