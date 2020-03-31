package Controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.openjdk.jmh.annotations.Benchmark;
import sample.cellsImageProcessing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class mainViewController implements Initializable {

    public static int redCellNum;
    public static int whiteCellNum;

    public static mainViewController mVC;
    public AnchorPane mainViewPane;
    public Tab ogImgTab;
    public AnchorPane ogImgPane;
    public ImageView ogImgView;
    public Tab tricolorImgTab;
    public AnchorPane tricolorImgPane;
    public ImageView tricolorImgView;
    public Tab cellsImgTab;
    public AnchorPane cellsImgPane;
    public ImageView cellsImgView;
    public MenuBar menuBar;
    public Menu fileMenuBar;
    public MenuItem openImgMenu;
    public Menu goToMenuBar;
    public MenuItem goToHomeMenu;
    public MenuItem goToImgViewMenu;
    public MenuItem goToAboutMenu;
    public Menu helpMenuBar;
    public MenuItem quitMenu;
    public Slider brightnessSlider;
    public Slider saturationSlider;
    public Slider hueSlider;
    public TextField redTxt;
    public TextField blueTxt;
    public TextField greenTxt;
    public Button noiseBtn;
    public Button detectCellsBtn;
    public Button tricolorBtn;
    public Button overlaysBtn;
    public Button ogImgBtn;
    public TextArea imgInfoTxt;
    public Text brightnessTxt;
    public Text hueTxt;
    public Text contrastTxt;
    public Slider contrastSlider;
    public Text saturationTxt;
    public Button closeBtn;
    public Text nameTxt;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mVC = this;
        brightnessSlider.setMin(0);
        brightnessSlider.setMax(1);
        brightnessSlider.setBlockIncrement(0.1);
        hueSlider.setMin(0);
        hueSlider.setMax(1);
        hueSlider.setBlockIncrement(0.1);
        saturationSlider.setMin(0);
        saturationSlider.setMax(1);
        saturationSlider.setBlockIncrement(0.1);
        contrastSlider.setMin(0);
        contrastSlider.setMax(1);
        contrastSlider.setBlockIncrement(0.1);
        imgInfoTxt.setText("Image details: " + "\n" +
                "Image name: " + homeViewController.selectedFile.getName() + "\n" +
                "Image size: " + homeViewController.selectedFile.length() + "\n" +
                "Image path: " + homeViewController.selectedFile.getAbsolutePath());
    }

    public void openImage(ActionEvent actionEvent) throws IOException {
        homeViewController.hVC.openImg(null);
    }

    public void goHome(ActionEvent actionEvent) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("../FXML/homeView.fxml"));
        mainViewPane.getChildren().setAll(pane);
    }

    public void goImg(ActionEvent actionEvent) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("../FXML/mainView.fxml"));
        mainViewPane.getChildren().setAll(pane);
    }

    public void goAbout(ActionEvent actionEvent) throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("../FXML/aboutView.fxml"));
        mainViewPane.getChildren().setAll(pane);
    }

    public void quit(ActionEvent actionEvent) {
        close(null);
    }

    public ColorAdjust colorAdjust = new ColorAdjust();

    public void setBrightness(MouseEvent mouseEvent) {
        colorAdjust.setBrightness(brightnessSlider.getValue());
        ogImgView.setEffect(colorAdjust);
    }

    public void setSaturation(MouseEvent mouseEvent) {
        colorAdjust.setSaturation(saturationSlider.getValue());
        ogImgView.setEffect(colorAdjust);
    }

    public void setHue(MouseEvent mouseEvent) {
        colorAdjust.setHue(hueSlider.getValue());
        ogImgView.setEffect(colorAdjust);
    }

    public void setContrast(MouseEvent mouseEvent) {
        colorAdjust.setContrast(hueSlider.getValue());
        ogImgView.setEffect(colorAdjust);
    }

    static Image denoisedFinal;

    //https://stackoverflow.com/questions/52049507/image-processing-removing-noise-of-processed-image-opencv-java
    //https://stackoverflow.com/questions/41675245/opencv-remove-noise-in-image-using-java
    //https://stackoverflow.com/questions/36716238/how-to-denoise-an-image-using-java-opencv
    //https://opencv-python-tutroals.readthedocs.io/en/latest/py_tutorials/py_photo/py_non_local_means/py_non_local_means.html
    public void noiseReduction(ActionEvent actionEvent) throws IOException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread(homeViewController.fileName, Imgproc.COLOR_GRAY2BGR);
        Mat denoisedImage;
        denoisedImage = image;
        Photo.fastNlMeansDenoisingColored(image, denoisedImage, 10, 10, 7, 21);
        File openCVFile = new File("denoised.jpg");
        Imgcodecs.imwrite(String.valueOf(openCVFile), denoisedImage);
        URL url = openCVFile.toURI().toURL();
        denoisedFinal = new Image(url.toExternalForm(), ogImgView.getFitWidth(), ogImgView.getFitHeight(), false, true);
        ogImgView.setImage(denoisedFinal);
    }

    public static WritableImage writableTricolorImage;

    public void tricolorImage(ActionEvent actionEvent) throws IOException {
        noiseReduction(null);
        PixelReader pixelTricolorReader = denoisedFinal.getPixelReader();
        writableTricolorImage = new WritableImage((int) denoisedFinal.getWidth(), (int) denoisedFinal.getHeight());
        PixelWriter pixelTricolorWriter = writableTricolorImage.getPixelWriter();

        for (int x = 0; x < writableTricolorImage.getWidth(); x++) {
            for (int y = 0; y < writableTricolorImage.getHeight(); y++) {
                javafx.scene.paint.Color col = pixelTricolorReader.getColor(x, y);
                if (col.getBlue() > Double.parseDouble(blueTxt.getText()) && col.getRed() < Double.parseDouble(redTxt.getText())) {
                    col = Color.PURPLE;
                } else if (col.getGreen() < Double.parseDouble(greenTxt.getText())) {
                    col = Color.RED;
                } else col = Color.WHITE;
                pixelTricolorWriter.setColor(x, y, col);
            }
            tricolorImgView.setImage(writableTricolorImage);
        }
    }

    //https://docs.oracle.com/javase/7/docs/api/java/awt/Rectangle.html
    //http://www.java2s.com/Code/Java/JavaFX/SetRectangleStroketoColorBLACK.htm
    //https://stackoverflow.com/questions/35306938/javafx-create-rectangles-in-a-loop
    public void drawRedBloodCellRectangle(Rectangle rectangle, int cellID) {
        Text cellTag = new Text(String.valueOf(cellID));
        cellTag.setLayoutX(rectangle.getX());
        cellTag.setLayoutY(rectangle.getY());

        rectangle = new Rectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.GREEN);

        ((Pane) cellsImgView.getParent()).getChildren().addAll(rectangle, cellTag);
    }

    public void drawWhiteBloodCellRectangle(Rectangle rectangle, int cellID) {
        Text cellTag = new Text(String.valueOf(cellID));
        cellTag.setLayoutX(rectangle.getX());
        cellTag.setLayoutY(rectangle.getY());

        rectangle = new Rectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.PURPLE);

        ((Pane) cellsImgView.getParent()).getChildren().addAll(rectangle, cellTag);
    }

    public void toggleOverlays(ActionEvent actionEvent) {
            cellsImgPane.getChildren().removeIf(node -> node instanceof Rectangle);
    }

    public void detectCells(ActionEvent actionEvent) throws IOException {
        noiseReduction(null);
        tricolorImage(null);
        cellsImgView.setImage(writableTricolorImage);
        cellsImageProcessing cells = new cellsImageProcessing();
        cells.findRedCells();
        cells.findWhiteCells();
        redCellNum = 1;
        whiteCellNum = 1;
        for (Rectangle redRect : cells.getRedCellBoxes()) {
            drawRedBloodCellRectangle(redRect, redCellNum++);
        }
        for (Rectangle whiteRect : cells.getWhiteCellBoxes()) {
            drawWhiteBloodCellRectangle(whiteRect, whiteCellNum++);
        }
        imgInfoTxt.setText("Image details: " + "\n" +
                "Image name: " + homeViewController.selectedFile.getName() + "\n" +
                "Image size: " + homeViewController.selectedFile.length() + "\n" +
                "Image path: " + homeViewController.selectedFile.getAbsolutePath() + "\n" +
                "There are roughly " + cells.getRedCells().length + " red blood cells." + "\n" +
                "There are roughly " + cells.getWhiteCells().length + " white blood cells." + "\n" +
                "There are roughly " + cells.getCells().size() + cells.getCells().size() + " cells.");
    }

    public void originalImage(ActionEvent actionEvent) {
    }

    public void close(ActionEvent actionEvent) {
        Platform.exit();
    }
}
