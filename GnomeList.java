package gnomenwald;

import java.util.Vector;

public class GnomeList {
	private Vector<Gnome> gnomes ;
	
	public GnomeList(){
		this.gnomes = new Vector<Gnome>() ;
	}
	
	public void add( Gnome g ){
		//adds a gnome to the list
		gnomes.add( g ) ;
	}
	
	public Vector<Gnome> getList(){
		return gnomes ;
	}
	
	public Gnome getGnome( String name ) throws NotFoundException { 
		//if have time --edit to make more private //used only in GUI for updating 
		for( Gnome g : gnomes ){
			if( g.heisst().equals( name ) ) return g ;
		}
		throw new NotFoundException() ;
	}
	
	public void assignAuth(){
		int n = 3 ;
		//parabola:: y = a( x - h )^2 + k
		double[] code = new double[ 3 * n - 1 ] ;
		for( int i = 0 ; i < code.length ; i ++ ){
			code[ i ] = Math.random() ; //as long as they're different it doesn't matter
		}
		
		for( Gnome g : gnomes ){
			int group = gnomes.indexOf( g ) % n + 1 ; //groups go from 1 to n
//			System.out.println( "glist - " + g.heisst() + " assigned group " + group );
			g.assignGroup( group ); //for hacking purposes if needed
			
			double x = Math.random() * 600 ; //why not
			double y = x ;
			y -= code[ group % n ] ; // ( x - h ) 
			y *= y ; //square it
			y *= code[ group % n + 1 ] ; // a( x - h  )^2
			y += code[ group % n + 2 ] ; // a( x - h )^2 + k 
			g.assignKey( x , y, 453.6);
		}
		
	}
	public Location find( Gnome g, Gnome auth1, Gnome auth2, Gnome auth3 ) 
			throws AuthorizationFailureException{
		//check for qualifications of the 2 other gnomes
		return g.getLocation( auth1, auth2, auth3 ) ;
	}

	
}
