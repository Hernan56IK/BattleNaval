package com.example.navalbattle;

import com.example.navalbattle.View.BoardStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        BoardStage.getInstance();
    }

}
