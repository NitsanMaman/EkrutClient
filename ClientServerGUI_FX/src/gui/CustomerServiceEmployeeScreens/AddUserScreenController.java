package gui.CustomerServiceEmployeeScreens;

import application.client.ClientUI;
import application.client.MessageHandler;
import application.user.UserController;
import common.Departments;
import common.connectivity.Message;
import common.connectivity.MessageFromClient;
import common.connectivity.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddUserScreenController implements Initializable {

    @FXML
    private ChoiceBox<String> departmentField;

    @FXML
    private TextField emailAddressField;

    @FXML
    private Text errorMessage;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField idField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private TextField userNameField;

    private double xoffset;
    private double yoffset;
    @FXML
    void addUser(MouseEvent event) {
        errorMessage.setFill(Color.RED);
        // check for empty fields
        if (userNameField.getText().equals("") || passwordField.getText().equals("") || firstNameField.getText().equals("")
         || lastNameField.getText().equals("") || idField.getText().equals("") || phoneNumberField.getText().equals("")
         || emailAddressField.getText().equals("") || departmentField.getValue() == null){
            this.errorMessage.setText("All fields MUST be filled.");
            return;
        }

        // check for valid email address
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher matcher = pattern.matcher(emailAddressField.getText());
        if (!matcher.matches()){
            this.errorMessage.setText("invalid email address format");
            return;
        }
        if (emailAddressField.getText().contains(" ")){
            this.errorMessage.setText("email address MUST NOT contain spaces");
            return;
        }

        // check valid id number
        pattern = Pattern.compile("^[0-9]+$");
        matcher = pattern.matcher(idField.getText());
        if (!matcher.matches()){
            this.errorMessage.setText("ID MUST NOT contain letters");
            return;
        }
        if (idField.getText().length() < 9){
            this.errorMessage.setText("ID too short");
            return;
        }
        if (idField.getText().length() > 9){
            this.errorMessage.setText("ID too long");
            return;
        }

        // check phone number
        matcher = pattern.matcher(phoneNumberField.getText());
        if (!matcher.matches()){
            this.errorMessage.setText("phone number MUST only contain numbers");
            return;
        }

        // create user
        User user = new User();
        user.setUsername(userNameField.getText());
        user.setPassword(passwordField.getText());
        user.setFirstname(firstNameField.getText());
        user.setLastname(lastNameField.getText());
        user.setId(idField.getText());
        user.setPhonenumber(phoneNumberField.getText());
        user.setDepartment(departmentField.getValue().replace(" ", "_"));

        ClientUI.chat.accept(new Message(user, MessageFromClient.REQUEST_ADD_USER));

        errorMessage.setText(MessageHandler.getMessage());
        if (MessageHandler.getMessage().contains("successfully"))
            errorMessage.setFill(Color.GREEN);
    }

    @FXML
    protected void exit(MouseEvent event) {
        ArrayList<String> cred = new ArrayList<String>();
        cred.add(UserController.getCurrentuser().getUsername());
        ClientUI.chat.accept("disconnect");
        ClientUI.chat.accept(new Message(cred, MessageFromClient.REQUEST_LOGOUT));
        Platform.exit();
        System.exit(0);
    }

    @FXML
    protected void goBack(MouseEvent event) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("CustomerServiceEmployeeScreen.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        switchScreen(event, root);
    }


    private void switchScreen(MouseEvent event, Parent root){
        Stage primaryStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        root.setOnMousePressed(event1 -> {
            xoffset = event1.getSceneX();
            yoffset = event1.getSceneY();
        });

        // event handler for when the mouse is pressed AND dragged to move the window
        root.setOnMouseDragged(event1 -> {
            primaryStage.setX(event1.getScreenX()-xoffset);
            primaryStage.setY(event1.getScreenY()-yoffset);
        });
        primaryStage.setTitle("Client Editor");

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ArrayList<String> departments = new ArrayList<String>();
        for (Departments dep : Departments.values()){
            if (dep.name().contains("_")){
                departments.add(dep.name().replace("_", " "));
                continue;
            }
            departments.add(dep.name());
        }
        departmentField.getItems().setAll(departments);
    }
}
