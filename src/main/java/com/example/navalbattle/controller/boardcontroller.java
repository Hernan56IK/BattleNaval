package com.example.navalbattle.controller;

import com.example.navalbattle.Model.ship;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
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
    private TextField outputTextField;

    @FXML
    private BorderPane GeneralPane;

    @FXML
    private Pane SubPaneGeneral;

    @FXML
    private GridPane YouBoard;
    private boolean[][] youBoardCells;
    private boolean[][] enemyBoardCells;
    private ship selectedShip;

    // Declarar variables de seguimiento de la cantidad de barcos colocados y los límites máximos permitidos
    private int shipsOfSize6 = 0;
    private int shipsOfSize4 = 0;
    private int shipsOfSize2 = 0;
    private int EnemyshipsOfSize6 = 0;
    private int EnemyshipsOfSize4 = 0;
    private int EnemyshipsOfSize2 = 0;
    private final int MAX_SHIPS_OF_SIZE_6 = 1; // Máximo permitido de barcos de tamaño 6
    private final int MAX_SHIPS_OF_SIZE_4 = 1; // Máximo permitido de barcos de tamaño 4
    private final int MAX_SHIPS_OF_SIZE_2 = 2; // Máximo permitido de barcos de tamaño 2
    // Ruta del GIF de explosión
    private final String EXPLOSION_GIF_PATH = "file:src/main/resources/explosion.gif";

    public void initialize() {


        int numRows = YouBoard.getRowConstraints().size();
        int numCols = YouBoard.getColumnConstraints().size();

        youBoardCells = new boolean[numRows][numCols]; // Para rastrear celdas ocupadas
        enemyBoardCells = new boolean[numRows][numCols];

        initializeBoard(YouBoard);
        initializeBoard(EnemyBoard);

        for(int i=0;i<5;i++){
            Random random=new Random();
            int rows = random.nextInt(EnemyBoard.getRowConstraints().size());
            int cols = random.nextInt(EnemyBoard.getColumnConstraints().size());
            RandomEnemyShips(EnemyBoard,rows,cols);
        }
    }

    private void initializeBoard(GridPane board) {
        int numRows = board.getRowConstraints().size();
        int numCols = board.getColumnConstraints().size();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Pane cell = new Pane();
                Rectangle rect = new Rectangle(30, 30);
                rect.setFill(Color.TRANSPARENT);
                rect.setStroke(Color.BLACK); // Establece el color del borde
                cell.getChildren().add(rect);
                board.add(cell, col, row);


                int finalRow = row;
                int finalCol = col;
                cell.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        handlePrimaryClick(board,rect, finalRow, finalCol);
                    } else if (board == YouBoard && event.getButton() == MouseButton.SECONDARY ) {
                        handleSecondaryClick(board,finalRow, finalCol);
                    }
                });

            }
        }
    }

    private void handlePrimaryClick(GridPane board, Rectangle rect, int row, int col) {
        try {
            boolean hasShip = youBoardCells[row][col];
            boolean EnemyHasShip = enemyBoardCells[row][col];
            if (hasShip && board == YouBoard) {
                outputTextField.clear();
                rect.setFill(Color.RED); // Si hay parte del barco en esta área, ponerla de color rojo
                outputTextField.appendText("Barco aliado dañado\n");
            } else if (EnemyHasShip && board == EnemyBoard) {
                outputTextField.clear();
                rect.setFill(Color.RED); // Si hay parte del barco en esta área, ponerla de color rojo
                outputTextField.appendText("Encontrado\n");
            } else {
                outputTextField.clear();
                rect.setFill(Color.BLACK); // Si no hay parte del barco en esta área, ponerla de color negro
                outputTextField.appendText("Fallado\n");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Índice fuera de límites: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error desconocido: " + e.getMessage());
        }
    }

    // Método para manejar el clic secundario
    private void handleSecondaryClick(GridPane Iboard, int row, int col) {
        GridPane Eboard = Iboard;

        if (Eboard != YouBoard) {
            return; // Si no es el tablero del jugador, no hacer nada
        }

        // Verificar si aún se pueden colocar barcos del tamaño deseado dentro de los límites permitidos
        if (shipsOfSize6 >= MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 >= MAX_SHIPS_OF_SIZE_4 && shipsOfSize2 >= MAX_SHIPS_OF_SIZE_2) {
            System.out.println("Se ha alcanzado el límite de barcos permitidos.");
            return;
        }

        Random random = new Random();
        int shipSize;
        boolean placed = false;
        boolean vertical = random.nextBoolean();

        // Elegir aleatoriamente el tamaño del barco
        if (shipsOfSize6 < MAX_SHIPS_OF_SIZE_6) {
            shipSize = 6;
        } else if (shipsOfSize4 < MAX_SHIPS_OF_SIZE_4) {
            shipSize = 4;
        } else {
            shipSize = 2;
        }

        // Intentar colocar el barco en varias direcciones hasta encontrar una válida
        for (int attempt = 0; attempt < 4; attempt++) {
            if (canPlaceShip(Eboard, row, col, shipSize, vertical, true)) {
                placeShip(row, col, shipSize, vertical, Eboard, true);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, vertical, false)) {
                placeShip(row, col, shipSize, vertical, Eboard, false);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, true)) {
                placeShip(row, col, shipSize, !vertical, Eboard, true);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, false)) {
                placeShip(row, col, shipSize, !vertical, Eboard, false);
                placed = true;
                break;
            }
            vertical = !vertical; // Cambiar la orientación para el siguiente intento
        }

        if (placed) {
            incrementShipCount(shipSize);
        } else {
            System.out.println("No se puede colocar el barco en esta posición.");
        }
    }

    private void incrementShipCount(int shipSize) {
        if (shipSize == 6) {
            shipsOfSize6++;
        } else if (shipSize == 4) {
            shipsOfSize4++;
        } else {
            shipsOfSize2++;
        }
    }

    // Método para verificar si es posible colocar un barco en la posición deseada sin violar restricciones
    private boolean canPlaceShip(GridPane board, int row, int col, int shipSize, boolean vertical, boolean positiveDirection) {
        int numRows = board.getRowConstraints().size();
        int numCols = board.getColumnConstraints().size();

        if (vertical) {
            if (positiveDirection) {
                if (row + shipSize - 1 >= numRows) return false; // Si el barco se sale de la grilla hacia abajo, no se puede colocar
            } else {
                if (row - shipSize + 1 < 0) return false; // Si el barco se sale de la grilla hacia arriba, no se puede colocar
            }
        } else {
            if (positiveDirection) {
                if (col + shipSize - 1 >= numCols) return false; // Si el barco se sale de la grilla hacia la derecha, no se puede colocar
            } else {
                if (col - shipSize + 1 < 0) return false; // Si el barco se sale de la grilla hacia la izquierda, no se puede colocar
            }
        }

        for (int i = 0; i < shipSize; i++) {
            int checkRow = vertical ? (positiveDirection ? row + i : row - i) : row;
            int checkCol = vertical ? col : (positiveDirection ? col + i : col - i);

            if (board == YouBoard) {
                if (youBoardCells[checkRow][checkCol]) return false; // Si hay un barco en la posición deseada, no se puede colocar
            } else {
                if (enemyBoardCells[checkRow][checkCol]) return false; // Si hay un barco en la posición deseada, no se puede colocar
            }
        }
        return true;
    }



    // Método para colocar el barco en la posición deseada en la grilla
    private void placeShip(int row, int col, int shipSize, boolean vertical, GridPane theboard, boolean positiveDirection) {
        GridPane Myboard = theboard;

        for (int i = 0; i < shipSize; i++) {
            int shipRow = vertical ? (positiveDirection ? row + i : row - i) : row;
            int shipCol = vertical ? col : (positiveDirection ? col + i : col - i);

            if (Myboard == YouBoard) {
                youBoardCells[shipRow][shipCol] = true; // Marcar la celda como ocupada por el barco
            }
            if (Myboard == EnemyBoard) {
                enemyBoardCells[shipRow][shipCol] = true; // Marcar la celda como ocupada por el barco
            }
            ship newShip = new ship(1, vertical); // Crear una nueva instancia de barco con tamaño 1x1 y dirección especificada
            Myboard.add(newShip, shipCol, shipRow); // Agregar el barco a la grilla en la posición adecuada
            newShip.toBack();
        }
    }

    private void RandomEnemyShips(GridPane Iboard, int row, int col) {
        GridPane Eboard = Iboard;

        if (Eboard != EnemyBoard) {
            return; // Si no es el tablero del enemigo, no hacer nada
        }

        // Verificar si aún se pueden colocar barcos del tamaño deseado dentro de los límites permitidos
        if (EnemyshipsOfSize6 >= MAX_SHIPS_OF_SIZE_6 && EnemyshipsOfSize4 >= MAX_SHIPS_OF_SIZE_4 && EnemyshipsOfSize2 >= MAX_SHIPS_OF_SIZE_2) {
            System.out.println("Se ha alcanzado el límite de barcos permitidos.");
            return;
        }

        Random random = new Random();
        int shipSize;
        boolean placed = false;
        boolean vertical = random.nextBoolean();

        // Elegir aleatoriamente el tamaño del barco
        if (EnemyshipsOfSize6 < MAX_SHIPS_OF_SIZE_6) {
            shipSize = 6;
        } else if (EnemyshipsOfSize4 < MAX_SHIPS_OF_SIZE_4) {
            shipSize = 4;
        } else {
            shipSize = 2;
        }

        // Intentar colocar el barco en varias direcciones hasta encontrar una válida
        for (int attempt = 0; attempt < 4; attempt++) {
            if (canPlaceShip(Eboard, row, col, shipSize, vertical, true)) {
                placeShip(row, col, shipSize, vertical, Eboard, true);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, vertical, false)) {
                placeShip(row, col, shipSize, vertical, Eboard, false);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, true)) {
                placeShip(row, col, shipSize, !vertical, Eboard, true);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, false)) {
                placeShip(row, col, shipSize, !vertical, Eboard, false);
                placed = true;
                break;
            }
            vertical = !vertical; // Cambiar la orientación para el siguiente intento
        }

        if (placed) {
            incrementEnemyShipCount(shipSize);
        } else {
            System.out.println("No se puede colocar el barco en esta posición.");
        }
    }

    private void incrementEnemyShipCount(int shipSize) {
        if (shipSize == 6) {
            EnemyshipsOfSize6++;
        } else if (shipSize == 4) {
            EnemyshipsOfSize4++;
        } else {
            EnemyshipsOfSize2++;
        }
    }

}

