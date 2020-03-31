package Tests;

import Controllers.mainViewController;
import javafx.scene.image.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sample.cellsImageProcessing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class imageTest {

    File imageFile = new File("imageBenchmark.jpg");

    @BeforeEach
    void setUp() throws MalformedURLException {
    }

    @AfterEach
    void tearDown() {
    }

    //https://stackoverflow.com/questions/31279009/testing-image-files-with-junit
    @Test
    void add() {
        assertNotNull(imageFile);
        assertEquals(40707, imageFile.length());
    }

    @Test
    void imagePath() {
        assertNotNull(imageFile);
        assertEquals("imageBenchmark.jpg", imageFile.getPath());
    }


}
