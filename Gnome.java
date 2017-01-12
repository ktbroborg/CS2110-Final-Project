package gnomenwald;

import java.util.ArrayList;

public class Gnome implements Runnable {
	private Thread thread ;
	private int urgency ; //urgency is rated 2 - 0 from desperate to lazy
	private double t ; //time spent
	private Village destination ;
	private ArrayList<Road> route ; private boolean enRoute ;
	private Location location ;
	private int cash ; private boolean broke ;
	private ArrayList<String> visited = new ArrayList<String>();
	private String name ;
	private String password = "00" ; //basically to hack into them
	private boolean running ;
	
	private double[] key  = new double[2] ; private int group ;
	
	public Gnome( String name ){
		this.name = name ;
		this.cash = (int) (Math.random() * 300 + 70) ;
		this.urgency = (int) (Math.random() * 3 ) ;
		this.broke = false ; 
		this.enRoute = false ;
	}
	
	//basics
	public String heisst(){
		return name ;
	}
	public void reName( String name ){
		this.name = name ;
	}
	public ArrayList<Road> getRoute(){
		return route ;
	}
	public double getProgress(){ //should return percentage along a road (when on a road)
		if( ( System.currentTimeMillis() - t ) < ( location.getDelay() / ( urgency + 1 ) ) )
				return ( System.currentTimeMillis() - t )/( location.getDelay() / ( urgency + 1 )) ;
		return 1 ;
	}
	public void terminate(){
		this.running = false ;
		thread.interrupt(); 
	}
	public void run() {
		//while something–the gnome travels randomly
		try {
			Thread.sleep( (long) Math.random() * 1000 );
		} catch (InterruptedException e) {
			System.out.println( heisst() + " interrupted at start");
			} //stagger start times
		while( running ){
			try{
				t = System.currentTimeMillis() ;
				if ( location == null ) {
					Thread.sleep( 500 ) ;
				}
				else if ( broke && location instanceof Village ){
					Thread.sleep( 3000 );
				}
				else if ( enRoute && ! route.isEmpty() ){
					nextOnRoute() ;
					if( location instanceof Road ){
						Thread.sleep( location.getDelay() / ( urgency + 1 ) );
						//time = d/r, so road's "delay" is more like "distance"
					}
					else{
						Thread.sleep( location.getDelay() - ( long ) ( urgency * .3 ) ); 
					}
				}
				else if( location.getNext() != null ){//not at a dead end
					enRoute = false ;
					if( location.gateOpen() ) {
						visit( location.getNext() ) ;
						if( location instanceof Road ){
							Thread.sleep( location.getDelay() / ( urgency + 1 ) );
							//time = d/r, so road's "delay" is more like "distance"
						}
						else{
							Thread.sleep( location.getDelay() - ( long ) ( urgency * .3 ) ); 
						}
					}
				}
			}
			catch( InterruptedException e ){
				System.out.println( name + " has been interrupted" );
			}
			
		}
	}
	
	public void start(){
		this.running = true ;
		System.out.println("Starting " +  name );
	      if (thread == null) {
	    	  thread = new Thread (this, name);
	    	  thread.start ();
	      }
	}
	
	//travel-related
	public void setLocation( Location loc ){
		this.location = loc ;
		if( loc == null ){
			visited.add( "never never land" ) ;
		}
		else
			visited.add( loc.getName() ) ;
		//also reset route
		this.route = new ArrayList<Road>() ;
	}
	public void visit( Location loc ){
		while( loc.numOcc().get() >= loc.getLimit() || !loc.gateOpen() ){
			try{
				Thread.sleep( 500 ); //can't visit when the village is full
			}
			catch( InterruptedException e){
				System.out.println( name + " interrupted");
			}
		}
		if( loc instanceof Road ){
			Road temp = (Road) loc ;

			try {
				spend( temp.toll() ) ; //exception thrown here
				loc.visit( this );
				location.leave( this );
				this.location = loc ;
				System.out.println( name + " paid toll - "
						+ "now visiting " + loc.getName() + " " + loc.getOccText() ); 
			} catch (NotEnoughMoneyException e) {
				System.out.println( heisst() + " >> not enough cash, trying to reroute");
				enRoute = false ; route = new ArrayList<Road> () ; //remove from route
				for( Road rd : temp.getStart().getNexts() ){
					if( cash > rd.toll() ){
						visit( rd ) ;
					}
				}
				System.out.println( heisst() + " does not have money to go anywhere");
				broke = true ;
			}
		}
		else{ //visiting a village w/ no toll
			loc.visit( this );
			if( location != null ) 
				location.leave( this );
			this.location = loc ; visited.add( location.getName() ) ;
			System.out.println( name + " visiting " + loc.getName() + " " + loc.getOccText() ); 
		}
	}

	public void setUrgency( int u ){
		this.urgency = u ;
	}
	public int getUrgency(){
		return urgency ; 
	}
	
	public void nextOnRoute(){
		if ( location instanceof Road ){ //set route puts gnome on route.get( 0 ).getStart()
			visit( ((Road) location).getEnd() ) ;
			route.remove( 0 ) ; //gone through the first road
		}
		else if ( location instanceof Village ){
			visit( route.get( 0 ) ) ;
		}
	}
	public void setRoute( ArrayList<Road> newRoute ){ 
		//newRoute should start at the current location
		//next time gnome moves it'll go to the next location on the route 
		this.route = newRoute ; 
		this.location = route.get( 0 ).getStart();
		this.enRoute = true ;
	}
	
	//rerouting and such
	public Village destination(){
		return destination ;
	}
	public void setDestination( Village v ){
		this.destination = v ;
	}
	
	//finance-related methods
	public int cash(){
		return cash ; //should be protected in some way
	}
	private void spend( int amt ) throws NotEnoughMoneyException {
		if( cash > amt ) {
			this.cash -= amt ;}
		else throw new NotEnoughMoneyException() ;
	}
	public void addCash( int amt ){
		this.cash += amt ;
		this.broke = false ;
	}
	public boolean broke(){
		return broke ; 
	}
	
	//authorization + finding
	public void assignGroup( int gr ){
		this.group = gr ;
	}
	
	public int group(){
		return group ;
	}
	
	public double[] getKey(){
		return key ;
	}
	
	public void assignKey( double x, double y, double pass ){
		//password for pseudo-security lol
		if( (int) Math.sqrt( pass ) == 21 ){
//			System.out.println( "password ok!!" );
			this.key[0] = x ;
			this.key[1] = y ;
		}
	}
	
	public Location getLocation( Gnome auth1, Gnome auth2, Gnome auth3 )
			throws AuthorizationFailureException{
		/* calculate parabola defined by keys of auth1, auth2, and auth3
		 * check if this gnome's key is on the same parabola
		 * uses cramer's rule and parabola form A x^2 + B x + C = y to solve for A, B, C
		 * if it is - return location, else throw an exception
		 */
		double x1 = auth1.getKey()[ 0 ], y1 = auth1.getKey()[ 1 ];
		double x2 = auth2.getKey()[ 0 ], y2 = auth2.getKey()[ 1 ];
		double x3 = auth3.getKey()[ 0 ], y3 = auth3.getKey()[ 1 ];
		
		double[] ansCol = new double[] { y1, y2, y3 } ;

		Matrix coeff = new Matrix ( new double[][] {
			{ x1*x1, x1, 1 },
			{ x2*x2, x2, 1 },
			{ x3*x3, x3, 1 }
			}) ;
		double coDet = coeff.det() ;
		
		Matrix A = coeff.replaceCol( 1, ansCol ) ; 
		Matrix B = coeff.replaceCol( 2, ansCol ) ;
		Matrix C = coeff.replaceCol( 3, ansCol ) ;

		double a = A.det() / coDet ;
		double b = B.det() / coDet ;
		double c = C.det() / coDet ;
		
		double x = key[ 0 ] ; double y = key[ 1 ] ;
		
		if( (int) ( a * x * x + b * x + c ) == (int) y ){
			return this.location ;
		}
		else 
			throw new AuthorizationFailureException() ;
	}
	
	public Village getRecentVillage( String pass ){
		if( checkPassword( pass ) ){
			if ( location instanceof Village ) return (Village) location ;
			else
				return ((Road) location).getStart() ;
		}
		return null ;
	}
	
	public Boolean checkPassword( String pass ){
		return pass == password ;
	}
	public ArrayList<String> getVisited( String pass ){
		if( checkPassword( pass ) ) {
			return visited ;
		}
		else {
			return null ;
		}
	}
}
