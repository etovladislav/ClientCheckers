package ru.kpfu;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import ru.kpfu.util.MoveDto;
import ru.kpfu.util.Request;
import ru.kpfu.util.RequestError;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.kpfu.MainApp.USER_TOKEN;
import static ru.kpfu.MainApp.getSceneMainMenu;
import static ru.kpfu.MainApp.window;
import static ru.kpfu.MainMenu.GAME_ID;

/**
 * Created by etovladislav on 30.05.16.
 */
public class Game {

    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    public Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
            Request request = new Request();
            try {
                String result = request.get("api/game/waitMove?token=" + USER_TOKEN + "&gameId=" + GAME_ID);
                if (!result.equals("")) {
                    ObjectMapper mapper = new ObjectMapper();

                    MoveDto obj = mapper.readValue(result, MoveDto.class);
//                    if (obj.getGameOver().equals("true")) {
//                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                        alert.setTitle("End");
//                        alert.setHeaderText(null);
//                        alert.setContentText("You win");
//                        alert.show();
//                        fiveSecondsWonder.stop();
//                        pieceGroup.getChildren().removeAll();
//                        tileGroup.getChildren().removeAll();
//                        MainApp.window.setScene(getSceneMainMenu());
//                    } else {
//
//                    }
                    makeMove(obj);
                    fiveSecondsWonder.stop();
                }
            } catch (RequestError requestError) {
                requestError.printStackTrace();
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }));

    private PieceType yourPice;
    private Boolean isYourMove = false;

    public void setYourPice(PieceType yourPice) {
        this.yourPice = yourPice;
    }

    public void setIsYourMove(Boolean isYourMove) {
        this.isYourMove = isYourMove;
    }

    private Tile[][] board = new Tile[WIDTH][HEIGHT];

    private Group tileGroup = new Group();
    private Group pieceGroup = new Group();

    public Parent getGame() {
        Pane root = new Pane();
        root.setPrefSize(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
        root.getChildren().addAll(tileGroup, pieceGroup);

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0, x, y);
                board[x][y] = tile;

                tileGroup.getChildren().add(tile);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.RED, x, y);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = makePiece(PieceType.WHITE, x, y);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    pieceGroup.getChildren().add(piece);
                }
            }
        }
        Button button = new Button("Сдаться");
        button.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Look, a Confirmation Dialog");
            alert.setContentText("Are you ok with this?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Request r = new Request();
                try {
                    r.get("api/game/end?gameId=" + GAME_ID + "&loose=" + USER_TOKEN);
                } catch (RequestError requestError) {
                    requestError.printStackTrace();
                }
                fiveSecondsWonder.stop();
                pieceGroup.getChildren().removeAll();
                tileGroup.getChildren().removeAll();
                MainApp.window.setScene(getSceneMainMenu());
            } else {
                alert.close();
            }
        });
        root.getChildren().add(button);
        return root;
    }

    private MoveResult tryMove(Piece piece, int newX, int newY) {
        if (piece.getType() != yourPice || !isYourMove) {
            return new MoveResult(MoveType.NONE);
        }
        if (board[newX][newY].hasPiece() || (newX + newY) % 2 == 0) {
            return new MoveResult(MoveType.NONE);
        }

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());

        Request request = new Request();
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("oldX", String.valueOf(x0)));
        list.add(new BasicNameValuePair("oldY", String.valueOf(y0)));
        list.add(new BasicNameValuePair("newX", String.valueOf(newX)));
        list.add(new BasicNameValuePair("newY", String.valueOf(newY)));
        list.add(new BasicNameValuePair("token", USER_TOKEN));
        list.add(new BasicNameValuePair("gameId", GAME_ID));
        if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
            try {
                request.post("api/game/move", list);
            } catch (RequestError requestError) {
                return new MoveResult(MoveType.NONE);
            }
            return new MoveResult(MoveType.NORMAL);
        } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                try {
                    request.post("api/game/move", list);
                } catch (RequestError requestError) {
                    return new MoveResult(MoveType.NONE);
                }

                return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
            }
        }

        return new MoveResult(MoveType.NONE);
    }

    private int toBoard(double pixel) {
        return (int) (pixel + TILE_SIZE / 2) / TILE_SIZE;
    }


    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);
        piece.setOnMouseReleased(e -> {
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            MoveResult result = tryMove(piece, newX, newY);

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (result.getType()) {
                case NONE:
                    piece.abortMove();
                    break;
                case NORMAL:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    isYourMove = false;
                    fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
                    fiveSecondsWonder.play();
                    break;
                case KILL:
                    isYourMove = false;
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    Piece otherPiece = result.getPiece();
                    board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                    pieceGroup.getChildren().remove(otherPiece);
                    fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
                    fiveSecondsWonder.play();
                    break;
            }
            checkWinner();
        });

        return piece;
    }

    public void makeMove(MoveDto moveDto) {
        Piece piece = board[moveDto.getOldX()][moveDto.getOldY()].getPiece();

        int newX = moveDto.getNewX();
        int newY = moveDto.getNewY();

        MoveResult result = tryMove2(piece, newX, newY, moveDto.getOldX(), moveDto.getOldY());

        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());

        switch (result.getType()) {
            case NONE:
                piece.abortMove();
                break;
            case NORMAL:
                isYourMove = true;
                piece.move(newX, newY);
                board[x0][y0].setPiece(null);
                board[newX][newY].setPiece(piece);
                break;
            case KILL:
                isYourMove = true;
                piece.move(newX, newY);
                board[x0][y0].setPiece(null);
                board[newX][newY].setPiece(piece);
                Piece otherPiece = result.getPiece();
                board[toBoard(otherPiece.getOldX())][toBoard(otherPiece.getOldY())].setPiece(null);
                pieceGroup.getChildren().remove(otherPiece);
                break;
        }
        checkWinner();
    }

    private MoveResult tryMove2(Piece piece, int newX, int newY, int oldX, int oldY) {
        int x0 = oldX;
        int y0 = oldY;

        try {
            if (Math.abs(newX - x0) == 1 && newY - y0 == piece.getType().moveDir) {
                return new MoveResult(MoveType.NORMAL);
            } else if (Math.abs(newX - x0) == 2 && newY - y0 == piece.getType().moveDir * 2) {

                int x1 = x0 + (newX - x0) / 2;
                int y1 = y0 + (newY - y0) / 2;

                if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                    return new MoveResult(MoveType.KILL, board[x1][y1].getPiece());
                }
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
        return new MoveResult(MoveType.NONE);
    }


    public void waitMove() {
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();
    }

    private int count = 0;

    private void checkWinner() {
        Boolean red = false;
        Boolean white = false;
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (board[i][j].getPiece() == null) {
                    continue;
                }
                if (board[i][j].getPiece().getType() == PieceType.WHITE) {
                    white = true;
                }
                if (board[i][j].getPiece().getType() == PieceType.RED) {
                    red = true;
                }
            }
            if (red && white) {
                break;
            }
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("End");
        alert.setHeaderText(null);
        if (red == true && white == false || white == true && red == false) {
            alert.setContentText(red ? "RED WIN!" : "WHITE WIN!");
            alert.show();
            Request r = new Request();
            try {
                if (PieceType.RED == yourPice && red) {
                    r.get("api/game/end?gameId=" + GAME_ID + "&win=" + USER_TOKEN);
                } else if (PieceType.WHITE == yourPice && white) {
                    r.get("api/game/end?gameId=" + GAME_ID + "&win=" + USER_TOKEN);
                }
            } catch (RequestError requestError) {
                requestError.printStackTrace();
            }
            fiveSecondsWonder.stop();
            pieceGroup.getChildren().removeAll();
            tileGroup.getChildren().removeAll();
            MainApp.window.setScene(getSceneMainMenu());
        }
    }
}
