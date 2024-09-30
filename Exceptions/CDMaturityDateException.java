
public class CDMaturityDateException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public CDMaturityDateException(){
		super("Error: CD MaturityDate not reached!");
	}
}
