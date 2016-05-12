package Exception;

public class NameExistsException extends Exception{
	private static final long serialVersionUID = 1L;

	public NameExistsException(){
		System.out.println("Name allready exists...");
	}
}
