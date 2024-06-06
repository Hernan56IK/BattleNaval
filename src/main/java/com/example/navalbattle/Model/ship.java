package com.example.navalbattle.Model;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;

import javafx.scene.layout.Pane;

public class ship extends Pane {
    public int type;
    public boolean vertical;
    private int health;

    public ship(int type, boolean vertical) {
        this.type = type;
        this.vertical = vertical;
        this.health = type;

        for (int i = 0; i < type; i++) {
            Rectangle square = new Rectangle(37, 33);
            square.setFill(Color.BLUE);
            square.setStroke(Color.BLACK);
            if (vertical) {
                square.setTranslateY(i * 30); // Posicionar verticalmente los rectángulos
            } else {
                square.setTranslateX(i * 30); // Posicionar horizontalmente los rectángulos
            }
            getChildren().add(square); // Añadir rectángulos directamente como hijos de ship (que es un Pane)
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

