package com.danylo.imagecomparison.controller;

import com.danylo.imagecomparison.ImageComparator;
import com.danylo.imagecomparison.RectArea;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class MainController {
    @FXML
    private ImageView view1;
    @FXML
    private ImageView view2;
    @FXML
    private Label result;
    @FXML
    private Button btnCompare;
    @FXML
    private Button btnSaveImg;
    @FXML
    private Button btnAddExclusion;
    @FXML
    private Button btnRemoveExclusion;
    @FXML
    private javafx.scene.control.TextField txtTop;
    @FXML
    private TextField txtLeft;
    @FXML
    private TextField txtRight;
    @FXML
    private TextField txtBottom;


    private Parent fxmlError;
    private FXMLLoader fxmlLoader = new FXMLLoader();
    private ErrorController errorController;
    private Stage errorStage;
    private BufferedImage image1;
    private BufferedImage image2;
    private RectArea excludeArea;

    @FXML
    private void initialize() {
        try {
            fxmlLoader.setLocation(getClass().getResource("/error.fxml"));
            fxmlError = fxmlLoader.load();
            errorController = fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadImg1(ActionEvent event) {
        BufferedImage bufferedImage = readFromDisk(event, view1);
        if (bufferedImage != null) {
            image1 = bufferedImage;
            btnAddExclusion.setDisable(false);
        }
        enableCompare();

    }

    @FXML
    private void loadImg2(ActionEvent event) {
        BufferedImage bufferedImage = readFromDisk(event, view2);
        if (bufferedImage != null) {
            image2 = bufferedImage;
        }
        enableCompare();
    }


    @FXML
    private void compare(ActionEvent event) {
        if (image1.getHeight() != image2.getHeight() || image1.getWidth() != image2.getWidth()) {
            result.setText("Images are not equal size.");
        } else {
            ImageComparator imageComparator = new ImageComparator(image1, image2, excludeArea);
            if (imageComparator.compare()) {
                result.setText("Images are equal.");
            } else {
                result.setText("Images are not equal.");
                drawBorders(imageComparator);
                btnSaveImg.setDisable(false);
            }
        }
    }

    @FXML
    private void saveImg(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("JPG Images (*.jpg)", "*.jpg");
        chooser.getExtensionFilters().add(filter);
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            try {
                ImageIO.write(image2, "jpg", file);
            } catch (IOException e) {
                showErrorMessage("File write error", event);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void addExclusion(ActionEvent event) {
        int top, left, right, bottom;
        try {
            top = Integer.parseInt(txtTop.getText());
            left = Integer.parseInt(txtLeft.getText());
            right = Integer.parseInt(txtRight.getText());
            bottom = Integer.parseInt(txtBottom.getText());
            checkBounds(top, right, bottom, left);
            excludeArea = new RectArea(top, right, bottom, left);
            BufferedImage imageWithExclusion = imageClone(image1);
            Graphics2D graphics = imageWithExclusion.createGraphics();
            graphics.setColor(Color.black);
            graphics.drawRect(left, top, right - left, bottom - top);
            view1.setImage(SwingFXUtils.toFXImage(imageWithExclusion, null));
            btnRemoveExclusion.setDisable(false);
            btnAddExclusion.setDisable(true);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            showErrorMessage("Incorrect area bounds.", event);
            e.printStackTrace();
        }
    }

    @FXML
    private void removeExclusion(ActionEvent event) {
        excludeArea = null;
        view1.setImage(SwingFXUtils.toFXImage(image1, null));
        btnRemoveExclusion.setDisable(true);
        btnAddExclusion.setDisable(false);
    }

    private BufferedImage readFromDisk(ActionEvent event, ImageView view) {
        FileChooser chooser = new FileChooser();
        BufferedImage bufferedImage = null;
        try {
            File file = chooser.showOpenDialog(null);
            if (file != null) {
                bufferedImage = ImageIO.read(file);
                if (bufferedImage != null) {
                    Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                    view.setImage(image);
                } else {
                    showErrorMessage("Unsupported file format.", event);
                }
            }
        } catch (Exception e) {
            showErrorMessage("Image read error.", event);
            e.printStackTrace();
        }
        return bufferedImage;
    }

    private void enableCompare() {
        if (image1 != null && image2 != null) {
            btnCompare.setDisable(false);
        }
    }


    private void showErrorMessage(String message, ActionEvent event) {
        if (errorStage == null) {
            errorStage = new Stage();
            errorStage.setTitle("Error");
            errorStage.setScene(new Scene(fxmlError, 250, 100));
            errorStage.setResizable(false);
            errorStage.initModality(Modality.WINDOW_MODAL);
            errorStage.initOwner(((Node) event.getSource()).getScene().getWindow());
        }
        errorController.setErrorMessage(message);
        errorStage.show();
    }

    private void drawBorders(ImageComparator imageComparator) {
        Graphics2D graphics = image2.createGraphics();
        graphics.setColor(Color.red);
        for (RectArea area : imageComparator.getRectAreas()) {
            int x = area.getLeftBound() - 2 < 0 ? 0 : area.getLeftBound() - 2;
            int y = area.getUpperBound() - 2 < 0 ? 0 : area.getUpperBound() - 2;
            int dx = area.getRightBound() + 2 >= image2.getWidth() ? image2.getWidth() - 1 - x : area.getRightBound() + 2 - x;
            int dy = area.getLowerBound() + 2 >= image2.getHeight() ? image2.getHeight() - 1 - y : area.getLowerBound() + 2 - y;
            graphics.drawRect(x, y, dx, dy);
            view2.setImage(SwingFXUtils.toFXImage(image2, null));
        }
    }

    private void checkBounds(int top, int right, int bottom, int left) {
        int imageHeight = image1.getHeight();
        int imageWidth = image1.getWidth();
        System.out.println("height: " + imageHeight + ", width: " + imageWidth);
        if (top < 0 || top >= imageHeight || right < 0 || right >= imageWidth || bottom < 0 || bottom >= imageHeight ||
                left < 0 || left >= imageWidth || top > bottom || left > right) {
            throw new IndexOutOfBoundsException();
        }
    }

    private BufferedImage imageClone(BufferedImage image) {
        ColorModel cm = image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = image.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
