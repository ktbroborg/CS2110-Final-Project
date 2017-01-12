package gnomenwald;

import java.util.ArrayList;
import java.util.Vector;

import gnomenwald.SpanningTree.dijList;

public class Map {
	private Vector<Village> villages = new Vector<Village>() ;
	private Vector<Road> roads = new Vector<Road>() ;
	private ArrayList< dijList > shortest = new ArrayList< dijList >() ;
	
	private int numV ; //total number of villages
	
	public Map(){
		this.numV = 0 ;
	}
	public Map( int num ){
		this.numV = 0 ;
		int i = 0 ;
		while( i < num ){
			Village temp = new Village() ;
			this.add( temp );
			i++;
		}
//		build() ;
	}
	
	public ArrayList< dijList > getShortest(){
		return shortest ;
	}
	
	public Village getV( String name ) throws NotFoundException{
		for( Village v : villages ){
			if( v.getName().equals( name ) ){
				return v ;
			}
		}
		throw new NotFoundException() ;
	}
	
	public Road getRoad( Village st, Village en ){
		for( Road rd : st.getNexts() ){
			if( rd.getEnd().equals( en ) ) return rd ;
		}
		return null ;
	}
	
	public int getNumV(){
		return numV ;
	}
	public Vector<Village> getVillages(){
		return villages ;
	}
	public Vector<Road> getRoads(){
		return roads ;
	}
	
	//basics ( add + remove )
	public void add( Village v ){
		//build it so that it has at least one road to/from every other village
		if( numV < 1 ){
			numV++ ;
			villages.add( v );
		}
		else{ //first village has no one to build roads to
			Village temp1 = getRandomVillage() ;
			Village temp2 = getRandomVillage() ;
			connect( v, temp1 ) ;
			connect( temp2, v );
			numV++ ;
			villages.add( v );
		}
	}
	public void connect( Road rd ){
		//method to add a road
		rd.getStart().addNext( rd );
		roads.add( rd ) ;
	}
	public Road connect( Village v1, Village v2 ){
		//connects two villages, directed from v1 to v2
		Road temp = new Road( v1, v2 ) ;
		v1.addNext( temp ); //should be neighbors now, because of addnext function
		roads.add( temp ) ;
		return temp ;
	}
	public void remove( Road rd ){
		rd.closeGate();
		rd.clear();
		rd.remove();
		roads.remove( rd ) ;
		
	}
	public ArrayList<Gnome> remove1( Village v ) {
		//remove village and remove any roads that lead to it
		if( !villages.isEmpty() ){
			ArrayList<Gnome> tempHousing = new ArrayList<Gnome>() ;
			
			v.shutdown(); //close gates on v and surrounding roads
			tempHousing.addAll( v.clear() ) ;	
			
			for( int i = v.getConnections().size() - 1 ; i >= 0 ; i-- ){
				Road rd = v.getConnections().get( i ) ;
				tempHousing.addAll( rd.clear() ) ;
				roads.remove( rd );
				rd.remove() ;
			}
			//all gnomes in village and surrounding roads gathered, locations should be null
			
			numV --;
			villages.remove( v ) ; //remove village
			return tempHousing ;
		}
		return null ;
	}
	
	public ArrayList<Gnome> remove2( Village v ) {
		//remove village and have villages connected through it be connected directly
		if( !villages.isEmpty() ){
	ArrayList<Gnome> tempHousing = new ArrayList<Gnome>() ;
			
			v.shutdown(); //close gates on v and surrounding roads
			tempHousing.addAll( v.clear() ) ;	
			
			ArrayList<Village> startV = new ArrayList<Village>() ;
			ArrayList<Village> endV = new ArrayList<Village>() ;

			for( int i = v.getConnections().size() - 1 ; i >= 0 ; i -- ){
				Road temp = v.getConnections().get( i ) ;
				tempHousing.addAll( temp.clear() ) ;
				if( temp.getStart().equals( v ) ) 
					endV.add( temp.getEnd() ) ;
				else
					startV.add( temp.getStart() );
				roads.remove( temp );
				remove( temp );
			}
			//all gnomes in village and surrounding roads gathered, locations should be null
			
			for( Village s : startV ){
				for( Village e : endV ){
					if( ! s.equals( e ) )
						connect( s, e ) ;
				}
			}
			
			numV --;
			villages.remove( v ) ; //remove village
			return tempHousing ;
		}
		return null ;
	}
	
	//other methods
	public void plant( Gnome g ){//for starting them out 
		//stick gnomes at random villages in the map
		int x = (int) (Math.random() * villages.size() ) ;
		System.out.println( g.heisst() + " planted");
		g.visit( villages.get( x ) );
	}
	
	public void reRoute( Gnome g, Village st, Village en ){
		System.out.println( "route from " + st.getName() + " to " + en.getName() );
		
		SpanningTree span = new SpanningTree( this ) ;
		dijList info = dijCheck( st ) ;
		
		if( info == null ){
			info = span.dijkstra( st ) ;
		}
		
		ArrayList<Road> route = new ArrayList<Road> () ;
		
		Village end = en ;
		Village from = info.get( en ).getFrom() ;
		while( ! st.equals( end  )){
//			System.out.println( from.getName() + " -- " + end.getName() + " added" );
			Road rd = getRoad( from, end );
			route.add( 0, rd ) ;
			Village temp = info.get( from ).getFrom() ;
			end = from ;
			from = temp ;
		}
		
//		for( Road rd : route ){
//			System.out.println( rd.getName() + " !!!!! ");
//		}
//		
		g.setRoute( route );
	}
	
	private dijList dijCheck( Village st ){ //check if tree already done
		for( dijList li : shortest ){
			if( li.getStart().equals( st ) ) return li ;
		}
		return null ;
	}
	
	public Village getRandomVillage(){
		int x = (int) (Math.random() * villages.size() ) ;
		return villages.get( x ) ;
	}
	
	public void build(){
		/* have a list of roads minRDs that starts empty
		 * builds a road from each village to every other
		 * sort roads from lowest cost to highest
		 * take the lowest cost road 
		 * 		- if the two villages are not connected( directly or indirectly ) add it to minRDs
		 * 		- continue until all villages are able to get to any other village
		 */
	}
}
