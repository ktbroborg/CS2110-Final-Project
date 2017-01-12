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

public class GnomeWindow {
	private GnomeList gnomes ;
	private Map map ;
	private Gnome gTemp ;
	private final ObservableList<String> namen = FXCollections.observableArrayList() ;
	private static ListView<String> nameList ;

	Stage stage = new Stage() ;
	
	public GnomeWindow( GnomeList gnomes, Map map ){
		this.gnomes = gnomes ;
		this.map = map ;
		
		Timeline gUpdate = new Timeline( new KeyFrame(
				Duration.millis( 500 ),
				ae -> {
					namen.clear();
					for( Gnome g : gnomes.getList() ){
						if( g.broke() ){
							namen.add( g.heisst() + "*" ) ;
						}
						else
							namen.add( g.heisst() ) ;
					}
				}
				)) ;
		gUpdate.setCycleCount( Animation.INDEFINITE );
		gUpdate.play();
		
		stage.setTitle( "Gnome Window" );
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
		
		//adding a gnome pane
		AddPane addGPane = new AddPane( "add a gnome" ) ;
		
		//finding a gnome pane
		FindPane findGPane = new FindPane( "find gnome" ) ;
		
		//modifying a gnome pane
		ModifyPane modGPane = new ModifyPane( "modify a gnome" ) ;
		
		//seeing the history of a particular gnome pane
		VisitedPane visitGPane = new VisitedPane( "view history" ) ;
		
		accordion.getPanes().add( addGPane ) ;
		accordion.getPanes().add( findGPane ) ;
		accordion.getPanes().add( modGPane ) ;
		accordion.getPanes().add( visitGPane ) ;
		accordion.setExpandedPane( addGPane );
		
		VBox right = new VBox() ;
		right.setSpacing( 10 );
		
		Label info = new Label() ;
		info.setText( "If a gnome doesn't have enough cash to go anywhere, there is a * by their name");
		info.setPrefWidth( 150 );
		info.setWrapText( true );
		right.getChildren().add( info ) ;
		
		//list of gnomes on the side
		nameList = new ListView<String>( namen );
		nameList.setEditable( true );
		nameList.setPrefWidth( 150 );
		right.getChildren().add( nameList ) ;
		
		box.getChildren().add( accordion ) ;
		box.getChildren().add( right ) ;
		Scene scene = new Scene( box ) ;
		stage.setScene( scene );
		stage.show();
	}

	class AddPane extends TitledPane{
		private GridPane pane = new GridPane() ;
		
		public AddPane( String name ){
			super.setText( name );
			pane.setVgap( 10 );
			pane.setPadding( new Insets(5,5,5,5));
			
			final TextField getName = new TextField() ;
			getName.setPromptText( "Enter a name: ");
			pane.add( getName, 0, 0);
			
			Button submit = new Button( "submit" ) ;
			pane.add( submit, 1, 0);
			
			Label status = new Label() ;
			pane.add( status , 0, 1);
			
			Label cash = new Label() ;
			pane.add( cash, 0, 2);
			
			final TextField cashAmt = new TextField() ;
			cashAmt.setDisable( true );
			pane.add( cashAmt, 0, 3);
			
			Button submit2 = new Button( "submit" ) ;
			submit2.setDisable( true );
			pane.add( submit2, 1, 3);
			
			Label status2 = new Label();
			pane.add( status2, 0, 4);
			
			submit.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){
					if( ! getName.getText().equals( "" ) ){
						String newName =  getName.getText() ;
						gTemp = new Gnome( newName ) ;
						gnomes.add(gTemp); 
						namen.add( gTemp.heisst() );
						
						map.plant( gTemp );
						gTemp.start();
	
						status.setText( gTemp.heisst() + " has been added");
						status.setStyle( "-fx-text-fill: #000000;");
						cashAmt.setDisable( false );
						cashAmt.setPromptText( "Enter an amt if you wish to add cash:" );
						submit2.setDisable( false );
						status.setText( gTemp.heisst() + " currently has " + gTemp.cash() );
					}
				}
			});

			submit2.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){
					try{ 
						if( cashAmt.getText() != "" ){
							int amt = Integer.parseInt( cashAmt.getText() ) ;
							gTemp.addCash( amt );
							status2.setText( gTemp.heisst() + " currently has " + gTemp.cash() );
						}
					} catch( NumberFormatException e ){
						//
					}
				}
			});
			
			super.setContent( pane );
		}
	}
	
	class ModifyPane extends TitledPane{
		private GridPane pane = new GridPane() ;
		
		private int urg ;
		
		public ModifyPane ( String name ){
			super.setText( name );
			pane.setVgap( 10 ); pane.setHgap( 10 );
			pane.setPadding( new Insets(5,5,5,5));
			
			TextField gnome = new TextField() ;
			gnome.setPromptText( "Enter the name of a gnome to edit: ");
			gnome.setPrefWidth( 220 );
			pane.add( gnome,  0, 0);
			
			Button subGnome = new Button( "enter" ) ;
			pane.add( subGnome, 1, 0);
			
			TextField newname = new TextField() ;
			pane.add( newname, 0, 1);
			
			Button subName = new Button( "rename" ) ;
			subName.setDisable( true );
			pane.add( subName, 1, 1);
			
			TextField cash = new TextField() ; 
			pane.add( cash, 0, 2);
			
			Button subCash = new Button( "add" ) ;
			subCash.setDisable( true );
			pane.add( subCash, 1, 2);	
			
			VBox urgency = new VBox() ;
			urgency.setSpacing( 10 );
			final ToggleGroup group = new ToggleGroup() ;
			
			RadioButton rb1 = new RadioButton();
			rb1.setText( "zzzzzz" ); rb1.setUserData( 0 );
			rb1.setToggleGroup( group );
			
			RadioButton rb2 = new RadioButton() ;
			rb2.setText( "mehhh" ); rb2.setUserData( 1 ); 
			rb2.setToggleGroup( group );
			
			RadioButton rb3 = new RadioButton() ;
			rb3.setText( "AHHHHH" ); rb3.setUserData( 2 );
			rb3.setToggleGroup( group );
			
			RadioButton rb4 = new RadioButton() ;
			rb4.setText( "don't change" ); //don't do anything to urgency
			rb4.setToggleGroup( group ); rb4.setUserData( -3 ); //will check for negative after submit
			
			urgency.getChildren().addAll( rb1, rb2, rb3, rb4 ) ;
			pane.add( urgency,  0, 3 );
			
			VBox urgency2 = new VBox() ;
			urgency2.setSpacing( 10 );
			Label urgencyL = new Label( "urgency" ) ;
			
			Button subUrg = new Button( "update" ) ;
			subUrg.setDisable( true );
			urgency2.getChildren().addAll( urgencyL, subUrg ) ;
			pane.add( urgency2, 1, 3);
			
			TextField destination = new TextField() ;
			pane.add( destination, 0, 4);
			
			Button subDest = new Button( "reroute" ) ;
			subDest.setDisable( true );
			pane.add( subDest, 1, 4);
			
			Label currentLoc = new Label() ;
			currentLoc.setWrapText( true );
			currentLoc.setPrefWidth( 80 );
			pane.add( currentLoc , 1, 5);
			
			Label status = new Label() ;
			status.setWrapText( true );
			status.setPrefWidth( 220 );
			pane.add( status , 0, 5);
			
			
			Timeline updateLoc = new Timeline( new KeyFrame(
				Duration.millis( 500 ),
				ae-> {
					currentLoc.setText( "current village: " + 
							gTemp.getRecentVillage( "00" ).getName() );
				}
			)) ;
			updateLoc.setCycleCount( Animation.INDEFINITE);
			
			subGnome.setOnAction( new EventHandler<ActionEvent> () {
				@Override
				public void handle( ActionEvent event ){
					if( ! gnome.getText().equals( "" ) ){
						try {
							gTemp = gnomes.getGnome( gnome.getText() ) ;
							status.setText( gTemp.heisst() + " selected");
							
							newname.setPromptText( "Enter a new name: ");
							subName.setDisable( false );
							cash.setPromptText( "Enter an number to add cash: " );
							subCash.setDisable( false );
							destination.setPromptText( "Enter a new destination: " );
							subDest.setDisable( false );
							
							updateLoc.play();

						} catch (NotFoundException e) {
							status.setText( "Gnome not found" );
							status.setStyle( "-fx-text-fill: #000000;");

						}
					}
					else {
						status.setText( "Please enter a valid gname" );
						status.setStyle( "-fx-text-fill: #ff0000;");
					}
				}
			});
			
			subName.setOnAction( new EventHandler<ActionEvent> () {
				@Override
				public void handle( ActionEvent event ){
					if( newname.getText() != "" ){
						String temp = gTemp.heisst() ;

						gTemp.reName( newname.getText() );
						status.setText( temp + " is now " + gTemp.heisst() );
						status.setStyle( "-fx-text-fill: #000000;");

					}
					else {
						subName.setText( "Please enter a name" );
						subName.setStyle( "-fx-text-fill: #ff0000;");
					}
				}
			});

			subCash.setOnAction( new EventHandler<ActionEvent> (){
				@Override
				public void handle( ActionEvent event ){
					if ( cash.getText() != null ){
						try{ 
							int temp = Integer.parseInt( cash.getText() ) ;
							
							gTemp.addCash( temp );
							status.setText( temp + " added to " + gTemp.heisst() +
									" for a total of " + gTemp.cash() );
							status.setStyle( "-fx-text-fill: #000000;");

						} catch( NumberFormatException e ){
							status.setText( "Enter valid number");
							status.setStyle( "-fx-text-fill: #ff0000;");
						}
					}
				}
				
			});
			
			group.selectedToggleProperty().addListener( new ChangeListener<Toggle>(){
				@Override
				public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue,
						Toggle newValue) {
					if( group.getSelectedToggle() != null ){
						urg = (int) group.getSelectedToggle().getUserData() ;
						subUrg.setDisable( false );
					}
				}
			}
			);
			
			subUrg.setOnAction( new EventHandler<ActionEvent> (){
				@Override
				public void handle( ActionEvent event ){
					if( urg >= 0 ){
						gTemp.setUrgency( urg );
						status.setText( gTemp.heisst() + " now has urgency of " + gTemp.getUrgency()  );
						status.setStyle( "-fx-text-fill: #000000;");

					}
				}
			});
			
			subDest.setOnAction( new EventHandler<ActionEvent> (){
				@Override
				public void handle( ActionEvent event ){
					if( ! destination.getText().equals( "" ) ){
						try{
							Village tempV = map.getV( destination.getText() ) ;
							
							gTemp.setDestination( tempV );
							map.reRoute( gTemp, gTemp.getRecentVillage( "00" ), tempV );
							
							ArrayList<Road> route = gTemp.getRoute() ;
							String routeS = "New route: " ;

							if( ! route.isEmpty() ){
								for( Road rd : route ){
									routeS += rd.getStart().getName() + ", " ;
								}
								routeS += route.get( route.size()-1 ).getEnd().getName() ;
								status.setText( routeS );
								status.setStyle( "-fx-text-fill: #000000;");
							}
						}
						catch( NotFoundException e ){
							status.setText( "Village not found" );
							status.setStyle( "-fx-text-fill: #ff0000;");
						}
					}
				}
			});
			
			super.setContent( pane );
			
		}
	}
	
	class FindPane extends TitledPane{
		private GridPane pane = new GridPane() ;
		
		public FindPane( String name ){
			super.setText( name );
			pane.setVgap( 10 ); pane.setHgap( 10 );
			pane.setPadding( new Insets(5,5,5,5));
			
			TextField toFind = new TextField() ;
			toFind.setPromptText( "Gnome to find: ");
			pane.add( toFind, 0, 0);
			
			TextField auth1 = new TextField() ;
			auth1.setPromptText( "Gnome to authorize: ");
			pane.add( auth1, 0, 1);
			
			TextField auth2 = new TextField() ;
			auth2.setPromptText( "Gnome to authorize: ");
			pane.add(auth2, 0, 2);
			
			TextField auth3 = new TextField() ;
			auth3.setPromptText( "Gnome to authorize: ");
			pane.add(auth3, 0, 3);
			
			Button find = new Button( "find" ) ;
			pane.add( find, 1, 3);
			
			Label status = new Label() ;
			pane.add( status, 1, 4);
			
			find.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){
					if( ! toFind.getText().equals( null ) &&
							! auth1.getText().equals( null ) &&
							! auth2.getText().equals( null ) &&
							! auth3.getText().equals( null ) ) {
						try {
							Location temp = gnomes.find( gnomes.getGnome( toFind.getText()),
									gnomes.getGnome( auth1.getText()),
									gnomes.getGnome( auth2.getText()),
									gnomes.getGnome( auth3.getText() ) ) ;
							status.setText( toFind.getText() + " is at " + temp.getName());
						} 
						catch( NotFoundException e ){
							status.setText( "enter valid names >:(");
						}
						catch (AuthorizationFailureException e) {
							status.setText( "authorization failed");
						}
					}
					else{
						status.setText( "please enter valid gnome names");
					}
				}
			});
			
			super.setContent( pane );
			
		}
	}
	
	class VisitedPane extends TitledPane{
		private GridPane pane = new GridPane() ;
		private final ObservableList<String> history = FXCollections.observableArrayList() ;
		
		public VisitedPane( String name ){
			super.setText( name ) ;
			pane.setVgap( 10 );
			pane.setPadding( new Insets(5,5,5,5));
			
			final TextField gName = new TextField();
			gName.setPromptText( "Enter the name of a gnome: ");
			gName.setPrefWidth( 250 );
			pane.add( gName , 0, 0);
			
			Button nameSub = new Button( "submit" ) ;
			pane.add( nameSub , 1 , 0);
			
			Label label = new Label( "village log");
			pane.add( label , 0, 2);
	        
    		final ListView<String> view = new ListView<String>( history ) ;
			view.setEditable( true );
	        pane.add( view, 0, 3);
			
	        nameSub.setOnAction( new EventHandler<ActionEvent>() {
				@Override
				public void handle( ActionEvent event ){
					history.clear(); 
					if ( ! gName.getText().equals( "" ) ){
						//go grab the gnome's data
						String pass = "00" ;
						try{
							gTemp = gnomes.getGnome( gName.getText() ) ;
							ArrayList<String> temp = gTemp.getVisited( pass ) ;
							if ( temp != null ){
								for( String v : temp ){
									history.add( v ) ;
								}
							}
							else
								label.setText( gTemp.heisst() + " hasn't been anywhere yet") ;
						} catch( NotFoundException e ){
							label.setText( "Gnome could not be found.");
						}
					}
					else
						label.setText( "empty submit" );
				}
			});
	        
	        super.setContent( pane );
		}
	}
}
