package exceptions;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String userName) {
		super(String.format("Could not find user %s", userName));
	}	
}
