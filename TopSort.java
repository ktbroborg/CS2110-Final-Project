package gnomenwald;

import java.util.ArrayList;
import java.util.Stack;


public class TopSort {
	private Map map ;
	
	public TopSort( Map map ){
		this.map = map ;
	}
	
	public ArrayList<Village> sort() throws CycleException {
		Stack<topObj> sorting = new Stack<topObj>() ;
		ArrayList<Village> temp = new ArrayList<Village> () ;
		topObjList toSort = new topObjList () ;
		
		for( Village v: map.getVillages() ){ //initialize
			toSort.list.add( new topObj( v ) );
		}
		
		boolean cycle = false ;

		while( toSort.list.size() > 0 && !cycle ){
			cycle = true ; //set to false if vertex with indegree 0 is found
			for( int i = toSort.list.size() - 1 ; i >= 0 ; i -- ){ //put all with indegree 0 on the stack
				topObj o = toSort.list.get( i ) ;
				if( o.inDeg == 0 ) {
					temp.add( o.vertex ) ;
					sorting.push( o ) ;
					toSort.list.remove( o ) ;
					cycle = false ;
				}
			}
			while( ! sorting.isEmpty() ){ //take all out of stack
				//reduce indegree of nexts ( all adjacents are nexts )
				for( Road rd : sorting.pop().vertex.getNexts() ){
					topObj o = toSort.get( rd.getEnd() ) ;
					if( o != null ) o.decrement();
				}
			}
		}
		
		if( cycle ){
			throw new CycleException() ;
		}
		else
			return temp ;
	}
	class topObjList{
		private ArrayList< topObj > list  = new ArrayList< topObj >() ;
		
		topObjList(){
			super() ;
		}
		
		public topObj get( Village v ){
			for( topObj o : list ){
				if( o.vertex.equals( v ) ) return o ;
			}
			return null ;
		}
	}
	class topObj{
		private int inDeg ;
		private Village vertex ;
		
		topObj( Village v ){
			this.vertex = v ;
			this.inDeg = v.getConnections().size() - v.getNexts().size() ;
		}
		
		private void decrement(){
			this.inDeg -- ;
		}
	}
}
