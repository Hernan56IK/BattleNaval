package com.example.navalbattle.controller;

import com.example.navalbattle.Model.ship;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.Random;

public class boardcontroller {

    @FXML
    private GridPane EnemyBoard;

    @FXML
    private TextField ExitText;

    @FXML
    private BorderPane GeneralPane;

    @FXML
    private Pane SubPaneGeneral;

    @FXML
    private GridPane YouBoard;
    private boolean[][] youBoardCells;
    private ship selectedShip;
    // Declarar variables de seguimiento de la cantidad de barcos colocados y los límites máximos permitidos
    private int shipsOfSize6 = 0;
    private int shipsOfSize4 = 0;
    private int shipsOfSize2 = 0;
    private final int MAX_SHIPS_OF_SIZE_6 = 1; // Máximo permitido de barcos de tamaño 6
    private final int MAX_SHIPS_OF_SIZE_4 = 1; // Máximo permitido de barcos de tamaño 4
    private final int MAX_SHIPS_OF_SIZE_2 = 2; // Máximo permitido de barcos de tamaño 2
    public void initialize() {
        int numRows = YouBoard.getRowConstraints().size();
        int numCols = YouBoard.getColumnConstraints().size();

        youBoardCells = new boolean[numRows][numCols]; // Para rastrear celdas ocupadas

        initializeBoard(YouBoard);
        initializeBoard(EnemyBoard);
    }

    private void initializeBoard(GridPane board) {
        int numRows = board.getRowConstraints().size();
        int numCols = board.getColumnConstraints().size();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Pane cell = new Pane();
                Rectangle rect = new Rectangle(30, 30);
                rect.setFill(Color.LIGHTGRAY);
                rect.setStroke(Color.BLACK); // Establece el color del borde
                cell.getChildren().add(rect);
                board.add(cell, col, row);


                int finalRow = row;
                int finalCol = col;
                cell.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        handlePrimaryClick(rect, finalRow, finalCol);
                    } else if (board == YouBoard && event.getButton() == MouseButton.SECONDARY ) {
                        handleSecondaryClick(finalRow, finalCol);
                    }
                });

            }
        }
    }

    private void handlePrimaryClick(Rectangle rect, int row, int col) {
        boolean hasShip = youBoardCells[row][col];
        if (hasShip) {
            rect.setFill(Color.RED); // Si hay parte del barco en esta área, ponerla de color rojo
        } else {
            rect.setFill(Color.BLACK); // Si no hay parte del barco en esta área, ponerla de color negro
        }
    }

    // Método para manejar el clic secundario
    private void handleSecondaryClick(int row, int col) {
        // Verificar si aún se pueden colocar barcos del tamaño deseado dentro de los límites permitidos
        if ((shipsOfSize6 < MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 < MAX_SHIPS_OF_SIZE_4 && shipsOfSize2 < MAX_SHIPS_OF_SIZE_2) ||
                (shipsOfSize6 == MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 < MAX_SHIPS_OF_SIZE_4 * 2 && shipsOfSize2 < MAX_SHIPS_OF_SIZE_2) ||
                (shipsOfSize6 < MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 == MAX_SHIPS_OF_SIZE_4 && shipsOfSize2 < MAX_SHIPS_OF_SIZE_2 * 2) ||
                (shipsOfSize6 < MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 < MAX_SHIPS_OF_SIZE_4 && shipsOfSize2 == MAX_SHIPS_OF_SIZE_2)) {

            Random random = new Random();
            int shipSize;
            boolean vertical = random.nextBoolean();

            // Elegir aleatoriamente el tamaño del barco
            if (shipsOfSize6 < MAX_SHIPS_OF_SIZE_6) {
                shipSize = 6; // Si aún no se han colocado todos los barcos de tamaño 6, se coloca un barco de tamaño 6
                shipsOfSize6++; // Incrementar el contador de barcos de tamaño 6
            } else if (shipsOfSize4 < MAX_SHIPS_OF_SIZE_4) {
                shipSize = 4; // Si ya se han colocado los barcos de tamaño 6, se coloca un barco de tamaño 4
                shipsOfSize4++; // Incrementar el contador de barcos de tamaño 4
            } else {
                shipSize = 2; // Si ya se han colocado los barcos de tamaño 6 y 4, se coloca un barco de tamaño 2
                shipsOfSize2++; // Incrementar el contador de barcos de tamaño 2
            }

            // Verificar si es posible colocar el barco en la posición deseada y colocarlo si es posible
            if (canPlaceShip(row, col, shipSize, vertical)) {
                placeShip(row, col, shipSize, vertical, YouBoard);
            } else if (canPlaceShip(row, col, shipSize, !vertical)) {
                placeShip(row, col, shipSize, !vertical, YouBoard);
            } else {
                System.out.println("No se puede colocar el barco en esta posición.");
            }
        } else {
            System.out.println("Se ha alcanzado el límite de barcos permitidos.");
        }
    }
    // Método para verificar si es posible colocar un barco en la posición deseada sin violar restricciones
    private boolean canPlaceShip(int row, int col, int shipSize, boolean vertical) {
        int numRows = YouBoard.getRowConstraints().size();
        int numCols = YouBoard.getColumnConstraints().size();

        // Verificar si el barco se sale de la grilla en la dirección elegida
        if (vertical) {
            if (row + shipSize - 1 >= numRows) return false; // Si el barco se sale de la grilla hacia abajo, no se puede colocar
        } else {
            if (col + shipSize - 1 >= numCols) return false; // Si el barco se sale de la grilla hacia la derecha, no se puede colocar
        }

        // Verificar si el barco se superpone con otros barcos en la grilla
        for (int i = 0; i < shipSize; i++) {
            int checkRow = vertical ? row + i : row;
            int checkCol = vertical ? col : col + i;

            if (youBoardCells[checkRow][checkCol]) return false; // Si hay un barco en la posición deseada, no se puede colocar
        }

        // Si no se violan las restricciones, se puede colocar el barco en esta posición
        return true;
    }

    // Método para colocar el barco en la posición deseada en la grilla
    private void placeShip(int row, int col, int shipSize, boolean vertical, GridPane board) {
        // Colocar el barco en la grilla
        for (int i = 0; i < shipSize; i++) {
            boolean booleanFinish = true;
            int shipRow = vertical ? row + i : row;
            int shipCol = vertical ? col : col + i;

            if (i == shipSize - 1){
                booleanFinish = false;
            }

            youBoardCells[shipRow][shipCol] = true; // Marcar la celda como ocupada por el barco
            ship newShip = new ship(1, vertical, booleanFinish); // Crear una nueva instancia de barco con tamaño 1x1 y dirección especificada
            board.add(newShip, shipCol, shipRow); // Agregar el barco a la grilla en la posición adecuada
        }
    }
}

