package gnomenwald;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Village extends Location {
	private Color[] colors = { Color.web( "#e8c47d" ), Color.web( "#7de8e8" ), Color.web( "#f07575" ),
			Color.web( "#b3e87d" ), Color.web( "#e87de8" ) } ;
	private Color color ;
	private static int numVev ; //stands for number of Villages ever, so ok even after removing villages
	private static int[] vNames = new int[ 200 ];//initialize them
	private static double[] vX = new double[ 200 ], vY = new double[ 200 ] ; //for coordinates
	private double x,y ;
	private ArrayList<Road> nexts = new ArrayList<Road>() ; //the roads that start in this village
	private ArrayList<Road> connections = new ArrayList<Road>() ; 
		//all the ones that are 1 away, regardless of road direction
	
	static{
		for (int i = 0 ; i < vNames.length ; i ++ ){
			vNames[ i ] = i+1 ; //we want the first village to be "1" not "0"
		}
	}
	
	static{ //calculating coordinates ( should be in consecutive circles w/ each circle having 8 )
		for (int i = 0; i < vX.length ; i ++ ){ 
			double r = i / 8 + 1 ;
			double a = i % 8 ;
			vX[ i ] = ( r * 80 ) * ( Math.cos( a * ( 2 * Math.PI / 8 ) ) ) + 300 ;
			vY[ i ] = ( r * 80 ) * ( Math.sin( a * ( 2 * Math.PI / 8 ) ) ) + 300 ;
		}
	}
	
	//constructor methods
	public Village(){
		this.color = colors[ numVev % colors.length ];
		super.gate = false ;
		super.limit = (int) ( Math.random() * 5 + 3 ) ;
		super.numOcc.set( 0 ) ;
		super.name = Integer.toString( vNames[ numVev ] );//gets next name
		super.occText = "(0/" + limit + ")" ;
		super.delay = (int) ( Math.random() * 1200 + 2500 );
		this.x = vX[ numVev ] ; 
		this.y = vY[ numVev ] ;
		numVev ++ ;
	}
	public Village( int limit ){
		this.color = colors[ numVev % colors.length ];
		super.gate = false ;
		super.name = Integer.toString( vNames[ numVev ] );
		super.limit = limit ;
		super.numOcc.set( 0 );
		super.occText = " (0/" + limit + ")" ;
		super.delay = (int) ( Math.random() * 1200 + 2500 );
		this.x = vX[ numVev ] ; 
		this.y = vY[ numVev ] ;
		numVev ++ ;
	}
	
	//general
	public double getX(){
		return x ;
	}
	public double getY(){
		return y ;
	}
	public Color getColor(){
		return color ;
	}
	public int getNameInt(){ //in case this is useful at some point
		return Integer.parseInt( name ) ;
	}
	public synchronized ArrayList<Road> getConnections(){
		return connections ;
	}
	public ArrayList<Road> getNexts(){
		return nexts ;
	}
	public Road getNext(){//returns a random road that can be taken from this village
		int r = (int) (Math.random() * nexts.size() ) ;
		if ( nexts.size() <= 0 ){
			return null ;
		}
		return nexts.get( r ) ;
	}
	
	//map-related
	public void addNext( Road road ){
		if ( road.getStart().equals( this ) ){
			nexts.add( road );
			addConnection( road ) ;
			road.getEnd().addConnection( road ) ;
		}
		//if it doesn't start in this village, we don't want to add it
	}
	public void removeConnection( Road road ){
		connections.remove( road ) ;
		nexts.remove( road ) ;
	}
	public void shutdown(){
		closeGate() ;
		for( Road rd : connections ){
			rd.closeGate();
//			System.out.println( rd.getName() + " has been shutdown");
		}
	}
	
	public void addConnection( Road road ){
		if( connections.indexOf( road ) < 0 ){
			connections.add( road );
		}
		//else do nothing, because it's already in the list
	}
}
