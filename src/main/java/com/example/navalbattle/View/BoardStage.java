package com.example.navalbattle.View;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class BoardStage extends Stage {

    public BoardStage() throws IOException {
        // Load the FXML file that defines the graphical interface of the welcome window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/navalBattle/BoardGame.fxml"));
        Parent root = loader.load();
        // The scene is created and the window is configured
        Scene scene = new Scene(root);
        getIcons().add(new Image(String.valueOf(getClass().getResource("/images/barco.png"))));
        setResizable(false);
        setTitle("NAVALBATTLE");
        setScene(scene);
        show(); // Shows the window
    }

    // Static method to obtain a unique instance of the welcome window
    public static BoardStage getInstance() throws IOException {
        return BoardStageHolder.INSTANCE = new BoardStage();
    }

    // Static method for removing the instance of the welcome window
    public static void deleteInstance() {
        BoardStageHolder.INSTANCE.close();
        BoardStageHolder.INSTANCE = null;
    }

    // Static class for storing the unique instance of the welcome window
    private static class BoardStageHolder {
        private static BoardStage INSTANCE;
    }
}
