public class AccountClosedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AccountClosedException() {
		super("Error: Account is Closed!");
	}
	public AccountClosedException(int accNumber) {
		super("Error: Account number " + accNumber + " is closed!");
	}
}
