package com.example.tower36;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tower36 extends Application {
    public record Tower(int height,Color c) {}
    public record Move(int x,int y,Tower t) {}
    public record Coord (int x,int y){
        public static List<Coord> getAllCoords() {
            ArrayList<Coord> all = new ArrayList<>();
            for(int xa=0; xa < 6; xa++) {
                for(int ya=0; ya < 6; ya++) {
                    all.add(new Coord(xa,ya));
                }
            }
            return all;
        }
    }

    SimpleObjectProperty<GameState> gameState = new SimpleObjectProperty<>(GameState.emptyGameState);

    ListView<GameState> lstNextStates = new ListView<>();


    //List of game piece colors
    public static final Color[] colors  = {
        Color.RED,Color.GREEN,Color.BLUE,Color.ORANGE,Color.PURPLE,Color.YELLOW
    };

    //Initial board layout stolen from observing game board
    // is multidimensional array to mirror real world board, value is height of piece that belongs there
    public static final int[][] board  = {
        { 1,2,5,4,6,3},
        { 5,3,6,1,4,2},
        { 4,6,3,5,2,1},
        { 2,1,4,3,5,6},
        { 3,5,2,6,1,4},
        { 6,4,1,2,3,5}
    };

    record BoardCell(Coord c,int height) {}

    private static ArrayList<BoardCell> cells = new ArrayList<>();
    {
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                cells.add(new BoardCell(new Coord(x, y), board[y][x]));
            }
        }
    }

    public static Stream<BoardCell> boardStream() {
        return cells.stream();
    }

    public void groupFinder(int height,List<Coord> used) {
        List<BoardCell> availableMoves = boardStream().filter(bc -> bc.height==height && !used.contains(used)).collect(Collectors.toList());


    }

    public static List<Tower> createPiecePool() {
        List<Tower> pieces = new ArrayList<>();

        for(int color=0; color < 6; color++) {
            for (int size = 1; size < 7; size++) {
                pieces.add(new Tower(size,colors[color]));
            }
        }
        return pieces;
    }

    @Override
    public void start(Stage stage) throws IOException {
        UIGameView mainView = new UIGameView();
        ComboBox<Color> cboColorSelect = new ComboBox<>();
        Button btnClear = new Button("RESET");
        Button btnSolve = new Button("Solve");
        VBox vb = new VBox();
        HBox hb = new HBox();

        mainView.setOnTileLeftClicked( (fx,fy) -> {
            System.out.printf("Mouse Clicked: %d,%d\n", fx, fy);
            if (cboColorSelect.getValue() != null) {
                gameState.set(gameState.get().submitMove(fx, fy, cboColorSelect.getValue()));
            } else {
                System.err.println("No Color Selected");
            }
        });

        mainView.setOnTileRightClicked( (fx,fy) -> gameState.set(gameState.get().clearTile(fx,fy))  );

        mainView.stateProperty().bind(gameState);

        btnClear.setOnAction(eh -> {
            gameState.set(GameState.emptyGameState);
        });

        btnSolve.setOnAction(eh -> solve(null));

        cboColorSelect.getItems().addAll(colors);
        cboColorSelect.setCellFactory((list) ->  new ListCell<Color>(){
            {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override
            protected void updateItem(Color o, boolean b) {
                super.updateItem(o, b);
                if(b || o == null) { setText(null); setGraphic(null);  return; }
                Rectangle r = new Rectangle();
                r.setWidth(16);
                r.setHeight(16);
                r.setFill(o);
                setGraphic(r);
                setText(null);
            }
        }  );

        lstNextStates.setCellFactory(v ->
            new ListCell<GameState>() {
                UIGameView graphic = new UIGameView();{
                    graphic.setCellSize(16);
                }
                @Override
                protected void updateItem(GameState gameState, boolean b) {
                    super.updateItem(gameState, b);
                    if(gameState == null || b) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        graphic.setState(gameState);
                        setGraphic(graphic);
                        setText(null);
                    }
                }
        });

        vb.getChildren().add(cboColorSelect);
        vb.getChildren().add(btnClear);
        vb.getChildren().add(btnSolve);
        vb.getChildren().add(lstNextStates);

        hb.getChildren().addAll(mainView,vb);
        Scene scene = new Scene(hb);

        stage.setTitle("Towers 36");
        stage.setScene(scene);
        stage.show();
    }

    public void solve(MoveTreeItem parent) {
        if(parent == null) {
            GameState defaultState = GameState.emptyGameState;

            Stream<GameState> nextStates = defaultState.getAvailableSlots(1).map(c -> defaultState.submitMove(c.x(),c.y(),colors[0]) );
            lstNextStates.getItems().setAll(nextStates.toList());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}