//https://github.com/james02135/Bird-Flock-Analyser/blob/master/Bird%20Flock%20Analyser/BirdFlockAnalyser2/src/application/MainController.java

package sample;

import Controllers.homeViewController;
import Controllers.mainViewController;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.opencv.core.Mat;

import java.util.HashSet;
import java.util.Set;

import static Controllers.mainViewController.mVC;

public class cellsImageProcessing {
    public unionFind uF;

    private Set<Integer> cells = new HashSet<>();
    private int[] redCells;
    private int[] whiteCells;
    private Rectangle[] cellBoxes;
    private Rectangle[] redCellBoxes;
    private Rectangle[] whiteCellBoxes;

    public int imgWidth = (int) mVC.ogImgView.getImage().getWidth();
    public int imgHeight = (int) mVC.ogImgView.getImage().getHeight();

    public cellsImageProcessing() {
        readThroughRedPixels();
        readThroughPurplePixels();
    }

    public Set<Integer> getCells() {
        return cells;
    }

    public int[] getRedCells() { return redCells; }

    public int[] getWhiteCells() { return whiteCells; }

    public Rectangle[] getCellBoxes() {
        return cellBoxes;
    }

    public Rectangle[] getRedCellBoxes() {
        return redCellBoxes;
    }

    public Rectangle[] getWhiteCellBoxes() {
        return whiteCellBoxes;
    }

    private void readThroughRedPixels() {
        redCells = new int[imgWidth * imgHeight];

        PixelReader reader = mVC.tricolorImgView.getImage().getPixelReader();

        int red = 0;
        for (int row = 0; row < imgHeight; row++) {
            for (int col = 0; col < imgWidth; col++) {
                Color color = reader.getColor(col, row);

                redCells[red] = color.equals(Color.RED) ? red : -1;
                red++;
            }
        }
    }

    private void readThroughPurplePixels() {
        whiteCells = new int[imgWidth * imgHeight];

        PixelReader reader = mVC.tricolorImgView.getImage().getPixelReader();

        int purple = 0;
        for (int row = 0; row < imgHeight; row++) {
            for (int col = 0; col < imgWidth; col++) {
                Color color = reader.getColor(col, row);

                whiteCells[purple] = color.equals(Color.PURPLE) ? purple : -1;
                purple++;
            }
        }
    }

    public void findRedCells() {
        if (redCells == null)
            return;

        for (int i = 0; i < redCells.length; i++) {
            if (redCells[i] == -1)
                continue;

            try {
                if (redCells[i + 1] != -1) {
                    // Union with the pixel to the right
                    uF.union(redCells, i, i + 1);
                }

                if (redCells[i + imgWidth] != -1) {
                    // Union with the pixel below
                    uF.union(redCells, i, i + imgWidth);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }

        addRedCellsToSet();
        removeSmallRedCells();
        createRedCellBoxes();
    }

    public void addRedCellsToSet() {
        for (int id = 0; id < redCells.length; id++) {
            int parent = uF.find(redCells, id);
            if (parent != -1)
                cells.add(parent);
        }
    }

    public int redCellOccurrences(int parent) {
        int count = 0;
        for (int i = 0; i < redCells.length; i++) {

            if (parent == uF.find(redCells, i)) {
                count++;
            }
        }
        return count;
    }

    private void removeSmallRedCells() {
        // Remove small sets of birds
        cells.removeIf(b->((double) redCellOccurrences(b) / redCells.length) * 100.0<0.055);
        mVC.imgInfoTxt.setText("There are roughly " + cells.size() + " red blood cells.");
    }

    public void findWhiteCells() {
        if (whiteCells == null)
            return;

        for (int i = 0; i < whiteCells.length; i++) {
            if (whiteCells[i] == -1)
                continue;

            try {
                if (whiteCells[i + 1] != -1) {
                    // Union with the pixel to the right
                    uF.union(whiteCells, i, i + 1);
                }

                if (whiteCells[i + imgWidth] != -1) {
                    // Union with the pixel below
                    uF.union(whiteCells, i, i + imgWidth);
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }

        addWhiteCellsToSet();
        removeSmallWhiteCells();
        createWhiteCellBoxes();
    }

    public void addWhiteCellsToSet() {
        for (int id = 0; id < whiteCells.length; id++) {
            int parent = uF.find(whiteCells, id);
            if (parent != -1)
                cells.add(parent);
        }
    }

    public int whiteCellOccurrences(int parent) {
        int count = 0;
        for (int i = 0; i < whiteCells.length; i++) {

            if (parent == uF.find(whiteCells, i)) {
                count++;
            }
        }
        return count;
    }

    private void removeSmallWhiteCells() {
        // Remove small sets of birds
        cells.removeIf(b->((double) whiteCellOccurrences(b) / whiteCells.length) * 100.0<0.055);
        mVC.imgInfoTxt.setText("There are roughly " + cells.size() + " white blood cells.");
    }

    private void createRedCellBoxes() {
        redCellBoxes = new Rectangle[cells.size()];

        int index = 0;
        for (Integer element : cells) {
            int x = element % imgWidth;
            int y = element / imgWidth;

            int minX = x, maxX = x, minY = y, maxY = y;
            for (int i = 0; i < redCells.length; i++) {
                if (redCells[i] == -1)
                    continue;

                if (element == uF.find(redCells, i)) {
                    int xLoc = i % imgWidth;
                    int yLoc = i / imgWidth;

                    minX = Math.min(minX, xLoc);
                    maxX = Math.max(maxX, xLoc);

                    minY = Math.min(minY, yLoc);
                    maxY = Math.max(maxY, yLoc);
                }

            }

            redCellBoxes[index++] = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        }
    }

    private void createWhiteCellBoxes() {
        whiteCellBoxes = new Rectangle[cells.size()];

        int index = 0;
        for (Integer element : cells) {
            int x = element % imgWidth;
            int y = element / imgWidth;

            int minX = x, maxX = x, minY = y, maxY = y;
            for (int i = 0; i < whiteCells.length; i++) {
                if (whiteCells[i] == -1)
                    continue;

                if (element == uF.find(whiteCells, i)) {
                    int xLoc = i % imgWidth;
                    int yLoc = i / imgWidth;

                    minX = Math.min(minX, xLoc);
                    maxX = Math.max(maxX, xLoc);

                    minY = Math.min(minY, yLoc);
                    maxY = Math.max(maxY, yLoc);
                }

            }

            whiteCellBoxes[index++] = new Rectangle(minX, minY, maxX - minX, maxY - minY);
        }
    }
}
