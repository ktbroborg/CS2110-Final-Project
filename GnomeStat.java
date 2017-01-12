package gnomenwald;

public class GnomeStat {
	private Gnome gnome ;
	private String status ; //"gone", "in", "new"
	
	public GnomeStat( Gnome g ){
		this.gnome = g ;
		this.status = "new" ;
	}
	public void setStatus( String status ){
		this.status = status ;
	}
	public String getStatus(){
		return status ;
	}
	public Gnome getGnome(){
		return gnome ;
	}
	
}
