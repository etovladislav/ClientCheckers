package ru.kpfu;

/**
 * Created by etovladislav on 29.05.16.
 */
public enum  PieceType {
    RED(1), WHITE(-1);

    final int moveDir;

    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }

}
