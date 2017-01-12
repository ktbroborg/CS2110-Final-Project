package gnomenwald;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Location {
	protected int limit ;
	protected AtomicInteger numOcc = new AtomicInteger() ;
	protected Vector<GnomeStat> log = new Vector<GnomeStat>() ;
	protected Location next, prev ;
	protected String name, occText = "" ;
	protected long delay ;
	protected boolean gate ; //true when closed, false when open
	
	public String getName(){
		return name ;
	}
	public int getLimit(){
		return limit ;
	}
	public AtomicInteger numOcc(){
		return numOcc ;
	}
	public void visit( Gnome g ){
		numOcc.incrementAndGet() ;
		this.occText = "(" + numOcc.get() + "/" + limit + ")" ;
		log.add( new GnomeStat( g ) );
	}
	public void stay( Gnome g ){
		for( int i = 0 ; i < log.size() ; i ++ ){
			if( log.get( i ).getGnome().equals( g ) ) log.get( i ).setStatus( "in" );
		}
	}
	public void leave( Gnome g ){
		numOcc.decrementAndGet() ;
		this.occText = "(" + numOcc.get() + "/" + limit + ")" ;

		for( int i = 0 ; i < log.size() ; i ++ ){
			if( log.get( i ).getGnome().equals( g ) ) log.get( i ).setStatus( "gone" ) ;
		}
	}
	public Vector<GnomeStat> getLog(){
		return log ;
	}
	public Vector<Gnome> getCurrentOcc(){ //gnomes currently at this location
		Vector<Gnome> temp = new Vector<Gnome>() ;
		for( GnomeStat gs : log ){
			if( gs.getStatus().equals( "in" ) || gs.getStatus().equals( "new" ) ){
				temp.add( gs.getGnome() ) ;
			}
		}
		
		return temp ;
	}
	public Location getNext(){
		return next ;
	}
	public Location getPrev(){
		return prev ;
	}
	public long getDelay(){
		return delay ;
	}
	public void setOccText( String s ){
		this.occText = s ;
	}
	public String getOccText(){
		return occText ;
	}
	public void closeGate(){
		this.gate = true ;
	}
	public void openGate(){
		this.gate = false ;
	}
	public Boolean gateOpen(){
		return gate == false ;
	}
	public ArrayList<Gnome> clear(){
		ArrayList<Gnome> temp = new ArrayList<Gnome>() ;
		for( GnomeStat gc : log ){
			if( gc.getStatus().equals( "in" ) || gc.getStatus().equals( "new" ) ){
				gc.getGnome().setLocation( null );
				temp.add( gc.getGnome() ) ;
				numOcc.decrementAndGet() ;
//				System.out.println( this.getName() + " has " + numOcc.get() );
			}
		}
//		System.out.println( this.getName() + " has been cleared ");
		return temp ;
	}
}
