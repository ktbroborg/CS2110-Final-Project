package gnomenwald;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JPanel;

public class GraphPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int width ;
	int height ;
	ArrayList<Node> nodes = new ArrayList<Node>() ;
	ArrayList<Edge> edges = new ArrayList<Edge>() ;
	
	class Node{
		int x,y ;
		String name ;
		
		public Node( String name, int x, int y ){
			this.name = name ;
			this.x = x ;
			this.y = y ;
		}
	}
	
	class Edge{
		int st, en; //for start end end
		
		public Edge( int st, int en ){
			this.st = st ;
			this.en = en ;
		}
	}
	
	public void addNode( String name, int x, int y){
		nodes.add( new Node( name, x, y ) ) ;
	}
	public void addEdge( int st, int en ){
		edges.add( new Edge( st, en ) ) ;
	}
	
	@Override
	public void paint( Graphics g ){	
		super.paintComponent( g );
		
		for ( Edge e : edges ){
			g.drawLine( nodes.get( e.st ).x + 25, nodes.get( e.st ).y + 25, 
					nodes.get( e.en ).x + 25, nodes.get( e.en ).y + 25 );
		}
		
		int nHeight = 50 ;
		int nWidth = 50 ;

		for( Node n : nodes ){
			g.setColor( Color.gray );
			g.fillOval( n.x, n.y, nWidth, nHeight);
			g.setColor( Color.white );
			g.drawString( n.name , n.x + 25, n.y+15);
		}
	}


}
