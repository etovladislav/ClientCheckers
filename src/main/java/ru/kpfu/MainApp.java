package ru.kpfu;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ru.kpfu.xml.TokenXml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;

public class MainApp extends Application {
    public static final int TILE_SIZE = 100;


    private static Scene sceneLogin, sceneRegistration, sceneGame;

    public static Stage window;

    public static Scene getSceneGame() {
        return sceneGame;
    }

    public static Scene getSceneLogin() {
        return sceneLogin;
    }

    public static Scene getSceneRegistration() {
        return sceneRegistration;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        sceneRegistration = new Scene(new Registration().getRegistration());
        sceneGame = new Scene(new Game().getGame());
        sceneLogin = new Scene(new Login().getLogin());
        sceneLogin.getStylesheets().add("/styles/login.css");
        sceneRegistration.getStylesheets().add("/styles/login.css");
        primaryStage.setScene(sceneLogin);
        primaryStage.show();
    }

    public static TokenXml loadPersonDataFromFile() {
        File file = new File("token.xml");
        try {
            JAXBContext context = JAXBContext
                    .newInstance(TokenXml.class);
            Unmarshaller um = context.createUnmarshaller();
            TokenXml wrapper = (TokenXml) um.unmarshal(file);
            return wrapper;
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
        return null;
    }

    public static void savePersonDataToFile(String token) {
        File file = new File("token.xml");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            JAXBContext context = JAXBContext
                    .newInstance(TokenXml.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            TokenXml wrapper = new TokenXml();
            wrapper.setToken(token);
            m.marshal(wrapper, file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
}
