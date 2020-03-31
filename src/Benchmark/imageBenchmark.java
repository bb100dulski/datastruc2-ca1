package Benchmark;

import Controllers.mainViewController;
import javafx.scene.image.Image;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Measurement(iterations=10)
@Warmup(iterations=5)
@Fork(value=1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Thread)
public class imageBenchmark {

    public static Image pic;
    public static File imageFile;
    public static URL url;

    @Setup(Level.Invocation)
    public void setup() throws MalformedURLException {
        imageFile = new File("imageBenchmark.jpg");
        url = imageFile.toURI().toURL();
        pic = new Image(url.toExternalForm());
    }

    @Benchmark
    public void noiseReductionBM() throws IOException {
        mainViewController.mVC.noiseReduction(null);
    }

    @Benchmark
    public void triColorImageBM() throws IOException {
        mainViewController.mVC.tricolorImage(null);
    }

    @Benchmark
    public void detectCellsBM() throws IOException {
        mainViewController.mVC.detectCells(null);
    }

    public static void main(String[] args) throws
            RunnerException, IOException {
        Main.main(args);
    }


}
