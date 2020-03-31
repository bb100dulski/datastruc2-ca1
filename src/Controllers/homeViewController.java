package Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class homeViewController implements Initializable {
    public static homeViewController hVC;
    public AnchorPane homePane;
    public MenuBar menuBar;
    public Menu fileMenuBar;
    public MenuItem openImgMenu;
    public Menu goToMenuBar;
    public MenuItem goToHomeMenu;
    public MenuItem goToImgViewMenu;
    public MenuItem goToAboutMenu;
    public Menu helpMenuBar;
    public MenuItem quitMenu;
    public Button openImgBtn;
    public Button closeBtn;

    public static String fileName;
    public static URL url;
    public static Image image;
    public static File selectedFile;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hVC = this;
    }

    public void openImage(ActionEvent actionEvent) throws IOException {
        openImg(null);
    }

    public void goHome(ActionEvent actionEvent) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("../FXML/homeView.fxml"));
        homePane.getChildren().setAll(pane);
    }

    public void goImg(ActionEvent actionEvent) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("../FXML/mainView.fxml"));
        homePane.getChildren().setAll(pane);
    }

    public void goAbout(ActionEvent actionEvent) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("../FXML/aboutView.fxml"));
        homePane.getChildren().setAll(pane);
    }

    public void quit(ActionEvent actionEvent) {
        close(null);
    }

    public void openImg(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.jpeg"));
        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            fileName = selectedFile.getAbsolutePath();
            url = selectedFile.toURI().toURL();
        }
        image = new Image(url.toExternalForm());
        Pane pane = FXMLLoader.load(getClass().getResource("../FXML/mainView.fxml"));
        homePane.getChildren().setAll(pane);
        mainViewController.mVC.ogImgView.setImage(image);
    }

    public void close(ActionEvent actionEvent) {
        Platform.exit();
    }
}
