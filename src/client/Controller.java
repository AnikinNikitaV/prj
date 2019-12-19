package client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.*;


import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.stage.StageStyle;


public class Controller {

    @FXML
    private TextField nicknameEditor;

    @FXML
    private Button connectButton;

    @FXML
    void initialize() {

        connectButton.setOnAction(event -> {

            if (!nicknameEditor.getText().equals("")) {
                Parent root1;
                try {
                    root1 = FXMLLoader.load(getClass().getResource("main.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle(nicknameEditor.getText());
                    stage.setScene(new Scene(root1, 1280, 720));
                    stage.setResizable(false);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.show();

                    Stage startStage = (Stage) connectButton.getScene().getWindow();
                    startStage.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            });

        nicknameEditor.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                connectButton.fire();
            }
        });

    }
}