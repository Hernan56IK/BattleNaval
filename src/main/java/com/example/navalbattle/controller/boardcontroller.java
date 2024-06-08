package com.example.navalbattle.controller;

import com.example.navalbattle.Model.boatPositionException;
import com.example.navalbattle.Model.ship;
import com.example.navalbattle.View.Alert.AlertBox;
import com.example.navalbattle.View.BoardStage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * @author Juan Camilo Jimenez Toro
 * @author Hernan Dario Garcia Mahecha
 * @author James Sanchez
 * @version 2.0
 * @deprecated
 */

public class boardcontroller implements Serializable {

    //Boton de ayuda con las instrucciones
    @FXML
    private Button HelpButton;

    //Grilla del enemigo
    @FXML
    private GridPane EnemyBoard;
    //Grilla del jugador
    @FXML
    private GridPane YouBoard;

    //TexField del Jugador y del enemigo
    @FXML
    private TextField YouTextField;
    @FXML
    private TextField EnemyText;

    //BordenPane que es la base donde esta contenido y se debe inicializar
    @FXML
    private BorderPane GeneralPane;

    @FXML
    private Pane SubPaneGeneral;

    //Matrices que almacenan las posiciones donde hay barcos colocando trues donde los hay
    private boolean[][] youBoardCells;
    private boolean[][] enemyBoardCells;

    //tamaño de los rectangulos
    private final int RECTANGLE_SIZE = 30;

    public boardcontroller(){}


    // cantidad de barcos de lontigud 6 en el tablero del jugador
    private int shipsOfSize6 = 0;
    // cantidad de barcos de lontigud 4 en el tablero del jugador
    private int shipsOfSize4 = 0;
    // cantidad de barcos de lontigud 2 en el tablero del jugador
    private int shipsOfSize2 = 0;

    // cantidad de barcos de lontigud 6 en el tablero del jugador
    private int EnemyshipsOfSize6 = 0;
    // cantidad de barcos de lontigud 4 en el tablero del jugador
    private int EnemyshipsOfSize4 = 0;
    // cantidad de barcos de lontigud 2 en el tablero del jugador
    private int EnemyshipsOfSize2 = 0;
    private final int MAX_SHIPS_OF_SIZE_6 = 1; // Máximo permitido de barcos de tamaño 6
    private final int MAX_SHIPS_OF_SIZE_4 = 2; // Máximo permitido de barcos de tamaño 4
    private final int MAX_SHIPS_OF_SIZE_2 = 3; // Máximo permitido de barcos de tamaño 2

    //imagen que contrenda el gif de la explosion
    private Image explosionImageView;

    //total de barcos enemigos en las grillas
    private int totalEnemyShips;
    //total de barcos aliados en las grillas
    private int totalPlayerShips;

    //enemigos o aliados encontrados
    private int FoundEnemy;
    private int FoundAliade;

    //booleano para permitir o no dar click durante el tiempo de espera que juega la maquina
    private boolean canclick=true;

    // variable entera que ayuda a que no se reproduzca el try catch de alerta de una de las excepciones más de una vez
    private int usagesMethodCanPlaceShip = 0;


    /**
     * It is responsible for initializing the game, so it does not receive parameters.
     */
    public void initialize() {
        // Ruta del GIF de explosión
        String EXPLOSION_GIF_PATH = "file:src/main/resources/images/explosion.gif";
        //inicializador de la imagen de explosion
        explosionImageView = new Image(EXPLOSION_GIF_PATH);

        // carga imagen de fondo en YouBoard
        setBackgroundImage(YouBoard, "file:src/main/resources/images/mar.png");
        // carga imagen de fondo en YouBoard
        setBackgroundImage(EnemyBoard, "file:src/main/resources/images/mar.png");
        // carga imagen de fondo de fono que es la del piso metalico en border pane
        setBackgroundImagePane(GeneralPane, "file:src/main/resources/images/fondo.jpg");


        int numRows = YouBoard.getRowConstraints().size();//carga la cantidad de filas del board del jugador
        int numCols = YouBoard.getColumnConstraints().size();//carga la cantidad de columnas del board del jugador

        //inicializa la matriz donde se guardaran las posiciones de los barscos del jugador
        youBoardCells = new boolean[numRows][numCols];
        //inicializa la matriz donde se guardaran las posiciones de los barscos del enemigo
        enemyBoardCells = new boolean[numRows][numCols];

        initializeBoard(YouBoard);//Crea la board del jugador con los cuadros que se pueden dar click
        initializeBoard(EnemyBoard);//Crea la board del enemigo con los cuadros que se pueden dar click


        /*Este bucle do-while se utiliza para colocar los barcos enemigos de manera aleatoria en el tablero del enemigo hasta que se cumplan ciertas condiciones. Los comentarios explican cada paso:

          boolean enemy=false;: Se inicializa una variable booleana enemy como falsa para controlar la ejecución del bucle.
          El bucle do-while se ejecuta al menos una vez y continúa mientras la variable enemy sea falsa.
          Se genera una fila y una columna aleatoria dentro de los límites del tablero del enemigo.
          Se colocan barcos aleatorios en el tablero del enemigo en la posición determinada por las filas y columnas aleatorias.
          Se cuenta el total de barcos enemigos colocados en el tablero.
          Si se han colocado todos los barcos enemigos según los límites establecidos, se ocultan los barcos y se sale del bucle.*/

        boolean enemy=false;
        do{
            // Se crea una instancia de la clase Random para generar números aleatorios.
            Random random=new Random();
            // Se obtienen índices aleatorios para seleccionar una celda en el tablero del enemigo.
            int rows = random.nextInt(EnemyBoard.getRowConstraints().size());
            int cols = random.nextInt(EnemyBoard.getColumnConstraints().size());
            // Se colocan barcos aleatorios en el tablero del enemigo en la celda seleccionada.
            RandomEnemyShips(EnemyBoard,rows,cols);
            // Se cuenta el total de barcos enemigos colocados en el tablero.
            totalEnemyShips=countTrueValues(enemyBoardCells);
            // Si se han colocado todos los barcos enemigos según los límites establecidos, se ocultan los barcos y se sale del bucle.
            if (EnemyshipsOfSize6 == MAX_SHIPS_OF_SIZE_6 && EnemyshipsOfSize4 == MAX_SHIPS_OF_SIZE_4 && EnemyshipsOfSize2 == MAX_SHIPS_OF_SIZE_2){
                hideEnemyShips();
                return;
            }
        }while(!enemy);

    }

    /**
     *  The grid (GridPane) is received as a parameter for initialization.
     * The number of rows (numRows) and columns (numCols) of the grid are obtained.
     * Iteration occurs over each row and column to create a cell in the grid.
     * For each cell, a new panel (Pane) is created, acting as a visual container.
     * A rectangle (Rectangle) is created to represent the visual content of the cell, with a specific size.
     * The fill color and border color of the rectangle are set to transparent.
     * The rectangle is added as a child to the panel.
     * The panel is added to the grid at the corresponding position.
     * The preferred size of the grid cell is set to ensure proper dimensions.
     * The final row and column are stored for use within the click event.
     * The click event for the cell is defined, executing different actions based on the type of click and the grid it belongs to.
     *
     * @param board It represents the grid (gridPane) where the ships will be stored; there is one grid for the player and another for the machine.
     */
    private void initializeBoard(GridPane board) {

        // Se obtiene el número de filas y columnas de la cuadrícula.
        int numRows = board.getRowConstraints().size();
        int numCols = board.getColumnConstraints().size();

        // Se itera sobre cada celda de la cuadrícula.
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {

                // Se crea un nuevo panel para representar la celda.
                Pane cell = new Pane();

                // Se crea un rectángulo que actuará como el contenido visual de la celda.
                Rectangle rect = new Rectangle(37,33);
                rect.setFill(Color.TRANSPARENT);// Se establece el color de relleno del rectángulo como transparente
                rect.setStroke(Color.TRANSPARENT); // Establece el color del borde del rectangulo como transparente

                // Se agrega el rectángulo como hijo del panel.
                cell.getChildren().add(rect);
                // Se agrega el panel a la cuadrícula en la posición correspondiente.
                board.add(cell, col, row);

                // Se establece el tamaño preferido de la celda de la cuadrícula para que tenga las dimensiones adecuadas.
                board.getColumnConstraints().get(col).setPrefWidth(RECTANGLE_SIZE);
                board.getRowConstraints().get(row).setPrefHeight(RECTANGLE_SIZE);

                // Se guarda la fila y columna finales para su uso dentro del evento de clic.
                int finalRow = row;
                int finalCol = col;

                // Se define el evento de clic para la celda.
                cell.setOnMouseClicked(event -> {

                    // Se verifica si se hizo clic con el botón izquierdo del ratón en el tablero del enemigo y si la celda está vacía y el jugador puede hacer clic.
                    if (event.getButton() == MouseButton.PRIMARY && board==EnemyBoard && (rect.getFill().equals(Color.TRANSPARENT))&&canclick){

                        // Se maneja el clic primario en el tablero del enemigo.
                        handlePrimaryClick(board,rect, finalRow, finalCol, true);

                        //verificar los cuadros con el mismo id
                        String id = rect.getId();
                        System.out.println(id);


                        // Se realiza el turno del enemigo después de que el jugador haya realizado un ataque.
                        enemyTurn();
                    } else if (board == YouBoard && event.getButton() == MouseButton.SECONDARY ) {

                        // Se maneja el clic secundario en el tablero del jugador.
                        handleSecondaryClick(board,finalRow, finalCol);

                        // Se cuenta el total de barcos del jugador colocados en el tablero.
                        totalPlayerShips=countTrueValues(youBoardCells);
                    }else if(!canclick){
                        // Si no es el turno del jugador, se muestra un mensaje indicando que es el turno del enemigo.
                        YouTextField.clear();
                        YouTextField.appendText("Turno del enemigo");
                    }else if(board==YouBoard){
                        // Si se intenta atacar en el tablero del jugador, se muestra un mensaje indicando que no se puede.
                        YouTextField.clear();
                        YouTextField.appendText("No puedes tirar ahi");
                    }else{
                        // Si la celda ya ha sido marcada, se muestra un mensaje indicando que ya está marcada.
                        YouTextField.clear();
                        YouTextField.appendText("Ya marcado");
                    }
                });

            }
        }
    }


    /**
     * A method that, through a left mouse click, does the following:
     * - Verify whether the player is attempting to attack the enemy without placing all their ships.
     * - Check if there is a ship at the selected position for both the player and the enemy.
     * - Handle the attack based on whether it’s the player’s turn or the enemy’s.
     * - If the board belongs to the player, display an invalid zone message if they try to attack their own board.
     * - If there is an enemy ship at the selected position, mark it as found, display an explosion, and update the counter.
     * - Verify if all enemy ships have been found and display a victory message if so.
     * - If it’s the enemy’s turn, handle the attack result based on whether there is an allied ship at the selected position.
     * - Check if all allied ships have been destroyed and display a defeat message if so."
     *
     * @param board Gridpane that represents the board, whether for the enemy or the player.
     * @param rect  Rectangle data, which changes color depending on whether the player or the enemy discovers a ship by clicking on one of the grid cells.
     * @param row   Row Specifies where the rectangle will be placed within the board (Gridpane)
     * @param col   Column Specifies where the rectangle will be placed within the board (Gridpane)
     * @param isPlayer Boolean variable. If its value is True, it’s the player’s turn; if False, it’s the machine’s turn.
     */
    private void handlePrimaryClick(GridPane board, Rectangle rect, int row, int col, boolean isPlayer) {
        try {
            // Verificar si se está intentando atacar al enemigo sin haber colocado todos los barcos del jugador.
            if (board == EnemyBoard && !allShipsPlaced()) {
                YouTextField.clear();
                YouTextField.appendText("Primero coloca tus barcos.\n");
                return; // Salir del método si no se han colocado todos los barcos



            }

            // Verificar si hay un barco en la posición seleccionada para el jugador y el enemigo.
            boolean hasShip = youBoardCells[row][col];
            boolean EnemyHasShip = enemyBoardCells[row][col];

            // Manejar el ataque dependiendo de si es el jugador o el enemigo.
            if (isPlayer) {

                // Si el tablero es del jugador, mostrar un mensaje de zona inválida si intenta atacar su propio tablero.
                if (board == YouBoard) {
                    YouTextField.clear();
                    YouTextField.appendText("Zona invalida");
                } else if (board == EnemyBoard) {
                    YouTextField.clear();

                    // Si hay un barco enemigo en la posición seleccionada, marcarlo como encontrado, mostrar una explosión y actualizar el contador.
                    if (EnemyHasShip) {
                        rect.setFill(Color.RED); // Si hay parte del barco en esta área, ponerla de color rojo
                        YouTextField.appendText("Enemigo Encontrado!");
                        FoundEnemy++;
                        showExplosion(row, col, board);

                        // Verificar si se han encontrado todos los barcos del enemigo y mostrar un mensaje de victoria si es el caso.
                        if(FoundEnemy==totalEnemyShips){
                            new AlertBox().showConfirm("GAME OVER","HAS DESTRUIDO LA FLOTA ENEMIGA","FELICIDADES HAS GANADOS");
                            BoardStage.deleteInstance();
                        }
                    } else {
                        rect.setFill(Color.BLACK); // Si no hay parte del barco en esta área, ponerla de color negro
                        YouTextField.appendText("Fallaste!");
                    }

                }
            } else {
                // Si es el turno del enemigo, manejar el resultado del ataque dependiendo de si hay un barco aliado en la posición seleccionada.
                if (hasShip) {
                    EnemyText.clear();
                    rect.setFill(Color.RED);
                    rect.setStroke(Color.TRANSPARENT);// Si hay parte del barco aliado en esta área, ponerla de color rojo
                    EnemyText.appendText("Aliado dañado\n");
                    FoundAliade++;
                    showExplosion(row, col, board);

                    // Verificar si se han destruido todos los barcos aliados y mostrar un mensaje de derrota si es el caso
                    if(FoundAliade==totalPlayerShips){
                        new AlertBox().showConfirm("GAME OVER","HAN DESTRUIDO LA FLOTA ALIADA...","TU PIERDES...");
                        BoardStage.deleteInstance();
                    }
                } else {
                    EnemyText.clear();
                    rect.setFill(Color.BLACK); // Si no hay parte del barco aliado en esta área, ponerla de color negro
                    EnemyText.appendText("Maquina Falló\n");
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            YouTextField.appendText("Índice fuera de límites: ");
        } catch (Exception e) {
            System.out.println("Error desconocido: " + e.getMessage());
        }
    }


    /**
     * A method that serves to handle the right-click on the board, which performs ship placement by the player. It also:
     * - Verifies whether it’s still possible to place ships of the desired size within the allowed limits.
     * - Attempts to place the ship in various directions until finding a valid one
     * @param Iboard Gridpane that represents the board, whether for the enemy or the player.
     * @param row Row Specifies where the rectangle will be placed within the board (Gridpane)
     * @param col Column Specifies where the rectangle will be placed within the board (Gridpane)
     */
    private void handleSecondaryClick(GridPane Iboard, int row, int col) {
        GridPane Eboard = Iboard;

        // Si no es el tablero del jugador, no hacer nada
        if (Eboard != YouBoard) {
            return; // Si no es el tablero del jugador, no hacer nada
        }

        // Verificar si aún se pueden colocar barcos del tamaño deseado dentro de los límites permitidos
        if (shipsOfSize6 >= MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 >= MAX_SHIPS_OF_SIZE_4 && shipsOfSize2 >= MAX_SHIPS_OF_SIZE_2) {
            System.out.println("Se ha alcanzado el límite de barcos permitidos.");
            return;
        }

        // Crear una instancia de la clase Random para generar valores aleatorios.
        Random random = new Random();

        // Declarar una variable para almacenar el tamaño del barco que se va a colocar.
        int shipSize;

        //Id del rectangulo
        String ID;

        // Declarar una variable booleana para rastrear si se ha colocado un barco en el tablero.
        boolean placed = false;

        // Declarar una variable booleana para determinar la orientación del barco (vertical u horizontal) de manera aleatoria.
        boolean vertical = random.nextBoolean();

        // Elegir aleatoriamente el tamaño del barco
        if (shipsOfSize6 < MAX_SHIPS_OF_SIZE_6) {
            shipSize = 6;
            ID="Ship6";
        } else if (shipsOfSize4 < MAX_SHIPS_OF_SIZE_4) {
            shipSize = 4;
            ID="Ship4";
        } else {
            shipSize = 2;
            ID="Shipe2";
        }

        // Intentar colocar el barco en varias direcciones hasta encontrar una válida
        for (int attempt = 0; attempt < 4; attempt++) {
            if (canPlaceShip(Eboard, row, col, shipSize, vertical, true)) {
                placeShip(row, col, shipSize, vertical, Eboard, true,ID);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, vertical, false)) {
                placeShip(row, col, shipSize, vertical, Eboard, false,ID);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, true)) {
                placeShip(row, col, shipSize, !vertical, Eboard, true,ID);
                placed = true;
                break;
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, false)) {
                placeShip(row, col, shipSize, !vertical, Eboard, false,ID);
                placed = true;
                break;
            }
            vertical = !vertical; // Cambiar la orientación para el siguiente intento
        }

        // Incrementar el contador de barcos del tamaño colocado si se ha colocado correctamente
        if (placed) {
            incrementShipCount(shipSize);
        } else {
            System.out.println("No se puede colocar el barco en esta posición.");
        }
    }

    /**
     * // Method to increment the ship placement counter based on its size.
     * // This method is used to increase the count of ships placed on the player's board according to the size of the placed ship.
     * // Depending on the ship size provided as a parameter (shipSize), the corresponding counter (shipsOfSize6, shipsOfSize4, or shipsOfSize2) is incremented.
     * // This is essential for keeping track of the number of ships of each size that have been placed on the player's board.
     * @param shipSize An integer variable that randomly holds a quantity representing the type of ship. These ships differ based on the number of squares they can occupy on the board
     */
    private void incrementShipCount(int shipSize) {
        // Comprobar el tamaño del barco y aumentar el contador correspondiente.
        if (shipSize == 6) {
            shipsOfSize6++; // Incrementar el contador de barcos de tamaño 6.
        } else if (shipSize == 4) {
            shipsOfSize4++; // Incrementar el contador de barcos de tamaño 4.
        } else {
            shipsOfSize2++; // Incrementar el contador de barcos de tamaño 2.
        }
    }

    /**
     *   Method to check if it's possible to place a ship in the desired position without violating restrictions.
     *   This method evaluates whether a ship can be placed at a specific position on the board, considering the
     *   ship's size, orientation (vertical or horizontal), and the desired direction (positive or negative).
     * @param board The board where the ship placement will be attempted.
     * @param row The row where the ship will be placed.
     * @param col The column where the ship will be placed.
     * @param shipSize The size of the ship being placed.
     * @param vertical Indicates whether the ship will be placed vertically (true) or horizontally (false).
     * @param positiveDirection Indicates whether the ship should be placed in the positive direction (true) or negative direction (false).
     * @return Returns true if it's possible to place the ship in the desired position without violating restrictions,
     * and false otherwise.
     */
    private boolean canPlaceShip(GridPane board, int row, int col, int shipSize, boolean vertical, boolean positiveDirection) {
        int numRows = board.getRowConstraints().size();
        int numCols = board.getColumnConstraints().size();

        // Verificar si el barco se sale de los límites del tablero en la dirección deseada.
        if (vertical) {
            if (positiveDirection) {
                if (row + shipSize - 1 >= numRows)
                    return false; // Si el barco se sale de la grilla hacia abajo, no se puede colocar
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

        // Verificar si hay algún barco en las posiciones que ocupará el nuevo barco.
        /*
        En esta parte del código se utiliza una expresión ternaria para calcular las variables checkRow y checkCol en función de las condiciones de vertical y positiveDirection. La estructura general de una expresión ternaria es (condición) ? valor_si_verdadero : valor_si_falso.

        Para checkRow, se evalúa si vertical es verdadero. Si es verdadero, se verifica si positiveDirection es verdadero. Si lo es, se suma i a row para obtener la fila a verificar; si no lo es, se resta i de row. Si vertical es falso, checkRow simplemente toma el valor de row.
        Para checkCol, si vertical es verdadero, simplemente toma el valor de col. Si es falso, se evalúa positiveDirection. Si es verdadero, se suma i a col para obtener la columna a verificar; si no lo es, se resta i de col.

        En resumen, estas líneas de código calculan las coordenadas de fila y columna a verificar dependiendo de la orientación (vertical) y la dirección (positiveDirection) del barco que se está intentando colocar. Esto se realiza para verificar si las celdas que ocuparía el barco están disponibles en el tablero
        * */
        for (int i = 0; i < shipSize; i++) {
            int checkRow = vertical ? (positiveDirection ? row + i : row - i) : row;
            int checkCol = vertical ? col : (positiveDirection ? col + i : col - i);

            //mensaje de alerta que se arroja por medio de un try catch cuando se posiciona un barco donde hay otro
            if (board == YouBoard) {
                if (youBoardCells[checkRow][checkCol]){
                    usagesMethodCanPlaceShip++; //como el método de canPlaceShip es usado 8 veces cada vez que se coloca un barco
                    // es necesario condicionar el try catch para que solo lance la alerta una vez
                    if (usagesMethodCanPlaceShip==8) {
                        try {
                            validate2(youBoardCells[checkRow][checkCol]); //metodo para validar la excepción
                        } catch (boatPositionException b) {
                            new AlertBox().showConfirm("POSICIÓN NO VALIDA", "NO PUEDES COLOCAR UN BARCO DONDE HAY OTRO", "ESCOGE OTRA POSICIÓN");
                            System.out.println(b.getMessage());
                            usagesMethodCanPlaceShip=0;
                        }
                    }
                    return false; // Si hay un barco en la posición deseada, no se puede colocar
                }
            } else {
                if (enemyBoardCells[checkRow][checkCol]) return false; // Si hay un barco en la posición deseada, no se puede colocar
            }
        }
        return true;// Se puede colocar el barco en la posición deseada sin violar restricciones.
    }



    /**
     * Method for placing the ship in the desired position on the grid.
     * These lines calculate the coordinates of the next position for the ship on the grid,
     * considering whether the ship is placed vertically or horizontally, and the direction it is moving.
     * @param row The row where the ship will be placed.
     * @param col The column where the ship will be placed.
     * @param shipSize The size of the ship being placed.
     * @param vertical Indicates whether the ship will be placed vertically (true) or horizontally (false).
     * @param theboard The board where the ship will be placed.
     * @param positiveDirection Indicates whether the ship should be placed in the positive direction (true) or negative direction (false).
     */
    private void placeShip(int row, int col, int shipSize, boolean vertical, GridPane theboard, boolean positiveDirection, String ID) {
        GridPane Myboard = theboard;

        for (int i = 0; i < shipSize; i++) {

            /*
            * Estas líneas de código se encargan de calcular las coordenadas de la fila y columna donde se va a colocar el siguiente segmento del barco en la grilla.

            La variable shipRow representa la coordenada de la fila.
            La variable shipCol representa la coordenada de la columna.

            El cálculo de estas coordenadas se realiza de la siguiente manera:

            Se utiliza el operador ternario (? :) para determinar si el barco se debe colocar en posición vertical o horizontal, según el valor de la variable vertical. Si vertical es verdadero, significa que el barco se coloca en posición vertical; de lo contrario, se coloca en posición horizontal.
            Si el barco se coloca en posición vertical:
            Si positiveDirection es verdadero, significa que el barco se coloca hacia abajo desde la posición inicial (row + i). Si positiveDirection es falso, significa que el barco se coloca hacia arriba desde la posición inicial (row - i).
            Si el barco se coloca en posición horizontal, shipRow será igual a row, ya que la fila no cambia.
            Si el barco se coloca en posición horizontal:
            Si positiveDirection es verdadero, significa que el barco se coloca hacia la derecha desde la posición inicial (col + i). Si positiveDirection es falso, significa que el barco se coloca hacia la izquierda desde la posición inicial (col - i).
               Si el barco se coloca en posición vertical, shipCol será igual a col, ya que la columna no cambia.

            En resumen, estas líneas calculan las coordenadas de la siguiente posición del barco en la grilla, teniendo en cuenta si el barco se coloca en posición vertical u horizontal, y la dirección en la que se está moviendo.
            * */
            int shipRow = vertical ? (positiveDirection ? row + i : row - i) : row;
            int shipCol = vertical ? col : (positiveDirection ? col + i : col - i);
            boolean booleanFinish = true;
            boolean indicatorEnemyShip = false; //variable que indica cuando los barcos creados son del enemigo


            if (Myboard == YouBoard) {
                youBoardCells[shipRow][shipCol] = true; // Marcar la celda como ocupada por el barco aliado
            }
            if (Myboard == EnemyBoard) {
                indicatorEnemyShip = true; //cambia a true cuando se detecta el barco enemigo
                enemyBoardCells[shipRow][shipCol] = true; // Marcar la celda como ocupada por el barco enemigo
            }
            // cuando se genera el ultimo cuadro del barco que se esta creando, la variable booleana cambia a false
            // para que la clase ship nos genera el triangulo en ese momento en lugar de otro cuadro
            if (i == (shipSize - 1)){
                booleanFinish = false;
            }
            ship newShip = new ship(1, vertical, booleanFinish, indicatorEnemyShip, positiveDirection,ID); // Crear una nueva instancia de barco con tamaño 1x1 y dirección especificada
            Myboard.add(newShip, shipCol, shipRow); // Agregar el barco a la grilla en la posición adecuada
            newShip.toBack();//pone la representacion del barco detras de la grilla para no bloquear los botones
        }
    }


    /**
     * Method for randomly placing enemy ships on the enemy board.
     * Board verification:
     * - Assign the input board (Iboard) to the variable Eboard.
     * - If Eboard is not equal to the enemy board (EnemyBoard), the method terminates without taking any action.
     * Ship limit verification:
     * - Check if it's still possible to place ships of the desired size within the allowed limits.
     * - If the maximum number of ships of a certain size has already been placed, the method ends without taking any action.
     * Random selection of ship size:
     * - Use a random number generator to determine the size of the ship to be placed.
     * - Verify how many ships of each size have already been placed and choose a size that hasn't reached its limit.
     * Ship placement attempts:
     * - Make several attempts (up to four) to place the ship in different directions.
     * - Call the canPlaceShip function to verify if it's possible to place the ship at the current position and orientation.
     * - If a valid position is found, call the placeShip function to place the ship in that position.
     * - If the ship is successfully placed, set the 'placed' variable to true and exit the loop of attempts.
     * Increment the enemy ship count:
     * - If the ship is successfully placed, increment the counter corresponding to the size of the placed ship by calling the incrementEnemyShipCount function.
     * Error message:
     * - If the ship cannot be placed in any valid position, display an error message indicating that it cannot be placed in that position.
     * @param Iboard The board where the ships will be placed.
     * @param row The row where the first segment of the ship will be attempted to be placed.
     * @param col The column where the first segment of the ship will be attempted to be placed.
     */
    private void RandomEnemyShips(GridPane Iboard, int row, int col) {
        // Asigna el tablero de entrada Iboard a la variable Eboard
        GridPane Eboard = Iboard;

        // Verifica si el tablero no es el tablero del enemigo
        if (Eboard != EnemyBoard) {
            return; // Si no es el tablero del enemigo, termina el método
        }

        // Verifica si se pueden colocar más barcos del tamaño deseado dentro de los límites permitidos
        if (EnemyshipsOfSize6 >= MAX_SHIPS_OF_SIZE_6 && EnemyshipsOfSize4 >= MAX_SHIPS_OF_SIZE_4 && EnemyshipsOfSize2 >= MAX_SHIPS_OF_SIZE_2) {
            System.out.println("Se ha alcanzado el límite de barcos permitidos.");
            return; // Si ya se han colocado el máximo número de barcos permitidos, termina el método
        }

        // Genera un objeto Random para selección aleatoria de tamaño de barco y orientación
        Random random = new Random();
        int shipSize;
        String ID;
        boolean placed = false;
        boolean vertical = random.nextBoolean();

        // Elige aleatoriamente el tamaño del barco según los límites establecidos
        if (EnemyshipsOfSize6 < MAX_SHIPS_OF_SIZE_6) {
            shipSize = 6;
            ID="Ship6";
        } else if (EnemyshipsOfSize4 < MAX_SHIPS_OF_SIZE_4) {
            shipSize = 4;
            ID="Ship4";
        } else {
            shipSize = 2;
            ID="Ship2";
        }

        // Intenta colocar el barco en varias direcciones hasta encontrar una posición válida
        for (int attempt = 0; attempt < 4; attempt++) {
            // Comprueba si se puede colocar el barco en la posición actual y orientación
            if (canPlaceShip(Eboard, row, col, shipSize, vertical, true)) {
                // Coloca el barco en la posición actual con orientación vertical
                placeShip(row, col, shipSize, vertical, Eboard, true,ID);
                placed = true; // Establece la bandera de colocación como verdadera
                break; // Sale del bucle de intentos
            } else if (canPlaceShip(Eboard, row, col, shipSize, vertical, false)) {
                // Coloca el barco en la posición actual con orientación vertical invertida
                placeShip(row, col, shipSize, vertical, Eboard, false,ID);
                placed = true; // Establece la bandera de colocación como verdadera
                break; // Sale del bucle de intentos
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, true)) {
                // Coloca el barco en la posición actual con orientación horizontal
                placeShip(row, col, shipSize, !vertical, Eboard, true,ID);
                placed = true; // Establece la bandera de colocación como verdadera
                break; // Sale del bucle de intentos
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, false)) {
                // Coloca el barco en la posición actual con orientación horizontal invertida
                placeShip(row, col, shipSize, !vertical, Eboard, false, ID);
                placed = true; // Establece la bandera de colocación como verdadera
                break; // Sale del bucle de intentos
            }
            vertical = !vertical; // Cambia la orientación para el siguiente intento
        }

        // Incrementa el contador de barcos enemigos si se colocó uno
        if (placed) {
            incrementEnemyShipCount(shipSize);
        } else {
            System.out.println("No se puede colocar el barco en esta posición.");
        }
    }


    /**
     *A method for increasing the enemy ship count based on the size of the placed ship.
     * @param shipSize An integer variable that serves to increment the count for a specific type of ship, indicating that it has already been placed on the board
     */
    private void incrementEnemyShipCount(int shipSize) {
        // Verifica el tamaño del barco y actualiza el contador correspondiente
        if (shipSize == 6) {
            EnemyshipsOfSize6++;// Incrementa el contador de barcos de tamaño 6
        } else if (shipSize == 4) {
            EnemyshipsOfSize4++;// Incrementa el contador de barcos de tamaño 4
        } else {
            EnemyshipsOfSize2++;// Incrementa el contador de barcos de tamaño 2
        }
    }

    /**
     * Method to verify if all types of ships have been placed on YouBoard.
     * @return returns false if the ships have not been placed in their entirety and returns true if all have been placed.” 😊
     */
    private boolean allShipsPlaced() {
        return shipsOfSize6 == MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 == MAX_SHIPS_OF_SIZE_4 && shipsOfSize2 == MAX_SHIPS_OF_SIZE_2;
    }


    /**
     * Method representing the enemy’s turn:
     * - Verifies if all the player’s ships have been placed before starting the enemy’s turn.
     * - Defines actions that will occur after 2 seconds (simulating the enemy’s decision time).
     * - Randomly selects a row and column on the player’s board (if it’s empty, it selects it)
     */
    private void enemyTurn() {
        // Verificar si todos los barcos han sido colocados antes de iniciar el turno del enemigo
        if (!allShipsPlaced()) {
            return;
        }
        canclick=false; // Deshabilitar la capacidad de hacer clic durante el turno del enemigo

        // Crear un Timeline que representa la duración del turno del enemigo
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    // Definir acciones que se realizarán después de 2 segundos (simulando el tiempo de decisión del enemigo)

                    // Generar números aleatorios para seleccionar una celda en el tablero del jugador
                    Random random = new Random();
                    int numRows = YouBoard.getRowConstraints().size();
                    int numCols = YouBoard.getColumnConstraints().size();
                    int row, col;

                    do {

                        // Seleccionar aleatoriamente una fila y una columna en el tablero del jugado
                        row = random.nextInt(numRows);
                        col = random.nextInt(numCols);

                        // Verificar si la celda seleccionada está vacía (color transparente o azul, que indica agua)
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
                            ObservableList<Node> children = ((Pane) node).getChildren();
                            int numChildren = children.size();
                            // Ahora numChildren contiene el número de hijos en el Pane
                            System.out.println("Número de hijos en el Pane: " + numChildren);

                            // Iterar sobre los hijos del Pane
                            for (int i = 0; i < numChildren; i++) {
                                Node child = children.get(i);
                                // Obtener el id del hijo, si tiene
                                String childId = child.getId();
                                // Imprimir el índice y el id del hijo
                                System.out.println("Índice del hijo: " + i + ", ID del hijo: " + childId);
                            }
                            if(numChildren==1){

                                Rectangle rect = (Rectangle) ((Pane) node).getChildren().get(0);
                                // Si la celda está vacía, representa un ataque válido
                                if (rect.getFill().equals(Color.TRANSPARENT)||rect.getFill().equals(Color.BLUE)) {

                                    // Si la celda está vacía, es un ataque válido
                                    handlePrimaryClick(YouBoard, rect, row, col, false);
                                    return;// Salir del bucle después de realizar un ataque válido
                                }
                            }else if(numChildren==2){

                                Rectangle rect = (Rectangle) ((Pane) node).getChildren().get(0);
                                Rectangle rect2 =  (Rectangle) ((Pane) node).getChildren().get(1);
                                // Si la celda está vacía, representa un ataque válido
                                if (rect.getFill().equals(Color.TRANSPARENT)||rect.getFill().equals(Color.BLUE)) {
                                    rect2.setFill(Color.TRANSPARENT);
                                    rect2.setStroke(Color.TRANSPARENT);
                                    // Si la celda está vacía, es un ataque válido
                                    handlePrimaryClick(YouBoard, rect, row, col, false);
                                    return;// Salir del bucle después de realizar un ataque válido
                                }
                            }else{
                                Rectangle rect = (Rectangle) ((Pane) node).getChildren().get(0);
                                Rectangle rect2 =  (Rectangle) ((Pane) node).getChildren().get(1);
                                Polygon polygon = (Polygon) ((Pane) node).getChildren().get(2);
                                // Si la celda está vacía, representa un ataque válido
                                if (rect.getFill().equals(Color.TRANSPARENT)||rect.getFill().equals(Color.BLUE)) {
                                    rect2.setFill(Color.TRANSPARENT);
                                    rect2.setStroke(Color.TRANSPARENT);
                                    polygon.setFill(Color.TRANSPARENT);
                                    polygon.setStroke(Color.TRANSPARENT);
                                    // Si la celda está vacía, es un ataque válido
                                    handlePrimaryClick(YouBoard, rect, row, col, false);
                                    return;// Salir del bucle después de realizar un ataque válido
                                }
                            }
                        }

                        if (node != null && node instanceof Pane) {

                        }
                    } while (true);// Repetir hasta encontrar una celda válida para atacar
                })
        );
        // Acciones que se ejecutarán al finalizar el turno del enemigo
        timeline.setOnFinished(event -> canclick = true);// Habilitar la capacidad de hacer clic después del turno del enemigo
        timeline.play(); // Iniciar el Timeline para simular el turno del enemigo
    }


    /**
     * Method for hiding the ships on the enemy’s board (EnemyBoard).
     */
    private void hideEnemyShips() {
        // Iterar sobre todos los nodos hijos del tablero del enemigo
        for (Node child : EnemyBoard.getChildren()) {
            // Verificar si el nodo hijo es un contenedor Pane (representa una celda en la cuadrícula)
            if (child instanceof Pane) {
                Pane pane = (Pane) child;
                ObservableList<Node> children = pane.getChildren();

                // Obtener el primer rectángulo (representa el primer barco) dentro de la celda
                Rectangle rect1 = (Rectangle) children.get(0);
                rect1.setFill(Color.TRANSPARENT); // Establecer el color de relleno transparente
                rect1.setStroke(Color.TRANSPARENT); // Establecer el color del borde transparente

            }
        }
    }


    /**
     * Method for counting the number of true values in a boolean matrix:
     * @param matrix represents a matrix, whether it’s the player’s or enemy’s board.
     * It has a true value at a specific position if there is a ship placed in that cell, and false if it’s unoccupied.
     * @return Returns the total number of true values found in the matrix.
     */
    private int countTrueValues(boolean[][] matrix) {
        // Inicializar el contador de valores verdaderos
        int count = 0;

        // Iterar sobre las filas de la matriz
        for (int i = 0; i < matrix.length; i++) {
            // Iterar sobre las columnas de cada fila
            for (int j = 0; j < matrix[i].length; j++) {
                // Verificar si el valor en la posición actual de la matriz es verdadero (true)
                if (matrix[i][j]) {
                    // Incrementar el contador si el valor es verdadero
                    count++;
                }
            }
        }

        // Devolver el total de valores verdaderos encontrados en la matriz
        return count;
    }

    /**
     * Method that displays an alert or message when the corresponding button is pressed.
     * @param event Generates the button press event that triggers the message display
     */
    @FXML
    void PressToHelp(ActionEvent event) {
        new AlertBox().showConfirm("INSTRUCCIONES DE JUEGO", "PRIMERO CREA TUS BARCOS EN LA CUADRICULA A LA IZQUIERDA\nSEGUNDO: UNA VEZ CREADOS LOS BARCOS DA CLICK EN UNA DE LAS POSICIONES DE LA CUADRICULA IZQUIERDA PARA ATACAR, !CUIDADO¡ TU ENEMIGO TAMBIEN PODRA ATACAR\nTERCERO: DESTRUYE TODOS LOS BARCOS ENEMIGOS PARA GANAR","!QUE EMPIECE EL JUEGO¡");

    }

    /**
     * Method for loading the background image that will go on each of the boards.
     * @param gridPane Player’s or enemy’s board, where the background images will be placed.
     * @param imagePath Path to the image that will be used as the background.
     */
    private void setBackgroundImage(GridPane gridPane, String imagePath) {
        Image backgroundImage = new Image(imagePath);
        BackgroundSize backgroundSize = new BackgroundSize(
                376, 376, false, false, false, false);
        BackgroundImage backgroundImg = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize
        );
        gridPane.setBackground(new Background(backgroundImg));
    }

    /**
     * It is used to set a background image on a BorderPane.
     * @param borderPane The BorderPane to which you want to apply the background image.
     * @param imagePath The path to the image file.
     */
    private void setBackgroundImagePane(BorderPane borderPane, String imagePath) {
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(imagePath, 1000, 1000, true, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1000, 1000, true, true, true, true));

        Background background = new Background(backgroundImage);
        borderPane.setBackground(background);
    }

    /**
     * Method that displays a GIF animation on the grid when a ship area is uncovered, which doesn’t last long.
     * @param row Row where the ship has been discovered
     * @param col Column where the ship has been discovered
     * @param gridPane board where the explosion effect will occur
     */
    private void showExplosion(int row, int col, GridPane gridPane) {
        ImageView exploImageView = new ImageView(explosionImageView);
        exploImageView.setFitWidth(RECTANGLE_SIZE); // Ajusta el ancho de la imagen
        exploImageView.setFitHeight(RECTANGLE_SIZE); // Ajusta el alto de la imagen
        gridPane.getChildren().add(exploImageView);
        GridPane.setColumnIndex(exploImageView, col);
        GridPane.setRowIndex(exploImageView, row);
        exploImageView.toFront();

        // Define una Timeline para eliminar la imagen después de un breve período de tiempo
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> gridPane.getChildren().remove(exploImageView))
        );
        timeline.play(); // Inicia la línea de tiempo
    }


    /**
     * A static method that serves to validate whether the entered boolean incurs an exception, which will occur if it is positive.
     * @param bool A boolean whose value will be positive if it is detected that a ship is being placed in a position where another one has already been placed.
     * @throws boatPositionException Custom exception, which indicates that you cannot place a ship in the same cell
     */
    static void validate2 (boolean bool) throws boatPositionException {
        if (bool){
            throw new boatPositionException("No puedes digitar en la misma casilla");
        }
    }

}