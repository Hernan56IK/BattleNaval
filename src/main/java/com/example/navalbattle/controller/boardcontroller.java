package com.example.navalbattle.controller;

import com.example.navalbattle.Model.ship;
import com.example.navalbattle.View.Alert.AlertBox;
import com.example.navalbattle.View.BoardStage;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
    private final int RECTANGLE_SIZE = 30;


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
    private final String EXPLOSION_GIF_PATH = "file:src/main/resources/images/explosion.gif";
    //total de enemigos en las grillas
    private int totalEnemyShips;
    private int totalPlayerShips;
    //enemigos o aliados encontrados
    private int FoundEnemy;
    private int FoundAliade;


    public void initialize() {


        int numRows = YouBoard.getRowConstraints().size();
        int numCols = YouBoard.getColumnConstraints().size();

        youBoardCells = new boolean[numRows][numCols]; // Para rastrear celdas ocupadas
        enemyBoardCells = new boolean[numRows][numCols];

        initializeBoard(YouBoard);
        initializeBoard(EnemyBoard);

        boolean enemy=false;
        do{
            Random random=new Random();
            int rows = random.nextInt(EnemyBoard.getRowConstraints().size());
            int cols = random.nextInt(EnemyBoard.getColumnConstraints().size());
            RandomEnemyShips(EnemyBoard,rows,cols);
            totalEnemyShips=countTrueValues(enemyBoardCells);
            System.out.println(totalEnemyShips);
            if (EnemyshipsOfSize6 == MAX_SHIPS_OF_SIZE_6 && EnemyshipsOfSize4 == MAX_SHIPS_OF_SIZE_4 && EnemyshipsOfSize2 == MAX_SHIPS_OF_SIZE_2){
                hideEnemyShips();
                return;
            }
        }while(!enemy);

    }

    private void initializeBoard(GridPane board) {
        int numRows = board.getRowConstraints().size();
        int numCols = board.getColumnConstraints().size();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Pane cell = new Pane();
                Rectangle rect = new Rectangle(37,33);
                rect.setFill(Color.TRANSPARENT);
                rect.setStroke(Color.BLACK); // Establece el color del borde
                cell.getChildren().add(rect);
                board.add(cell, col, row);

                // Establecer el tamaño de la celda de la grilla
                board.getColumnConstraints().get(col).setPrefWidth(RECTANGLE_SIZE);
                board.getRowConstraints().get(row).setPrefHeight(RECTANGLE_SIZE);

                int finalRow = row;
                int finalCol = col;
                cell.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && board==EnemyBoard && (rect.getFill().equals(Color.TRANSPARENT))){
                        handlePrimaryClick(board,rect, finalRow, finalCol, true);
                        enemyTurn();
                    } else if (board == YouBoard && event.getButton() == MouseButton.SECONDARY ) {
                        handleSecondaryClick(board,finalRow, finalCol);
                        totalPlayerShips=countTrueValues(youBoardCells);
                        System.out.println(totalPlayerShips);
                    }else{
                        outputTextField.clear();
                        outputTextField.appendText("Primero coloca tus barcos.\n");
                    }
                });

            }
        }
    }

    private void handlePrimaryClick(GridPane board, Rectangle rect, int row, int col, boolean isPlayer) {
        try {
            if (board == EnemyBoard && !allShipsPlaced()) {
                outputTextField.clear();
                outputTextField.appendText("Primero coloca tus barcos.\n");
                return; // Salir del método si no se han colocado todos los barcos
            }

            boolean hasShip = youBoardCells[row][col];
            boolean EnemyHasShip = enemyBoardCells[row][col];
            if (isPlayer) {
                if (board == YouBoard) {
                    outputTextField.clear();
                    outputTextField.appendText("Zona invalida\n");
                } else if (board == EnemyBoard) {
                    outputTextField.clear();
                    if (EnemyHasShip) {
                        rect.setFill(Color.RED); // Si hay parte del barco en esta área, ponerla de color rojo
                        outputTextField.appendText("Enemigo Encontrado\n");
                        FoundEnemy++;
                        if(FoundEnemy==totalEnemyShips){
                            new AlertBox().showConfirm("GAME OVER","HAS DESTRUIDO LA FLOTA ENEMIGA","FELICIDADES HAS GANADOS");
                            BoardStage.deleteInstance();
                        }
                    } else {
                        rect.setFill(Color.BLACK); // Si no hay parte del barco en esta área, ponerla de color negro
                        outputTextField.appendText("Falló\n");
                    }

                }
            } else {
                if (hasShip) {
                    outputTextField.clear();
                    rect.setFill(Color.RED); // Si hay parte del barco en esta área, ponerla de color rojo
                    outputTextField.appendText("Aliado dañado\n");
                    FoundAliade++;
                    if(FoundAliade==totalPlayerShips){
                        new AlertBox().showConfirm("GAME OVER","HAN DESTRUIDO LA FLOTA ALIADA...","TU PIERDES...");
                        BoardStage.deleteInstance();
                    }
                } else {
                    outputTextField.clear();
                    rect.setFill(Color.BLACK); // Si no hay parte del barco en esta área, ponerla de color negro
                    outputTextField.appendText("Maquina Falló\n");
                }
            }



        } catch (ArrayIndexOutOfBoundsException e) {
            outputTextField.appendText("Índice fuera de límites: ");
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

    // Método para verificar si todos los barcos han sido colocados en YouBoard
    private boolean allShipsPlaced() {
        return shipsOfSize6 == MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 == MAX_SHIPS_OF_SIZE_4 && shipsOfSize2 == MAX_SHIPS_OF_SIZE_2;
    }

    private void enemyTurn() {
        // Verificar si todos los barcos han sido colocados
        if (!allShipsPlaced()) {
            return;
        }

        // Generar un ataque automático del enemigo
        Random random = new Random();
        int numRows = YouBoard.getRowConstraints().size();
        int numCols = YouBoard.getColumnConstraints().size();
        int row, col;

        do {
            row = random.nextInt(numRows);
            col = random.nextInt(numCols);

            // Verificar si la celda está vacía (color transparente)
            Node node = null;
            for (Node child : YouBoard.getChildren()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                Integer colIndex = GridPane.getColumnIndex(child);
                if (rowIndex != null && colIndex != null && rowIndex.intValue() == row && colIndex.intValue() == col) {
                    node = child;
                    break;
                }
            }

            if (node != null && node instanceof Pane) {
                Rectangle rect = (Rectangle) ((Pane) node).getChildren().get(0);
                if (rect.getFill().equals(Color.TRANSPARENT)||rect.getFill().equals(Color.BLUE)) {
                    // Si la celda está vacía, es un ataque válido
                    handlePrimaryClick(YouBoard, rect, row, col, false);
                    return;
                }
            }
        } while (true);
    }


    // Método para ocultar los barcos en EnemyBoard
    private void hideEnemyShips() {
        for (Node child : EnemyBoard.getChildren()) {
            if (child instanceof Pane) {
                Rectangle rect = (Rectangle) ((Pane) child).getChildren().get(0);
                rect.setFill(Color.TRANSPARENT);
            }
        }
    }

    private int countTrueValues(boolean[][] matrix) {
        int count = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }

}

