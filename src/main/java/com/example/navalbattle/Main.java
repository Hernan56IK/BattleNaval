package com.example.navalbattle;

import com.example.navalbattle.View.BoardStage;
import com.example.navalbattle.View.welcomeStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        welcomeStage.getInstance();
    }

}
