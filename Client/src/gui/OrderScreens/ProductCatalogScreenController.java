package gui.OrderScreens;
//

import application.client.ChatClient;
import application.client.ClientUI;
import application.user.UserController;
import common.connectivity.Message;
import common.connectivity.MessageFromClient;
import common.orders.Order;
import common.orders.Product;
import gui.ScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import javax.swing.*;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ProductCatalogScreenController extends ScreenController implements Initializable{
    @FXML
    private Button exitButton;

    @FXML
    private Button backButton;

    @FXML
    private Button emptyMyCartButton;

    @FXML
    private Button checkOutButton;
    
    @FXML
    private TabPane tabPane;
    
    @FXML
    private ListView<Object> myCart = new ListView<Object>();;
    
    @FXML
    private TilePane snacksPane;

    @FXML
    private TilePane drinksPane;
    
    @FXML
    private ScrollPane snacksScroll;
    
    @FXML
    private ScrollPane drinksScroll;
    


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	myCart.setFocusTraversable( false );
        ClientUI.chat.accept(new Message("HA01", MessageFromClient.REQUEST_ALL_MACHINE_PRODUCTS));
        tabPane.getStyleClass().add("tab-pane");
        for (Product product : ChatClient.productList) {
           if(product.getType().equals("SNACK"))
        	   snacksPane.getChildren().add(createProductTile(product));
           else
           		drinksPane.getChildren().add(createProductTile(product));
        }
        snacksScroll.setFitToWidth(true);
        drinksScroll.setFitToWidth(true);
    }
    
    @FXML
    void exit(MouseEvent event) {
		super.closeProgram(event, true);
    }
    
    private Node createProductTile(Product product) {
        HBox hBox = new HBox();
        VBox vBox = new VBox();
        InputStream inputStream = new ByteArrayInputStream(product.getFile());
        Image image = null;
        image = new Image(inputStream);
        ImageView imageview = new ImageView();
        imageview.setFitHeight(100.0);
        imageview.setFitWidth(100.0);
        Button addBtn = new Button("Add to cart");
        ImageView addtocarticon = new ImageView(getClass().getResource("/gui/OrderScreens/agalaadd.png").toExternalForm());
        addtocarticon.setFitHeight(35.0);
        addtocarticon.setFitWidth(35.0);
        addBtn.setGraphic(addtocarticon);
        addBtn.getStyleClass().add("btn");
        Button detBtn = new Button();
        ImageView deticon = new ImageView(getClass().getResource("/gui/OrderScreens/details.png").toExternalForm());
        deticon.setFitHeight(20.0);
        deticon.setFitWidth(20.0);
        detBtn.setGraphic(deticon);
        Tooltip tooltip = new Tooltip(product.getDescription());
        tooltip.setShowDelay(null);
        detBtn.setTooltip(tooltip);
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("name-label");
        nameLabel.setWrapText(true);
        nameLabel.setPrefWidth(150);
        Label idLable = new Label("ID: " + product.getProductId());
        idLable.getStyleClass().add("id-label");
        idLable.setWrapText(true);
        idLable.setPrefWidth(150);
        Spinner<Integer> SpinnerQuantity = new Spinner<>(0,product.getAmount(),0);
        SpinnerQuantity.getStyleClass().add("combo-color");
        Text newPrice = new Text();
        Text priceLabel = new Text("Price: " + product.getPrice());
        priceLabel.getStyleClass().add("price-label");
        SpinnerQuantity.setMaxWidth(75);
        imageview.setImage(image);
        if(product.getDiscount()!= 0) {
            priceLabel.setStrikethrough(true);
            float dis = product.getPrice()*(1-product.getDiscount());
            String present = String.format("%.0f%%OFF - Discount Price: %.1f",product.getDiscount()*100, dis);
            newPrice.setText(present + "\u20AA");
            newPrice.getStyleClass().add("new-price-label");
        }
        vBox.getChildren().addAll(nameLabel, idLable, detBtn, priceLabel, newPrice, SpinnerQuantity, addBtn);
        hBox.getChildren().addAll(imageview, vBox);
        vBox.setSpacing(15);
        vBox.setId(String.valueOf(product.getProductId()));
        imageview.setTranslateY(50);
        hBox.setPadding(new Insets(0, 0, 0, 0));
        vBox.setPadding(new Insets(0, 0, 20, 20));
        addBtn.setOnAction(event -> {
        	addToCart(product,nameLabel, idLable, detBtn, priceLabel, newPrice, SpinnerQuantity);
            System.out.println("");
        });
        
        return hBox;
    }
    
    private void addToCart(Product product, Label nameLabel, Label idLable, Button detBtn, Text priceLabel,
			Text newPrice, Spinner<Integer> spinnerQuantity) {
    	int quantity = spinnerQuantity.getValue();
    	System.out.println(quantity);
    	HBox hboxofcart = new HBox();
    	if (ChatClient.productInCart.containsKey(product)) {
    		if (quantity==0) {
    			ChatClient.productInCart.remove(product);
    			hboxofcart.getChildren().remove(hboxofcart);
    		}
    		else {
    			ChatClient.productInCart.replace(product, quantity);
    		}
    	}
    	else {
    		ChatClient.productInCart.put(product, quantity);
            InputStream inputStream = new ByteArrayInputStream(product.getFile());
            Image image = null;
            image = new Image(inputStream);
            ImageView imageview = new ImageView();
            imageview.setFitHeight(100.0);
            imageview.setFitWidth(100.0);
            imageview.setImage(image);
	    	Label namelb = new Label(nameLabel.getText());
	    	Label idlb = new Label(idLable.getText());
	    	Button detsBtn = new Button(detBtn.getStyle());
	    	Text pricelb = new Text(priceLabel.getText());
	    	Text newPricelb = new Text(newPrice.getText());
	    	Spinner<Integer> spinnerQuantitynew = new Spinner<Integer>(0,product.getAmount(),spinnerQuantity.getValue());
	    	hboxofcart.getChildren().addAll(imageview, namelb, idlb, detsBtn, pricelb, newPricelb, spinnerQuantitynew);
	    	imageview.setTranslateY(50);
	    	myCart.getItems().addAll(hboxofcart);
    	}
	}

	@FXML
    void goBack(MouseEvent event) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/gui/UserScreens/UserMainScreen.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.switchScreen(event, root);        
    }
}




