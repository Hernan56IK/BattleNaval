package com.example.navalbattle.Model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

// Clase 'ship' que representa un barco en el juego
public class ship extends Pane {
    public int type; // Tamaño del barco


    // Constructor de la clase 'ship'
    public ship(int type) {
        this.type = type; // Inicializa el tamaño del barco

        // Crear las secciones del barco representadas por rectángulos
        for (int i = 0; i < type; i++) {
            Rectangle square = new Rectangle(33, 33); // Crea un rectángulo de 33x33 píxeles
            square.setFill(Color.BLUE); // Establece el color de relleno del rectángulo a azul
            square.setStroke(Color.BLACK); // Establece el color del borde del rectángulo a negro
            square.setTranslateX(i * 30); // Posiciona horizontalmente los rectángulos con un espacio de 30 píxeles entre ellos
            getChildren().add(square); // Añade los rectángulos como hijos directos de 'ship' (que es un Pane)
        }
    }

}
