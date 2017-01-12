package gnomenwald;

public class AuthorizationFailureException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final String fake ;
	
	public AuthorizationFailureException () {
		super() ;
		this.fake = null ;
	}
}
