package exceptions;

public class GameNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GameNotFoundException(long id) {
		super(String.format("Could not find game %s", id));
	}
}
