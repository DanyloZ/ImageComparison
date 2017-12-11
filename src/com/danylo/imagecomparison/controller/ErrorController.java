package com.danylo.imagecomparison.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorController {

    @FXML
    private Label lblError;

    @FXML
    private void closeErrorWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    void setErrorMessage(String message) {
        lblError.setText(message);
    }

}
