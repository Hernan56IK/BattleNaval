package com.example.navalbattle.Model;

import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class ship extends Parent {
    public int type;
    public boolean vertical;
    private int health;

    public ship (int type, boolean vertical, boolean confirmation) {
        this.type = type;
        this.vertical = vertical;
        this.health = type;

        if (confirmation==true) {
            StackPane stackPane = new StackPane();
            for (int i = 0; i < type; i++) {
                Rectangle square = new Rectangle(30, 30);
                square.setFill(Color.BLUE);
                square.setStroke(Color.BLACK);

                Rectangle square1 = new Rectangle(15, 15);
                square1.setFill(Color.DARKBLUE);
                square1.setStroke(Color.BLACK);
                stackPane.getChildren().addAll(square, square1);
                getChildren().add(stackPane);
            }
        }
        else{
            StackPane stackPane = new StackPane();
            for (int i = 0; i < type; i++) {
                Polygon triangle = new Polygon();
                triangle.getPoints().addAll(0.0, 0.0,
                        0.0, 30.0,
                        30.0, 15.0
                );
                triangle.setFill(Color.BLACK);

                Polygon triangle1 = new Polygon();
                triangle1.getPoints().addAll(0.0, 0.0,
                        0.0, 15.0,
                        15.0, 7.5
                );
                triangle1.setFill(Color.GRAY);

                if (vertical == true){
                    Rotate rotate = new Rotate(90, 0, 0); // Ángulo de 90 grados, centro en (0, 0)
                    triangle.getTransforms().add(rotate);
                    Rotate rotate1 = new Rotate(90, -6, -10); // Ángulo de 90 grados, centro en (0, 0)
                    triangle1.getTransforms().add(rotate1);
                }

                stackPane.getChildren().addAll(triangle, triangle1);
                getChildren().add(stackPane);
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
