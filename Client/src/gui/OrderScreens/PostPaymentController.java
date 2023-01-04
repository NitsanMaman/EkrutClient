package gui.OrderScreens;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.client.ChatClient;
import application.client.ClientUI;
import common.connectivity.Message;
import common.connectivity.MessageFromClient;
import common.orders.Order;
import common.orders.Product;
import gui.ScreenController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PostPaymentController extends ScreenController implements Initializable{

    @FXML
    private Button exitButton;

    @FXML
    private Button backButton;
    
    @FXML
    private Text orderNum;
    
    @FXML
    private Text machineNum;
    
    @FXML
    private Text dynamicTxt;
    
    @FXML
    private ListView<Object> getOrder = new ListView<Object>();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		orderNum.setText(ChatClient.currentOrder.getOrderID());
		machineNum.setText(ChatClient.currentOrder.getMachineID());
		if (!ChatClient.currentOrder.getSupplyMethod().equals("machine pickup")) {
			machineNum.setVisible(false);
			dynamicTxt.setVisible(false);
			if (ChatClient.currentOrder.getSupplyMethod().equals("instant pickup")) {
		        Platform.runLater(() -> {
		            // Run this method on the JavaFX application thread
		        	executeOrder(ChatClient.currentOrder);
		            ArrayList<String> msg = new ArrayList<String>();
		       	 	msg.add(ChatClient.currentOrder.getOrderID());
		       	 	msg.add("picked up");
		       	 	ClientUI.chat.accept(new Message(msg, MessageFromClient.REQUEST_UPDATE_ORDER_STATUS));
		        });
			}
		}
		// reset the currentOrder for the next order
		ChatClient.currentOrder = new Order();
	}

	@FXML
    void exit(MouseEvent event) {
		super.closeProgram(event, true);
    }
	
	@FXML
    void goBack(MouseEvent event) {
		ChatClient.cartList = new ArrayList<Product>();
		ChatClient.rememberMyCart = new ListView<Object>();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/gui/UserScreens/CustomerMainScreen.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.switchScreenWithTimerCustomersOnly(event, root);        
    }
	
	public static void executeOrder(Order order) {
	    // Load the FXML file
	    Parent root = null;
	    try {
	        root = FXMLLoader.load(PostPaymentController.class.getResource("/gui/OrderScreens/ExecuteScene.fxml"));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Get the ListView object from the FXML file
	    ListView<Object> listView = (ListView<Object>) root.lookup("#getOrder");

	    // Add items to the ListView
	    listView.setItems(ChatClient.rememberMyCart.getItems());
	    ObservableList<Object> items = listView.getItems();

	    // Show the window
	    Scene scene = new Scene(root);
	    Stage stage = new Stage();
	    stage.setScene(scene);

	    // Create a timeline to remove the items from the ListView
	    Timeline timeline = new Timeline();
	    timeline.setCycleCount(items.size());

	    // Create a key frame to remove an item from the ListView and refresh the display
	    KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), event -> {
	        // Only remove an item if the list is not empty
	        if (!items.isEmpty()) {
	            items.remove(0);
	            listView.refresh();
	        }
	    });

	    // Add the key frame to the timeline
	    timeline.getKeyFrames().add(keyFrame);

	    // Close the window after the timeline has finished
	    timeline.setOnFinished(event -> {
	        // Add a 2 second delay before closing the stage
	        timeline.pause();
	        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(1), event2 -> stage.close());
	        timeline.getKeyFrames().add(keyFrame2);
	        timeline.play();
	    });

	    // Start the timeline when the stage is shown
	    stage.setOnShown(event -> timeline.play());

	    stage.show();


	}


}
