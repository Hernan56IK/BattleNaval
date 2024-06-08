package com.example.navalbattle.controller;
import com.example.navalbattle.Model.boatPositionException;
import com.example.navalbattle.View.BoardStage;
import com.example.navalbattle.View.welcomeStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.example.navalbattle.View.Alert.AlertBox;

import java.io.IOException;

public class welcomeController {

    @FXML
    private TextField AgeTextField;

    @FXML
    private TextField NameTextField;

    private String nickname;
    private int age;

    /**
     *
     * @param event
     * @throws IOException
     */
    @FXML
    void onHandlerButtonPlayy(ActionEvent event) throws IOException {
        nickname = NameTextField.getText();// Obtains the name entered by the user
        age = Integer.parseInt(AgeTextField.getText());

        // Verifies if the name contains only letters (without special characters)
        if (nickname.matches("[a-zA-ZñÑ]+")) {

            // Shows a confirmation window if the name is valid
            new AlertBox().showConfirm("CONFIRMACION","El nombre es valido","el nombre se ha guardado con exito");

            if (age>=12){
                // shows a confirmation window if the age is valid
                new AlertBox().showConfirm("CONFIRMACION","La edad ingresada es valida","la edad se ha guardado con exito");

                // Set the name in the game controller
                BoardStage.getInstance();

                // Close the welcome window
                welcomeStage.deleteInstance();
            }
            else{
                // si la edad no es valida, entra en la excepción y muestra una alerta
                try {
                    validate(age); //metodo estatico que valida la edad
                } catch(boatPositionException b){
                    new AlertBox().showMessage("NO TIENES LA EDAD REQUERIDA","Te recomendamos ir a jugar con otros niños de tu edad","Si te haz equivocado, ingresa nuevamente tu edad");
                    System.out.println(b.getMessage());
                }
            }
        } else {
            // si el nombre no es valido, entra en la excepción y muestra una alerta
            try {
                validate1(nickname); //metodo estatico que valida el nombre
            } catch(boatPositionException b){
                new AlertBox().showMessage("ERROR DE ESCRITURA","El nickname ingresado contiene caracteres especiales no validos","Por favor ingresar solo letras");
                System.out.println(b.getMessage());
            }
            // Shows an error message if the name contains special characters
        }
    }
    static void validate (int age) throws boatPositionException {
        if (age<12){
            throw new boatPositionException("No tienes la edad requerida para jugar");
        }
    }
    static void validate1 (String nickname) throws boatPositionException {
        if (!nickname.matches("[a-zA-ZñÑ]+")) {
            throw new boatPositionException("El nombre ingresado es invalido");
        }
    }

}
