package com.example.tower36;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UIGameView extends GridPane {
    private final Label[][] guiLabels = new Label[6][6];
    private final SimpleDoubleProperty cellSize = new SimpleDoubleProperty(32);

    private final SimpleObjectProperty<BiConsumer<Integer,Integer>> onTileLeftClicked = new SimpleObjectProperty<>( (x,y) -> {} );
    private final SimpleObjectProperty<BiConsumer<Integer,Integer>> onTileRightClicked = new SimpleObjectProperty<>( (x,y) -> {} );
    private Map<Color, Background> bg = Arrays.asList(Tower36.colors[0],Tower36.colors[1],Tower36.colors[2],Tower36.colors[3],Tower36.colors[4],Tower36.colors[5]).stream().collect(Collectors.toMap(Function.identity(),c -> new Background(new BackgroundFill(c,null,null))));
    private final Background cleared = new Background(new BackgroundFill(Color.LIGHTGRAY,null,null));

    private final SimpleObjectProperty<GameState> state = new SimpleObjectProperty<>(GameState.emptyGameState);

    public BiConsumer<Integer, Integer> getOnTileLeftClicked() {
        return onTileLeftClicked.get();
    }

    public SimpleObjectProperty<BiConsumer<Integer, Integer>> onTileLeftClickedProperty() {
        return onTileLeftClicked;
    }

    public void setOnTileLeftClicked(BiConsumer<Integer, Integer> onTileLeftClicked) {
        this.onTileLeftClicked.set(onTileLeftClicked);
    }

    public BiConsumer<Integer, Integer> getOnTileRightClicked() {
        return onTileRightClicked.get();
    }

    public SimpleObjectProperty<BiConsumer<Integer, Integer>> onTileRightClickedProperty() {
        return onTileRightClicked;
    }

    public void setOnTileRightClicked(BiConsumer<Integer, Integer> onTileRightClicked) {
        this.onTileRightClicked.set(onTileRightClicked);
    }

    public UIGameView() {
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 6; x++) {
                Label l = new Label();
                l.prefWidthProperty().bind(cellSize);
                l.prefHeightProperty().bind(cellSize);
                l.setAlignment(Pos.CENTER);
                l.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
                l.setText("" + Tower36.board[y][x]);
                guiLabels[y][x] = l;
                final int fx = x, fy = y;
                l.setOnMouseClicked(eh -> {
                    if (eh.getButton() == MouseButton.PRIMARY) {
                        onTileLeftClicked.get().accept(fx,fy);
                    } else {
                        onTileRightClicked.get().accept(fx,fy);
                    }
                });
                this.add(l, x, y);
            }
        }

        state.addListener((ob,ov,nv) -> {
            System.out.println("Updating Game State Render");
            Tower36.Coord.getAllCoords().stream().forEach(c -> {
                Optional<Tower36.Move> om = nv.getMoveForTile(c);
                if(!om.isPresent()) {
                    guiLabels[c.y()][c.x()].setBackground(cleared);
                } else {
                    om.ifPresent(m -> guiLabels[m.y()][m.x()].setBackground(bg.get(m.t().c())) );
                }
            });
        });

    }

    public double getCellSize() {
        return cellSize.get();
    }

    public SimpleDoubleProperty cellSizeProperty() {
        return cellSize;
    }

    public void setCellSize(double cellSize) {
        this.cellSize.set(cellSize);
    }

    public GameState getState() {
        return state.get();
    }

    public SimpleObjectProperty<GameState> stateProperty() {
        return state;
    }

    public void setState(GameState state) {
        this.state.set(state);
    }
}
