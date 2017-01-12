package gnomenwald;

public class CycleException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
public final String fake ;
	
	public CycleException () {
		super() ;
		this.fake = null ;
	}
}
