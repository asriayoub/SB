package Exception;

public class NotRegistredException extends Exception{
	private static final long serialVersionUID = 1L;
	
	public NotRegistredException(){
		System.out.println("Player is not yet registred..");
	}

}
