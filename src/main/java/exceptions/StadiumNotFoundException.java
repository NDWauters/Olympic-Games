package exceptions;

public class StadiumNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public StadiumNotFoundException(long id) {
		super(String.format("Could not find stadium %s", id));
	}
}

