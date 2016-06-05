package ru.kpfu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import ru.kpfu.util.Request;
import ru.kpfu.util.RequestError;

/**
 * Created by etovladislav on 30.05.16.
 */
public class MainMenu {

    public StackPane getMainMenu() {
        MainApp.window.setTitle("Game!");
        Button btn = new Button();
        btn.setText("New game'");
        Image image = new Image("http://preloaders.net/preloaders/712/%D0%9F%D0%BB%D1%8B%D0%B2%D1%83%D1%89%D0%B8%D0%B5%20%D0%BB%D1%83%D1%87%D0%B8.gif");
        ImageView imageView = new ImageView(image);
        imageView.setVisible(false);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                imageView.setVisible(true);
                btn.setVisible(false);
                Request request = new Request();
                try {
                    request.get("api/game/new?token="+MainApp.USER_TOKEN);
                } catch (RequestError requestError) {
                    requestError.printStackTrace();
                }
            }
        });

        StackPane root = new StackPane();
        root.setStyle("-fx-background: #FFFFFF;");
        root.getChildren().add(imageView);
        root.getChildren().add(btn);
        return root;
    }
}
