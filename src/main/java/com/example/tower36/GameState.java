package com.example.tower36;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

class GameState {
    final List<Tower36.Move> moves;

    public static final GameState emptyGameState = new GameState(Collections.emptyList());

    public boolean checkWin() {
        if(availablePieces().size() != 0) { return false; }

        boolean rowsPassed = true;
        boolean columnsPassed = true;

        for(int row = 0; row < 6; row++) {
            rowsPassed = Stream.of(getTile(0,row),getTile(1,row),getTile(2,row),getTile(3,row),getTile(4,row),getTile(5,row)).distinct().count() < 6;
            if(!rowsPassed) { break; }
        }

        for(int column = 0; column < 6; column++) {
            columnsPassed = Stream.of(getTile(column,0),getTile(column,1),getTile(column,2),getTile(column,3),getTile(column,4),getTile(column,5)).distinct().count() < 6;
            if(!columnsPassed) { break; }
        }
        return rowsPassed && columnsPassed;
    }

    public Optional<Tower36.Tower> getTile(int x, int y) {
        return getMoveForTile(new Tower36.Coord(x,y)).stream().map(Tower36.Move::t).findFirst();
    }

    public Optional<Tower36.Move> getMoveForTile(Tower36.Coord c) {
        List<Tower36.Move> revList = new ArrayList<>(moves);
        Collections.reverse(revList);
        return revList.stream().filter(m -> m.x() == c.x() && m.y() == c.y()).findFirst();
    }

    public List<Tower36.Tower> availablePieces() {
        List<Tower36.Tower> used = Tower36.Coord.getAllCoords().stream().map(c -> getTile(c.x(), c.y())).flatMap(Optional::stream).toList();
        List<Tower36.Tower> all = new ArrayList<>(Tower36.createPiecePool());
        all.removeAll(used);
        return all;
    }

    public Optional<Tower36.Tower> isPieceAvailable(int height, Color c) {
        return availablePieces().stream().filter(t -> t.c() == c && t.height() == height).findFirst();
    }

    public GameState submitMove(int x,int y,Color c) {

        ArrayList<Tower36.Move> modMoves = new ArrayList<>(moves);

        //cleanup Might create error?
        modMoves.stream().filter(msi -> msi.x() == x && msi.y() == y).findFirst().ifPresent(modMoves::remove);

        isPieceAvailable(Tower36.board[y][x],c).ifPresent(tower -> {
            modMoves.add(new Tower36.Move(x,y,tower));
        });

        //Legal Move?

        return new GameState(modMoves);
    }

    private GameState(List<Tower36.Move> moves) {
        this.moves = Collections.unmodifiableList(moves);
    }

    public GameState clearTile(int x,int y) {
        ArrayList<Tower36.Move> moves = new ArrayList<>(this.moves);
        for(int i=moves.size()-1; i >= 0; i--) {
            if(moves.get(i).x() == x && moves.get(i).y() == y) {
                moves.remove(i);
            }
        }
        return new GameState(moves);
    }

    //This is here because solver needs it
    public Stream<Tower36.Coord> getAvailableSlots(int height) {
        return Tower36.Coord.getAllCoords().stream().filter(c -> !getMoveForTile(c).isPresent() && Tower36.board[c.y()][c.x()] == height);
    }

}
