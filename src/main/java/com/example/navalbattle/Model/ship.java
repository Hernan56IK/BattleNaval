package com.example.navalbattle.Model;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;

import java.io.Serializable;

public class ship extends Pane implements Serializable {
    public int type;
    public boolean vertical;
    private int health;

    public ship(int type, boolean vertical, boolean confirmation, boolean indicatorEnemyShip, boolean horizontal) {
        this.type = type;
        this.vertical = vertical;
        this.health = type;


        for (int i = 0; i < type; i++) {
            Rectangle square = new Rectangle(33, 33);
            square.setFill(Color.BLUE);
            square.setStroke(Color.BLACK);
            // si se detecta que los barcos creados son del enemigo se vuelven invisibles
            if (indicatorEnemyShip==true) {
                square.setFill(Color.TRANSPARENT); // Establecer el color de relleno transparente
                square.setStroke(Color.TRANSPARENT); // Establecer el color del borde transparente
            }
            if (vertical) {
                square.setTranslateY(i * 30); // Posicionar verticalmente los rectángulos
            } else {
                square.setTranslateX(i * 30); // Posicionar horizontalmente los rectángulos
            }

            // rectangulo que va contenido dentro del más grande
            Rectangle square1 = new Rectangle(15, 15);
            square1.setFill(Color.DARKBLUE);
            square1.setStroke(Color.BLACK);
            if(indicatorEnemyShip==true){
                square1.setFill(Color.TRANSPARENT); // Establecer el color de relleno transparente
                square1.setStroke(Color.TRANSPARENT); // Establecer el color del borde transparente
            }
            getChildren().addAll(square, square1);

            // los barcos ocupan una cantidad determinada de casillas, esta variable cambia su valor
            // cuando se llega a la última casilla, ya que en esta se debe generar un triangulo.
            if (!confirmation) {
                //se vuelven invisibles los rectangulos creados para esta etapa
                square.setFill(Color.TRANSPARENT);
                square.setStroke(Color.TRANSPARENT);
                square1.setFill(Color.TRANSPARENT);
                square1.setStroke(Color.TRANSPARENT);
                for (int j = 0; j < type; j++) {
                    Polygon triangle = new Polygon();
                    //varia los puntos dependiendo del sentido del barco
                    triangle.getPoints().addAll(0.0, 0.0,
                            35.0, 0.0,
                            35.0, 30.0
                    );
                    triangle.setFill(Color.PURPLE);
                    if (indicatorEnemyShip == true) {
                        triangle.setFill(Color.TRANSPARENT); // Establecer el color de relleno transparente
                        triangle.setStroke(Color.TRANSPARENT); // Establecer el color del borde transparente
                    }

                    if (vertical && !horizontal){
                        triangle.getPoints().setAll(0.0, 33.0,
                                33.0, 33.0,
                                0.0, 0.0
                        );
                    }

                    if (!vertical && horizontal) {
                        triangle.getPoints().setAll(0.0, 33.0,
                                33.0, 33.0,
                                0.0, 0.0
                        );
                    }
                    getChildren().add(triangle);
                }
            }
        }
    }

    public void hit() {
        health--;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
        for (Node child : getChildren()) {
            if (child instanceof Rectangle) {
                Rectangle square = (Rectangle) child;
                if (vertical) {
                    square.setTranslateX(0); // Resetear posición horizontal
                    square.setTranslateY(getChildren().indexOf(child) * 30); // Posicionar verticalmente
                } else {
                    square.setTranslateY(0); // Resetear posición vertical
                    square.setTranslateX(getChildren().indexOf(child) * 30); // Posicionar horizontalmente
                }
            }
        }
    }

    public boolean isVertical() {
        return vertical;
    }
}



