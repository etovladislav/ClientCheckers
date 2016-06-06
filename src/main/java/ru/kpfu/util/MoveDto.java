package ru.kpfu.util;

/**
 * Created by etovladislav on 06.06.16.
 */
public class MoveDto {

    Integer oldX;
    Integer oldY;

    Integer newX;
    Integer newY;

    String gameOver;

    public MoveDto(Integer oldX, Integer oldY, Integer newX, Integer newY) {
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
    }

    public MoveDto() {
    }

    public String getGameOver() {
        return gameOver;
    }

    public void setGameOver(String gameOver) {
        this.gameOver = gameOver;
    }

    public Integer getOldX() {
        return oldX;
    }

    public void setOldX(Integer oldX) {
        this.oldX = oldX;
    }

    public Integer getOldY() {
        return oldY;
    }

    public void setOldY(Integer oldY) {
        this.oldY = oldY;
    }

    public Integer getNewX() {
        return newX;
    }

    public void setNewX(Integer newX) {
        this.newX = newX;
    }

    public Integer getNewY() {
        return newY;
    }

    public void setNewY(Integer newY) {
        this.newY = newY;
    }

    @Override
    public String toString() {
        return "MoveDto{" +
                "oldX=" + oldX +
                ", oldY=" + oldY +
                ", newX=" + newX +
                ", newY=" + newY +
                '}';
    }
}
