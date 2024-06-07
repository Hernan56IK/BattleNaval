package com.example.navalbattle.View;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class welcomeStage extends Stage {
    public welcomeStage() throws IOException {
        // Load the FXML file that defines the graphical interface of the welcome window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/navalbattle/welcome-view.fxml"));
        Parent root = loader.load();
        // The scene is created and the window is configured
        Scene scene = new Scene(root);
        setResizable(false);
        setTitle("BATALLA NAVAL");
        setScene(scene);
        show(); // Shows the window
    }

    // Static method to obtain a unique instance of the welcome window
    public static welcomeStage getInstance() throws IOException {
        return WelcomeStageHolder.INSTANCE = new welcomeStage();
    }

    // Static method for removing the instance of the welcome window
    public static void deleteInstance() {
        WelcomeStageHolder.INSTANCE.close();
        WelcomeStageHolder.INSTANCE = null;
    }

    // Static class for storing the unique instance of the welcome window
    private static class WelcomeStageHolder {
        private static welcomeStage INSTANCE;
    }
}
