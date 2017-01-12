package gnomenwald;

import java.io.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application ;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class AppWindow extends Application {
	private static GnomeList gnomes = new GnomeList() ;
	private static Map map = new Map( 16 );
	private final int numGnomes = 15 ;

    private static final double CIRCLE_SIZE = 30; // default circle size
    private final int ARR_SIZE = 5 ; //for arrow size
    
	private final ObservableList<String> vStrings = FXCollections.observableArrayList() ;
	private final ObservableList<String> rStrings = FXCollections.observableArrayList() ;

    
	public static void main( String[] args ){
		launch() ;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle( "GnomenWald" ) ;
		stage.setOnCloseRequest( new EventHandler<WindowEvent>() {
			@Override
			public void handle( WindowEvent w ){
				Platform.exit();
				System.exit( 0 );
			}
		});
		
		//container for everything
		AnchorPane box = new AnchorPane() ;
		box.setStyle( "-fx-background-color: #e2e9e2;"  );
		
		//start/stop button
		HBox basics = new HBox() ;
		basics.setPrefSize( 800 , 100 );
		basics.setPadding( new Insets( 20, 20, 20, 20 ) );
		basics.setSpacing( 20 );
		
		//buttons
		Button startButton = new Button( "start" ) ;
		Label status = new Label("") ;
		status.setMaxWidth(Double.MAX_VALUE);
		Button stopButton = new Button( "stop" ) ; stopButton.setDisable( true );

		basics.getChildren().addAll( startButton, stopButton, status ) ;
		
		//graphics T-T
		Pane cPane = new Pane() ;
		cPane.setStyle("-fx-background-color: #ffffff;" );
		Canvas canvas = new Canvas( 600, 600 ) ;
		GraphicsContext gc = canvas.getGraphicsContext2D() ;
		cartography( gc );
		
		Timeline cTime = new Timeline( new KeyFrame( 
				Duration.millis( 250 ),
				ae -> {
					gc.clearRect( 0, 0, canvas.getWidth(), canvas.getHeight());
					cartography( gc );
					}
				)) ;
		cTime.setCycleCount( Animation.INDEFINITE );
		cTime.play();
		
		cPane.getChildren().add( canvas ) ;
		
		//menu pane (on right)
		VBox menu = new VBox() ;
		menu.setPadding( new Insets( 20, 20, 20, 20 ));
		menu.setSpacing( 20 );
		
		//buttons for menu (on right)
		Button editVillages = new Button( "village window" ) ;
		editVillages.setDisable( true );
		Button editGnomes = new Button( "gnome window" ) ;
		editGnomes.setDisable( true );
		
		//displaying info (on left)
		
		GridPane overview = new GridPane() ;
		overview.setPadding( new Insets( 20, 20, 20, 20 ));
		overview.setHgap( 10 );
		overview.setVgap( 10 );
		
		Label villageLabel = new Label( "Villages" ) ;
		Label roadLabel = new Label( "Roads" ) ;
		
		ListView<String> villageView = new ListView<String>( vStrings ) ;
		villageView.setEditable( true );
		villageView.setPrefSize( 80 , 600 );
		
		ListView<String> roadView = new ListView<String>( rStrings ) ;
		roadView.setEditable( true );
		roadView.setPrefSize( 150 , 600 );
		
		overview.add( villageLabel, 0, 0);
		overview.add( roadLabel, 1, 0);
		overview.add( villageView, 0, 1);
		overview.add( roadView, 1, 1);
		
		Timeline tl = new Timeline( new KeyFrame( 
				Duration.millis( 300 ),
				ae -> {
					vStrings.clear();
					for( Village v : map.getVillages() ){
						vStrings.add( v.getName() + " " + v.getOccText() ) ; 
					}
					rStrings.clear();
					for( Road r : map.getRoads() ){
						rStrings.add( "(" + r.toll() + ") " + r.getName() + " " + r.getOccText() ) ;
					}
				}
				)) ;
		tl.setCycleCount( Animation.INDEFINITE );
		tl.play();
		
		//add everything to box
		AnchorPane.setTopAnchor( basics , 0.0 ) ;
		AnchorPane.setLeftAnchor( basics , 320.0 );
		AnchorPane.setRightAnchor( basics , 320.0 );
		AnchorPane.setTopAnchor( menu , 0.0 );
		AnchorPane.setRightAnchor( menu , 50.0 ) ;
		AnchorPane.setLeftAnchor( overview, 10.0 ) ;
		AnchorPane.setTopAnchor( cPane , 110.0 );
		AnchorPane.setLeftAnchor( cPane, 300.0 );
		AnchorPane.setRightAnchor( cPane, 30.0 );
		box.getChildren().addAll( basics, menu, overview, cPane ) ;
		box.setPrefSize( 1000 , 800 );
		
		Scene appScene = new Scene( box ) ;
		stage.setScene( appScene );
		stage.show(); 
		
		//action listeners and such
		startButton.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent event) {
		    	makeGnomes( numGnomes ) ;
		    	runAll() ;
		    	startButton.setDisable( true ) ;
		    	status.setText( "Wilkommen zu GnomenWald");
		    	stopButton.setDisable( false ) ;
		    	editGnomes.setDisable( false ) ;
		    	editVillages.setDisable( false );
		    }
		}); 
		stopButton.setOnAction( new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				cTime.stop();
				tl.stop();
				for( Gnome g : gnomes.getList() ){
					g.terminate();
				}
				
				status.setText( "simulation stopped" );
				stopButton.setDisable( true );
				editGnomes.setDisable( true );
				editVillages.setDisable( true );
			}
		});
		
		editGnomes.setOnAction( new EventHandler<ActionEvent>(){
			 @Override 
			 public void handle(ActionEvent e) {
				 new GnomeWindow( gnomes, map ) ;
			    }
		});
		editVillages.setOnAction( new EventHandler<ActionEvent>(){
			 @Override 
			 public void handle(ActionEvent e) {
				 new VillageWindow( map ) ;
			    }
		});		menu.getChildren().addAll( editVillages, editGnomes ) ;
		
		
	}
	
	private void cartography( GraphicsContext gc ){
		
		
		
		for( Road r : map.getRoads() ){
			drawRoad( gc, r , r.getStart().getColor() );
		}
		
		gc.setStroke( Color.BLACK );
		for( Village v : map.getVillages() ){
			drawVillage( gc, v, v.getColor() ) ;

		}
		
	}
	void drawRoad(GraphicsContext gc, Road rd, Color c ) {
		gc.setStroke( c );
		gc.setFill( c );
	    
	    gc.save();
	    double off ;
	    double[] offsets = { 1.02 , 1.0 , 1.01, 1.015, 1.005 } ;
		off = offsets[ rd.getStart().getNameInt() % offsets.length ];
		
	    double dx = rd.getEnd().getX() - rd.getStart().getX(), 
	    		dy = rd.getEnd().getY() - rd.getStart().getY();
	    double angle = Math.atan2(dy, dx) * off ;
	    dx -= CIRCLE_SIZE/2 * Math.cos( angle ) ; //offset so between village circles
	    dy -= CIRCLE_SIZE/2 * Math.sin( angle ) ;
	    double len = Math.sqrt(dx * dx + dy * dy) ;

	    Transform transform = Transform.translate( rd.getStart().getX() + CIRCLE_SIZE/2, 
	    		rd.getStart().getY() + CIRCLE_SIZE/2 ) ;
	    transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
	    gc.setTransform(new Affine(transform));

	    gc.strokeLine(0, 0, len, 0);
	    gc.fillPolygon(new double[]{len, len - ARR_SIZE, len - ARR_SIZE }, 
	    		new double[]{0, -ARR_SIZE, ARR_SIZE }, 3);
	    
	    gc.setStroke( Color.BLACK );
	    
	    Object[] temp = rd.getCurrentOcc().toArray() ;
	    for( Object o : temp ){
	    	Gnome g = (Gnome) o ;
	    	double dist = g.getProgress() * ( len - 30 ) + 15 ;
	    	gc.strokeOval( dist + 7.5 , 0, 4, 4);

	    }
	    
	    gc.restore();
	}
	
	void drawVillage( GraphicsContext gc, Village v, Color c ){
		gc.setStroke( Color.BLACK);
		gc.setFill( c );
		
		gc.fillOval( v.getX(), v.getY(), CIRCLE_SIZE, CIRCLE_SIZE ) ;
		gc.strokeText( v.getName(), v.getX() + CIRCLE_SIZE, v.getY() + CIRCLE_SIZE ) ;
		
		int num = v.getCurrentOcc().size() ;
		for( int i = 0 ; i < num ; i++ ){
			//generate random coordinates within the circle			
			double angle = 2 * i * Math.PI / num ;
			double r = CIRCLE_SIZE / 4 ;
			double xOff = Math.cos( angle ) * r + CIRCLE_SIZE / 2 - 2 ;
			double yOff = Math.sin( angle ) * r + CIRCLE_SIZE / 2 - 2;
			
			gc.strokeOval( v.getX() + xOff , v.getY() + yOff , 4, 4);
		}
	}

	
	public static void makeGnomes( int num ){
		try{
			BufferedReader read = new BufferedReader( new FileReader( "gnomen-namen.txt" ) ) ;
			for ( int i = 0 ; i < num ; i ++ ){
				gnomes.add( new Gnome( read.readLine() ) ) ;
			}
			read.close(); 
			
			gnomes.assignAuth();
		}
		catch( FileNotFoundException e ){
			System.out.println( "File got eaten by pixies.");
		}
		catch( IOException e ){
			System.out.println( "Something went wrong with IO :(" );
		}
	}

	public static void runAll(){
		for( Gnome g : gnomes.getList() ){
			map.plant( g );
			g.start();
		}
	}
}
