package gnomenwald;

public class Matrix {
	private double[] row1, row2, row3 ;

	public Matrix ( double[][] rows ){
		this.row1 = rows[0] ;
		this.row2 = rows[1] ;
		this.row3 = rows[2];
	}
	
	public void print(){
		System.out.println( "| " + row1[ 0 ] + " " + row1[ 1 ] + " " + row1[ 2 ] + " |\n" +
				"| " + row2[ 0 ] + " " + row2[ 1 ] + " " + row2[ 2 ] + " |\n" + 
				"| " + row3[ 0 ] + " " + row3[ 1 ] + " " + row3[ 2 ] + " |" );
	}
	
	public double det(){
		double det = 0 ;
		for( int i = 0 ; i < 3 ; i ++ ){ //multiplication by diagonals
			det += row1[ i % 3 ] * row2[ ( i+1 ) % 3 ] * row3[ ( i+2 ) % 3 ] ;
			det -= row1[ ( i+2 ) % 3 ] * row2[ ( i+1 ) % 3] * row3[ ( i ) % 3] ;
			}
		return det ;
	}
	
	public Matrix replaceCol( int c, double[] vec ){
		Matrix temp = new Matrix( new double[][] { row1.clone(), row2.clone(), row3.clone() } );
		
		for( int i = 0 ; i < 3 ; i ++ ){
			temp.rows()[ i ][ c - 1 ] = vec[ i ] ;
		}
		
		return temp ;
	}
	
	public double[][] rows(){
		return new double[][] { row1, row2, row3 } ;
	}
}
