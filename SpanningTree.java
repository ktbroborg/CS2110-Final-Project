package gnomenwald;

import java.util.ArrayList;

public class SpanningTree {
	private ArrayList<Road> minRDs = new ArrayList<Road>() ;
	private ArrayList<Village> reached = new ArrayList<Village>() ;
	private ArrayList<Village> unreached = new ArrayList<Village>() ;
	private Map map ;
	
	public SpanningTree( Map map ){
		this.map = map ;	
	}
	
	public ArrayList<Road> getMinRDs(){
		return minRDs ;
	}
	public void prims(){
		reached.add( map.getVillages().get( 0 ) ) ;
		for( int i = 1 ; i < map.getVillages().size() ; i ++ ) {
			unreached.add( map.getVillages().get( i ) ) ;
		}
		
		while( !unreached.isEmpty() ){
			//find lowest-toll road that starts in a reached node + ends in unreached + add to tree
			Road temp = primGetNextRD() ;
			minRDs.add( temp ) ;
		}
		
		for( int i = map.getRoads().size() - 1 ; i >= 0 ; i -- ){
			Road temp = map.getRoads().get( i ) ;
			if( minRDs.indexOf( temp ) < 0 ) map.remove( temp );
		}
	}
	private Road primGetNextRD(){ //for prim's (ignoring directedness of graph)
		Road nextRD = new Road() ; //this toll will be higher than any other, so won't be returned
		for( Village a : reached ){
			if ( ! a.getConnections().isEmpty() ){ //if have removed all connections to it………
				for( Road b : a.getConnections() ){ //if one of the ends of the road is unreached	
					if( ( unreached.indexOf( b.getEnd() ) >= 0 || unreached.indexOf( b.getStart() ) >= 0 )
							&& b.toll() < nextRD.toll() ){
						nextRD = b ;
					}
				}
			}
		}
		
		if( reached.indexOf( nextRD.getEnd() ) >= 0 ){ //the end is in reached
			unreached.remove( nextRD.getStart() ) ;
			reached.add( nextRD.getStart() ) ;
		}
		else{ //the start of the road is in reached
			unreached.remove( nextRD.getEnd() ) ;
			reached.add( nextRD.getEnd() ) ;
		}
		return nextRD ;
	}

//	public ArrayList<Road> edmonds( Village start ){
//		ArrayList<Road> temp = new ArrayList<Road> () ;
//
//		for( Village v : map.getVillages() ){
//			if( ! v.equals( start ) ){
//				int minCost = minCost( v ) ; //min cost of edge entering v
//				for( Road rd : v.getConnections() ){
//					if( rd.getEnd().equals( v ) ){ //road entering v
//						reduceCost( rd, minCost );
//					}
//				}
//				temp.add( chooseZeroCostRd( v )) ;
//			}
//		}
//		if( check( temp) ) return temp ;
//		else{
//			Map tempMap = contract( temp ) ;
//			
//		}
//		
//		return temp ;		
//	}
	
	public dijList dijkstra( Village st ){
		
		dijList info = new dijList() ;
		for( Village v : map.getVillages() ){
			info.add( new dijObj( v ) ) ;
			if( v.equals( st ) ) info.get( v ).cost = 0 ;
		} //initialize info such that starting village has cost 0 and everything else has cost -1
		
		while( reached.size() < map.getVillages().size() ){
			Village v = dijGetNext( info ) ;
//			System.out.println( "Village " + v.getName() + " has been reached ");
			reached.add( v ) ;
			long c = info.get( v ).cost ; //compare to this value
			for( Road rd : v.getNexts() ){
				if( info.get( rd.getEnd() ).cost < 0 || 
						( c + rd.getDelay() ) < info.get( rd.getEnd() ).cost){
					info.get( rd.getEnd() ).cost = c + rd.getDelay() ;
					info.get( rd.getEnd() ).from = v ;
//					System.out.println( rd.getEnd().getName() + " assigned from " + v.getName() );
				} //else we don't want to change it
			}
		}
		
		map.getShortest().add( info ) ;
		return info ;
	}
	
	private Village dijGetNext( dijList info ){
		for( dijObj o : info.list ){
			if( ! o.seen && o.cost >= 0 ) {
				o.seen = true ;
				return o.vertex ;
			}
		}
		return null ;
	}
	
	public class dijList{
		private ArrayList< dijObj > list = new ArrayList< dijObj > ();
		
		dijList(){
			super() ;
		}
		
		public Village getStart(){
			return list.get( 0 ).vertex ;
		}
		
		public dijObj get( Village v ){
			for( dijObj o : list ){
				if( o.vertex.equals( v ) ) return o ;
			}
			return null ;
		}
		
		private void add( dijObj o ){
			list.add( o );
		}
		public ArrayList<dijObj> list(){
			return list; 
		}
	}
	
	public class dijObj{
		private Village vertex ;
		private long cost ;
		private Village from ;
		private boolean seen ;
		
		public dijObj( Village vertex ){
			this.vertex = vertex ;
			this.cost = -1 ;
			this.from = null ;
			this.seen = false ;
		}
		
		public Village getFrom(){
			return from ;
		}
		
	}
}
