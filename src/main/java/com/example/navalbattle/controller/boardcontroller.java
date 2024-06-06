package com.example.navalbattle.controller;

import com.example.navalbattle.Model.ship;
import com.example.navalbattle.View.Alert.AlertBox;
import com.example.navalbattle.View.BoardStage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Random;

public class boardcontroller {

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
    private final int MAX_SHIPS_OF_SIZE_4 = 1; // Máximo permitido de barcos de tamaño 4
    private final int MAX_SHIPS_OF_SIZE_2 = 2; // Máximo permitido de barcos de tamaño 2

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


    public void initialize() {
        // Ruta del GIF de explosión
        String EXPLOSION_GIF_PATH = "file:/C:/Users/DARIO/Downloads/NavalBattle/src/main/resources/images/explosion.gif";
        //inicializador de la imagen de explosion
        explosionImageView = new Image(EXPLOSION_GIF_PATH);

        // carga imagen de fondo en YouBoard
        setBackgroundImage(YouBoard, "file:/C:/Users/DARIO/Downloads/NavalBattle/src/main/resources/images/mar.png");
        // carga imagen de fondo en YouBoard
        setBackgroundImage(EnemyBoard, "file:/C:/Users/DARIO/Downloads/NavalBattle/src/main/resources/images/mar.png");
        // carga imagen de fondo de fono que es la del piso metalico en border pane
        setBackgroundImagePane(GeneralPane, "file:/C:/Users/DARIO/Downloads/NavalBattle/src/main/resources/images/fondo.jpg");


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


    /*Este método se encarga de inicializar la cuadrícula del juego, creando las celdas y configurando los eventos de clic en cada celda. Aquí está la explicación detallada:

    Se recibe como parámetro la cuadrícula (GridPane) a inicializar.
    Se obtienen el número de filas (numRows) y columnas (numCols) de la cuadrícula.
    Se itera sobre cada fila y columna para crear una celda en la cuadrícula.
    Para cada celda, se crea un nuevo panel (Pane) que actúa como contenedor visual.
    Se crea un rectángulo (Rectangle) que representa el contenido visual de la celda, con un tamaño específico.
    Se establece el color de relleno y el color del borde del rectángulo como transparente.
    El rectángulo se agrega como hijo del panel.
    El panel se agrega a la cuadrícula en la posición correspondiente.
    Se establece el tamaño preferido de la celda de la cuadrícula para asegurarse de que tenga las dimensiones adecuadas.
    Se guarda la fila y columna finales para su uso dentro del evento de clic.
    Se define el evento de clic para la celda, que ejecuta diferentes acciones según el tipo de clic y la cuadrícula en la que se encuentre.*/
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


    // Método para manejar el clic primario en un tablero (ataque del jugador).
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


    // Método para manejar el clic secundario en un tablero (colocación de barcos por el jugador).
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

        // Declarar una variable booleana para rastrear si se ha colocado un barco en el tablero.
        boolean placed = false;

        // Declarar una variable booleana para determinar la orientación del barco (vertical u horizontal) de manera aleatoria.
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

        // Incrementar el contador de barcos del tamaño colocado si se ha colocado correctamente
        if (placed) {
            incrementShipCount(shipSize);
        } else {
            System.out.println("No se puede colocar el barco en esta posición.");
        }
    }

    // Método para incrementar el contador de barcos colocados según su tamaño.
    /*Este método se utiliza para aumentar el contador de barcos colocados en el tablero del jugador según el tamaño del barco que se haya colocado. Dependiendo del tamaño del barco proporcionado como parámetro (shipSize), se incrementa el contador correspondiente (shipsOfSize6, shipsOfSize4, o shipsOfSize2). Esto es esencial para realizar un seguimiento de la cantidad de barcos de cada tamaño que han sido colocados en el tablero del jugado*/
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



    // Método para verificar si es posible colocar un barco en la posición deseada sin violar restricciones.
    /*Este método evalúa si se puede colocar un barco en una posición específica del tablero, teniendo en cuenta el tamaño del barco,
    su orientación (vertical u horizontal) y la dirección en la que se desea colocar (positiva o negativa).
    Parámetros:
    - board: el tablero en el que se intentará colocar el barco.
    - row: la fila en la que se intentará colocar el barco.
    - col: la columna en la que se intentará colocar el barco.
    - shipSize: el tamaño del barco que se intentará colocar.
    - vertical: indica si el barco se colocará verticalmente (true) u horizontalmente (false).
    - positiveDirection: indica si se desea colocar el barco en dirección positiva (true) o negativa (false).
    Devuelve true si es posible colocar el barco en la posición deseada sin violar restricciones,
    y false en caso contrario.*/
    private boolean canPlaceShip(GridPane board, int row, int col, int shipSize, boolean vertical, boolean positiveDirection) {
        int numRows = board.getRowConstraints().size();
        int numCols = board.getColumnConstraints().size();

        // Verificar si el barco se sale de los límites del tablero en la dirección deseada.
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

            if (board == YouBoard) {
                if (youBoardCells[checkRow][checkCol]) return false; // Si hay un barco en la posición deseada, no se puede colocar
            } else {
                if (enemyBoardCells[checkRow][checkCol]) return false; // Si hay un barco en la posición deseada, no se puede colocar
            }
        }
        return true;// Se puede colocar el barco en la posición deseada sin violar restricciones.
    }



    // Método para colocar el barco en la posición deseada en la grilla
    private void placeShip(int row, int col, int shipSize, boolean vertical, GridPane theboard, boolean positiveDirection) {
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

            if (Myboard == YouBoard) {
                youBoardCells[shipRow][shipCol] = true; // Marcar la celda como ocupada por el barco aliado
            }
            if (Myboard == EnemyBoard) {
                enemyBoardCells[shipRow][shipCol] = true; // Marcar la celda como ocupada por el barco enemigo
            }
            ship newShip = new ship(1); // Crear una nueva instancia de barco con tamaño 1x1 y dirección especificada
            Myboard.add(newShip, shipCol, shipRow); // Agregar el barco a la grilla en la posición adecuada
            newShip.toBack();//pone la representacion del barco detras de la grilla para no bloquear los botones
        }
    }


    // Método para colocar los barcos del enemigo de manera aleatoria en el tablero enemigo
    /*
        * Este método, RandomEnemyShips, se encarga de colocar los barcos del enemigo de manera aleatoria en el tablero enemigo.

        Parámetros de entrada:
            Iboard: El tablero en el que se van a colocar los barcos.
            row, col: Las coordenadas de fila y columna donde se intentará colocar el primer segmento del barco.

        Verificación del tablero:
            Se asigna el tablero de entrada Iboard a la variable Eboard.
            Si Eboard no es igual al tablero del enemigo (EnemyBoard), el método termina y no se realiza ninguna acción.

        Verificación de límites de barcos:
            Se verifica si aún se pueden colocar barcos del tamaño deseado dentro de los límites permitidos. Si ya se han colocado el máximo número de barcos de cierto tamaño, el método termina sin realizar ninguna acción.

        Selección aleatoria del tamaño del barco:
            Se utiliza un generador de números aleatorios para determinar el tamaño del barco que se va a colocar.
            Se verifica cuántos barcos de cada tamaño ya se han colocado y se elige un tamaño que no haya alcanzado su límite.

        Intentos de colocación del barco:
            Se realizan varios intentos (hasta cuatro) para colocar el barco en diferentes direcciones.
            Se llama a la función canPlaceShip para verificar si es posible colocar el barco en la posición actual y orientación.
            Si se encuentra una posición válida, se llama a la función placeShip para colocar el barco en esa posición.
            Si se logra colocar el barco, se establece la variable placed como verdadera y se termina el bucle de intentos.

        Incremento del contador de barcos enemigos:
            Si se logra colocar el barco correctamente, se incrementa el contador correspondiente al tamaño del barco colocado llamando a la función incrementEnemyShipCount.

        Mensaje de error:
            Si no se puede colocar el barco en ninguna posición válida, se muestra un mensaje de error indicando que no se puede colocar en esa posición.*/
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
        boolean placed = false;
        boolean vertical = random.nextBoolean();

        // Elige aleatoriamente el tamaño del barco según los límites establecidos
        if (EnemyshipsOfSize6 < MAX_SHIPS_OF_SIZE_6) {
            shipSize = 6;
        } else if (EnemyshipsOfSize4 < MAX_SHIPS_OF_SIZE_4) {
            shipSize = 4;
        } else {
            shipSize = 2;
        }

        // Intenta colocar el barco en varias direcciones hasta encontrar una posición válida
        for (int attempt = 0; attempt < 4; attempt++) {
            // Comprueba si se puede colocar el barco en la posición actual y orientación
            if (canPlaceShip(Eboard, row, col, shipSize, vertical, true)) {
                // Coloca el barco en la posición actual con orientación vertical
                placeShip(row, col, shipSize, vertical, Eboard, true);
                placed = true; // Establece la bandera de colocación como verdadera
                break; // Sale del bucle de intentos
            } else if (canPlaceShip(Eboard, row, col, shipSize, vertical, false)) {
                // Coloca el barco en la posición actual con orientación vertical invertida
                placeShip(row, col, shipSize, vertical, Eboard, false);
                placed = true; // Establece la bandera de colocación como verdadera
                break; // Sale del bucle de intentos
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, true)) {
                // Coloca el barco en la posición actual con orientación horizontal
                placeShip(row, col, shipSize, !vertical, Eboard, true);
                placed = true; // Establece la bandera de colocación como verdadera
                break; // Sale del bucle de intentos
            } else if (canPlaceShip(Eboard, row, col, shipSize, !vertical, false)) {
                // Coloca el barco en la posición actual con orientación horizontal invertida
                placeShip(row, col, shipSize, !vertical, Eboard, false);
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


    // Método para incrementar el contador de barcos enemigos según el tamaño del barco colocado
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

    // Método para verificar si todos los barcos han sido colocados en YouBoard
    private boolean allShipsPlaced() {
        return shipsOfSize6 == MAX_SHIPS_OF_SIZE_6 && shipsOfSize4 == MAX_SHIPS_OF_SIZE_4 && shipsOfSize2 == MAX_SHIPS_OF_SIZE_2;
    }


    // Método que representa el turno del enemigo
    private void enemyTurn() {
        // Verificar si todos los barcos han sido colocados antes de iniciar el turno del enemigo
        if (!allShipsPlaced()) {
            return;
        }
        canclick=false; // Deshabilitar la capacidad de hacer clic durante el turno del enemigo

        // Crear un Timeline que representa la duración del turno del enemigo
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> {
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
                            Rectangle rect = (Rectangle) ((Pane) node).getChildren().get(0);
                            // Si la celda está vacía, representa un ataque válido
                            if (rect.getFill().equals(Color.TRANSPARENT)||rect.getFill().equals(Color.BLUE)) {
                                // Si la celda está vacía, es un ataque válido
                                handlePrimaryClick(YouBoard, rect, row, col, false);
                                return;// Salir del bucle después de realizar un ataque válido
                            }
                        }
                    } while (true);// Repetir hasta encontrar una celda válida para atacar
                })
        );
        // Acciones que se ejecutarán al finalizar el turno del enemigo
        timeline.setOnFinished(event -> canclick = true);// Habilitar la capacidad de hacer clic después del turno del enemigo
        timeline.play(); // Iniciar el Timeline para simular el turno del enemigo
    }



    // Método para ocultar los barcos en el tablero del enemigo (EnemyBoard)
    private void hideEnemyShips() {
        // Iterar sobre todos los nodos hijos del tablero del enemigo
        for (Node child : EnemyBoard.getChildren()) {
            // Verificar si el nodo hijo es un contenedor Pane (representa una celda en la cuadrícula)
            if (child instanceof Pane) {
                // Obtener el rectángulo (representa el barco) dentro de la celda y establecer su color de relleno y borde como transparentes
                Rectangle rect = (Rectangle) ((Pane) child).getChildren().get(0);
                rect.setFill(Color.TRANSPARENT); // Establecer el color de relleno transparente
                rect.setStroke(Color.TRANSPARENT); // Establecer el color del borde transparente
            }
        }
    }


    // Método para contar el número de valores verdaderos (true) en una matriz booleana
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

    @FXML
    void PressToHelp(ActionEvent event) {
        new AlertBox().showConfirm("INSTRUCCIONES DE JUEGO", "PRIMERO CREA TUS BARCOS EN LA CUADRICULA A LA IZQUIERDA\nSEGUNDO: UNA VEZ CREADOS LOS BARCOS DA CLICK EN UNA DE LAS POSICIONES DE LA CUADRICULA IZQUIERDA PARA ATACAR, !CUIDADO¡ TU ENEMIGO TAMBIEN PODRA ATACAR\nTERCERO: DESTRUYE TODOS LOS BARCOS ENEMIGOS PARA GANAR","!QUE EMPIECE EL JUEGO¡");

    }



    // Método para establecer una imagen de fondo en un GridPane
    private void setBackgroundImage(GridPane gridPane, String imagePath) {
        // Cargar la imagen desde la ruta proporcionada
        Image backgroundImage = new Image(imagePath);

        // Crear un objeto BackgroundSize para definir el tamaño de la imagen de fondo
        BackgroundSize backgroundSize = new BackgroundSize(
                376, 376, false, false, false, false);

        // Crear un objeto BackgroundImage con la imagen cargada y configuraciones adicionales
        BackgroundImage backgroundImg = new BackgroundImage(
                backgroundImage,                 // La imagen a usar como fondo
                BackgroundRepeat.NO_REPEAT,      // No repetir la imagen en horizontal
                BackgroundRepeat.NO_REPEAT,      // No repetir la imagen en vertical
                BackgroundPosition.CENTER,       // Centrar la imagen en el GridPane
                backgroundSize                   // Usar el tamaño de imagen especificado
        );

        // Establecer la imagen de fondo en el GridPane
        gridPane.setBackground(new Background(backgroundImg));
    }





    // Método para establecer una imagen de fondo en un BorderPane
    private void setBackgroundImagePane(BorderPane borderPane, String imagePath) {
        // Crear un objeto BackgroundImage con la imagen cargada y configuraciones adicionales
        BackgroundImage backgroundImage = new BackgroundImage(
                new Image(imagePath, 1000, 1000, true, true), // Cargar la imagen con el tamaño 1000x1000, manteniendo las proporciones
                BackgroundRepeat.NO_REPEAT, // No repetir la imagen en el fondo
                BackgroundRepeat.NO_REPEAT, // No repetir la imagen en el fondo
                BackgroundPosition.CENTER, // Colocar la imagen en el centro del BorderPane
                new BackgroundSize(1000, 1000, true, true, true, true) // Configurar el tamaño del fondo para ajustar al BorderPane
        );

        // Crear un objeto Background con el BackgroundImage creado
        Background background = new Background(backgroundImage);

        // Establecer el fondo del BorderPane con el Background creado
        borderPane.setBackground(background);
    }






    // Método para mostrar una explosión en la posición dada en la grilla
    private void showExplosion(int row, int col, GridPane gridPane) {
        // Crear un ImageView para la imagen de explosión
        ImageView exploImageView = new ImageView(explosionImageView);
        exploImageView.setFitWidth(RECTANGLE_SIZE); // Ajustar el ancho de la imagen al tamaño de la celda
        exploImageView.setFitHeight(RECTANGLE_SIZE); // Ajustar el alto de la imagen al tamaño de la celda

        // Agregar el ImageView a la grilla
        gridPane.getChildren().add(exploImageView);

        // Establecer la columna y fila de la imagen de explosión en la grilla
        GridPane.setColumnIndex(exploImageView, col);
        GridPane.setRowIndex(exploImageView, row);

        // Definir una Timeline para eliminar la imagen después de un breve período de tiempo
        Timeline timeline = new Timeline(
                // Crear un KeyFrame que se ejecutará después de 0.5 segundos
                new KeyFrame(Duration.seconds(0.5), event -> gridPane.getChildren().remove(exploImageView))
        );

        // Iniciar la Timeline
        timeline.play();
    }



}


