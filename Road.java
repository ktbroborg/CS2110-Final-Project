package gnomenwald;

public class Road extends Location{
	private int toll ;
	private int tempCost ;
	
	public Road(){
		super.delay = (int) (Math.random() * 500 + 2000 ) ;
		this.toll = 1000 ;
	}
	
	public Road( Village start, Village end ){
		super.delay = (int) (Math.random() * 500 + 2000 ) ;
		super.gate = false ;
		super.limit = 3 ;
		super.numOcc.set( 0 ) ;
		super.prev = start ;
		super.next = end ;
		super.occText = " (0/3)" ;
		this.toll = (int) (Math.random() * 50) + 10 ;
	}
	
	public String getName(){
		return super.prev.getName() + "--" + super.next.getName() ;
	}
	//travel-related
	public int toll(){
		return toll ;
	}
	
	//map-related

	public void remove(){
//		System.out.println( "removing " + this.getName() );
		Village temp = (Village) prev ;
		temp.removeConnection( this );
		temp = (Village) next ;
		temp.removeConnection(this);
	}
	public Village getStart(){
		return (Village) prev ;
	}
	public Village getEnd(){
		return (Village) next ;
	}
	public void setTempCost( int i ){
		this.tempCost = i ;
	}
	public int getTempCost(){
		return tempCost ;
	}
	public void resetTempCost(){
		this.tempCost = toll ;
	}
}
