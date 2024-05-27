package com.example.navalbattle.Model;

import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ship extends Parent {
    public int type;
    public boolean vertical;
    private int health;

    public ship(int type, boolean vertical) {
        this.type = type;
        this.vertical = vertical;
        this.health = type;

        VBox vbox = new VBox();
        for (int i = 0; i < type; i++) {
            Rectangle square = new Rectangle(30, 30);
            square.setFill(Color.BLUE);
            square.setStroke(Color.BLACK);
            vbox.getChildren().add(square);
        }

        getChildren().add(vbox);
    }

    public void hit() {
        health--;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
        VBox vbox = new VBox();
        for (int i = 0; i < type; i++) {
            Rectangle square = new Rectangle(30, 30);
            square.setFill(Color.BLUE);
            square.setStroke(Color.BLACK);
            vbox.getChildren().add(square);
        }
        getChildren().clear();
        getChildren().add(vbox);
    }

    public boolean isVertical() {
        return vertical;
    }
}
