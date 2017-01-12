package gnomenwald;

import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class VillageWindow {
	private Map map ;
	private final ObservableList<String> vNames = FXCollections.observableArrayList() ;
	private static ListView<String> nameList ;
	
	private Village vTemp ;
	
	Stage stage = new Stage() ;
	
	
	public VillageWindow( Map map ){
		this.map = map ;
		
		Timeline vUpdate = new Timeline( new KeyFrame(
				Duration.millis( 500 ),
				ae -> {
					vNames.clear();
					for( Village v : map.getVillages() ){
						vNames.add( v.getName() ) ;
					}
				}
				)) ;
		vUpdate.setCycleCount( Animation.INDEFINITE );
		vUpdate.play();
		
		
		stage.setTitle( "Village Window" );
		HBox box = new HBox() ;
		box.setPadding( new Insets( 20,20,20,20) );
		box.setSpacing( 20 );
		box.setStyle("-fx-background-color: #cce6ff;" );
		
		stage.setOnCloseRequest( new EventHandler<WindowEvent>() {
			@Override
			public void handle( WindowEvent w ){
				stage.close();
				}
		});
		
		final Accordion accordion = new Accordion() ;
		
		//adding a village pane
		EditPane addVPane = new EditPane( "edit villages" ) ;
		
		//removing a village pane
		RemovePane removeVPane = new RemovePane( "remove village" ) ;
		
		//viewing all the gnomes at a certain village
		FocusPane focusVPane = new FocusPane( "focus on village" ) ;
		
		accordion.getPanes().add( addVPane ) ;
		accordion.getPanes().add( removeVPane ) ;
		accordion.getPanes().add( focusVPane ) ;
		accordion.setExpandedPane( addVPane );
		
		//list of villages on the side
		nameList = new ListView<String>( vNames );
		nameList.setEditable( true );
		nameList.setPrefWidth( 80 );

		box.getChildren().add( accordion ) ;
		box.getChildren().add( nameList ) ;
		Scene scene = new Scene( box ) ;
		stage.setScene( scene );
		stage.show();
	}

	class EditPane extends TitledPane{
		private GridPane pane = new GridPane() ;
		
		public EditPane( String name ){
			super.setText( name );
			pane.setVgap( 10 );
			pane.setHgap( 10 );
			pane.setPadding( new Insets(5,5,5,5));
			
			final TextField oLimit = new TextField() ;
			oLimit.setPromptText( "Enter occupancy limit: ");
			pane.add( oLimit, 0, 0);
			
			Button add = new Button( "add village" ) ;
			pane.add( add, 1, 0);
			
			Label span = new Label( ) ;
			span.setText( "Demonstrate minimum spanning tree. Note: this ignores directedness and "
					+ "simply uses Prim's algorithm as if the roads were undirected. Also, it bases "
					+ "calculations on the road tolls.");
			span.setWrapText( true );
			span.setPrefWidth( 220 );
			pane.add( span, 0, 1);
			
			Button prims = new Button( "Go" ) ;
			pane.add( prims, 1, 1);
			
			Label sortLabel = new Label() ;
			sortLabel.setText( "Demonstrates topological sort. Sorting is just based on the direction "
					+ "of the roads." );
			sortLabel.setWrapText( true );
			sortLabel.setPrefWidth( 220 );
			pane.add( sortLabel,  0, 2);
			
			Button topSort = new Button( "Sort" ) ;
			pane.add( topSort, 1, 2);
			
			Label status = new Label() ;
			status.setWrapText( true );
			status.setPrefWidth( 220 );
			pane.add( status , 0, 3);
			
			add.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){
					try{
						int lim = Integer.parseInt( oLimit.getText() ) ;
						vTemp = new Village( lim );
						map.add( vTemp );
						status.setText( vTemp.getName() + " has been added");
						status.setStyle( "-fx-text-fill: #000000;");

					}
					catch( NumberFormatException e ){
						status.setText( "please enter an integer");
						status.setStyle( "-fx-text-fill: #ff0000;");
					}
				}
			});
			
			prims.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){
					SpanningTree spt = new SpanningTree( map );
					spt.prims();
					
					status.setText( "Many gnomes are probably stuck now……" );
				}
			});
			
			topSort.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){
					TopSort sort = new TopSort( map );
					try{
						ArrayList<Village> temp = sort.sort() ;
						String s = "Sorted: " ;
						
						for( Village v : temp ){
							s += v.getName() + " " ;
						}
						status.setText( s );
					} 
					catch( CycleException e ){
						status.setText( "Oh no, there was a cycle somewhere." );
					}
				}
			});
			
			super.setContent( pane );
		}
	}
	
	class RemovePane extends TitledPane{
		private GridPane pane = new GridPane() ;
		private int remType ;
		
		public RemovePane( String name ){
			super.setText( name );
			pane.setVgap( 10 );
			pane.setHgap( 10 );
			pane.setPadding( new Insets(5,5,5,5));
			
			final ToggleGroup group = new ToggleGroup() ;
			
			final TextField selectV = new TextField() ;
			selectV.setPromptText( "Enter a village name: " );
			pane.add( selectV , 0, 0);
			
			Button submit = new Button( "submit" ) ;
			pane.add( submit, 1, 0);
			
			RadioButton rb1 = new RadioButton();
			rb1.setText( "remove 1" );
			rb1.setToggleGroup( group );
			rb1.setUserData( 1 );
			pane.add( rb1, 0, 1);
			
			Label rem1exp = new Label() ;
			rem1exp.setText( "Removing by method 1 removes the village from the map"
					+ " and also removes any villages that went to or came from that village.");
			rem1exp.setPrefWidth( 200 );
			rem1exp.setWrapText( true );
			pane.add( rem1exp, 1, 1);
			
			RadioButton rb2 = new RadioButton();
			rb2.setText( "remove 2" );
			rb2.setToggleGroup( group );
			rb2.setUserData( 2 );
			pane.add( rb2, 0, 2 ) ;
			
			Label rem2exp = new Label() ;
			rem2exp.setText( "Removing by method 2 removes the village from the map but "
					+ "connects roads instead of deletes them." );
			rem2exp.setPrefWidth( 200 ) ;
			rem2exp.setWrapText( true );
			pane.add( rem2exp, 1, 2);
			
			Label remChoice = new Label() ;
			pane.add( remChoice, 0, 3);
			
			Label remVill = new Label() ;
			pane.add( remVill, 0, 4);
			
			Button go = new Button( "remove" ) ;
			go.setDisable( true );
			pane.add( go,  1,  4);
						
			super.setContent(pane );
			
			group.selectedToggleProperty().addListener( new ChangeListener<Toggle>(){
				@Override
				public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue,
						Toggle newValue) {
					if( group.getSelectedToggle() != null ){
						remType = Integer.parseInt( group.getSelectedToggle()
								.getUserData().toString() ); //should be 1 or 2 depending on toggle
						remChoice.setText( "using remove type " + remType );
						go.setDisable( false );
					}
				}
			}
			);
			
			submit.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event){
					selectV.setStyle("-fx-text-fill: #000000;");
					remVill.setStyle( "-fx-text-fill: #000000;");
				if( !selectV.getText().equals( "" ) ){
					Boolean good = false ;
					for( Village v : map.getVillages() ){
						if ( v.getName().equals(selectV.getText() )) {
							vTemp = v ;
							good = true ;
						}
					}
					if( !good ){
						selectV.setPromptText( "List of villages on right");
						remVill.setText( "please enter a valid village" );
						remVill.setStyle( "-fx-text-fill: #ff0000;");
						go.setDisable( true );
					}
					else if ( good ){
						remVill.setText( "village to remove: " + vTemp.getName() );
						go.setDisable( false );
					}
				}
				}
			});
			
			go.setOnAction( new EventHandler<ActionEvent> (){
				@Override
				public void handle( ActionEvent event ){
					go.setDisable( true );
					if( vTemp != null && group.getSelectedToggle() != null ){
						if( remType == 1 ){
							map.remove1( vTemp ) ;
						}
						else if ( remType == 2 ) {
							map.remove2( vTemp ) ;
						}
						
						vNames.clear(); 
						for( Village v : map.getVillages() ){
							if( ! v.equals( vTemp) ) vNames.add( v.getName() ) ;
						}
						
						remVill.setText( "village " + vTemp.getName() + " removed" );
						
						vTemp = null ;
					}
				}
			});
			
		}
	}
	
	class FocusPane extends TitledPane{
		private GridPane pane = new GridPane () ;
		private final ObservableList<Label> gNames = FXCollections.observableArrayList() ;
		
		public FocusPane( String name ) {
			super.setText( name );
			pane.setVgap( 10 );
			pane.setHgap( 10 );
			pane.setPadding( new Insets(5,5,5,5));
			
			TextField vName = new TextField() ;
			vName.setPromptText( "Enter village name: ");
			pane.add( vName , 0, 0);
			
			Button submit = new Button( "submit" ) ;
			submit.setPrefWidth( 100 );
			pane.add( submit, 1, 0);
			
			Label status = new Label("") ;
			pane.add( status, 0, 1);
			
			Button go = new Button( "go" ) ;
			go.setDisable( true );
			pane.add( go,  1, 1 );
			
			final ListView<Label> view = new ListView<Label>( gNames ) ;  
			view.setEditable( true );
			pane.add( view , 0, 2 );
			
			VBox key = new VBox() ;
			
			VBox info = new VBox() ;
			info.setSpacing( 10 );
			info.setPrefWidth( 100 );
			Label delay = new Label() ;
			Label limit = new Label() ;
			Label connections = new Label() ;
			connections.setWrapText( true );
			info.getChildren().addAll( delay, limit, connections );
			
			Label l1 = new Label( "left" ) ;
			l1.setStyle( "-fx-text-fill: #a6a6a6;" );
			Label l2 = new Label( "in village" ) ;
			l2.setStyle( "-fx-text-fill: #000000;" );
			Label l3 = new Label( "new" ) ;
			l3.setStyle( "-fx-text-fill: #aa80ff;" );
			key.getChildren().addAll( l1, l2, l3, info ) ;
			pane.add( key , 1, 2);
			
			super.setContent( pane );
			
			long start = System.currentTimeMillis() ;
			Timeline tl = new Timeline( new KeyFrame( 
					Duration.millis( 500 ),
					ae -> {
						gNames.clear();
						if( vTemp != null ){
							for( int i = 0 ; i < vTemp.getLog().size() ; i ++ ){
								GnomeStat gs = vTemp.getLog().get( i ) ; 
								Label l = new Label() ;
								l.setText( gs.getGnome().heisst() ) ;
								
								if( gs.getStatus().equals( "gone" ) ){
									l.setStyle( "-fx-text-fill: #a6a6a6;" );
								}
								else if ( gs.getStatus().equals( "in" ) ){
									l.setStyle( "-fx-text-fill: #000000;" );
								}
								else{
									l.setStyle( "-fx-text-fill: #aa80ff;" );
									gs.setStatus( "in" );
								}
								
								gNames.add( l ) ;
								}
							
							status.setText( "time taken: " + (System.currentTimeMillis() - start ) );
						}
					}
					)) ;
			
			submit.setOnAction(  new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){					
					gNames.clear();
					tl.stop();
					
					if( !vName.getText().equals( "")){
						try{
							vTemp = map.getV( vName.getText() );
							if ( ! vTemp.equals( "" )){
								for( GnomeStat gs : vTemp.getLog() ){
									Label l = new Label( gs.getGnome().heisst() ) ;
									l.setStyle( "-fx-text-fill: #ffaa80;" );
									gNames.add( l ) ;
								}
								go.setDisable( false );
								limit.setText( "Occ Limit:\n" + vTemp.getLimit() );
								delay.setText( "\nDelay:\n" + vTemp.getDelay() );
								
								String nexts = "Nexts: " ;
								for( Road rd : vTemp.getNexts() ){
									nexts += rd.getEnd().getName() + " " ;
								}
								connections.setText( nexts );
							}
							else{
								go.setDisable( true );
								status.setText( "village does not exist" );
							}
						} catch( Exception e ){
							status.setText( "something went wrong" );
						}
					}
					else{
						status.setText( "empty submit" );
					}
				}
			}) ;
			
			go.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){
					go.setDisable( true );
					view.setItems( gNames );
					status.setText( "time taken: " + (System.currentTimeMillis() - start ) );

	    			tl.setCycleCount( Animation.INDEFINITE );
	    			tl.play(); 	
	    			}
			});
		}
	}
}
