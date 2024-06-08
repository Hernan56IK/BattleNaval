package com.example.navalbattle;

import com.example.navalbattle.View.BoardStage;
import com.example.navalbattle.View.welcomeStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        welcomeStage.getInstance();
        try (FileOutputStream fileOut  = new FileOutputStream("batalla.naval");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)){
            out.writeObject(board);
            //welcomeStage  = (welcomeStage) in.readObject();

        }catch (IOException i){
            i.printStackTrace();
        }
        try (FileInputStream fileIn  = new FileInputStream("batalla.naval");
             ObjectInputStream In = new ObjectInputStream(fileIn)){
            welcomeStage bu = (welcomeStage) In.readObject();

        }catch (IOException | ClassNotFoundException i){
            i.printStackTrace();
        }
    }

}
