package gnomenwald;

public class NotFoundException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final String fake ;
	
	public NotFoundException () {
		super() ;
		this.fake = null ;
	}
}
