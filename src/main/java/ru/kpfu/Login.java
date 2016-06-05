package ru.kpfu;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.chainsaw.Main;
import ru.kpfu.util.Request;
import ru.kpfu.util.RequestError;
import ru.kpfu.xml.TokenXml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.kpfu.MainApp.getSceneGame;
import static ru.kpfu.MainApp.window;

/**
 * Created by etovladislav on 30.05.16.
 */
public class Login {
    String user = "admin";
    String pw = "1234";
    String checkUser, checkPw;

    public BorderPane getLogin() {

        MainApp.window.setTitle("Login");
        MainApp.window.setResizable(false);

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10, 50, 50, 50));

        HBox hb = new HBox();
        hb.setPadding(new Insets(20, 20, 20, 30));

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        Label lblUserName = new Label("Username");
        final TextField txtUserName = new TextField();
        Label lblPassword = new Label("Password");
        final PasswordField pf = new PasswordField();
        Button btnLogin = new Button("Login");
        Button btnRegistration = new Button("Registration");

        final Label lblMessage = new Label();

        gridPane.add(lblUserName, 0, 0);
        gridPane.add(txtUserName, 1, 0);
        gridPane.add(lblPassword, 0, 1);
        gridPane.add(pf, 1, 1);
        gridPane.add(btnLogin, 2, 1);
        gridPane.add(btnRegistration, 2, 2);
        gridPane.add(lblMessage, 1, 2);


        Reflection r = new Reflection();
        r.setFraction(0.7f);
        gridPane.setEffect(r);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);

        Text text = new Text("CHECKERS");
        text.setFont(Font.font("Courier New", FontWeight.BOLD, 38));
        text.setEffect(dropShadow);
        hb.getChildren().add(text);

        bp.setId("bp");
        gridPane.setId("root");
        btnLogin.setId("btnLogin");
        btnRegistration.setId("btnLogin");
        text.setId("text");

        btnLogin.setOnAction(event -> {
                String username = txtUserName.getText().toString();
                String password = pf.getText().toString();
                List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
                urlParameters.add(new BasicNameValuePair("login", username));
                urlParameters.add(new BasicNameValuePair("password", password));
                Request request = new Request();
                try {
                    String result = request.post("api/login", urlParameters);
                    TokenXml token = new TokenXml();
                    token.setToken(result);
                    MainApp.savePersonDataToFile(result);
                    MainApp.USER_TOKEN = result;
                    MainApp.window.setScene(MainApp.getSceneMainMenu());
                } catch (RequestError requestError) {
                    System.err.println(requestError.getMessage());
                    lblMessage.setText("Error");
                    lblMessage.setTextFill(Color.RED);
                }
                txtUserName.setText("");
                pf.setText("");
        });
        btnRegistration.setOnAction(event -> {
            window.setScene(MainApp.getSceneRegistration());
        });
        //Add HBox and GridPane layout to BorderPane Layout
        bp.setTop(hb);
        bp.setCenter(gridPane);
        return bp;
    }
}
