package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static Controller ctrl;
    public static Stage dialog;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader MainLoader = new FXMLLoader();
        MainLoader.setLocation(getClass().getResource("sample.fxml"));
        Parent root = MainLoader.load();
        ctrl = MainLoader.getController();
        primaryStage.setTitle("Drag&Drop&Crypt");
        primaryStage.setScene(new Scene(root, 281, 281));
        primaryStage.show();

        dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        Parent root_dialog = FXMLLoader.load(getClass().getResource("dialog.fxml"));
        Scene dialog_scene = new Scene(root_dialog,348,95);
        dialog.setTitle("Параметры");
        dialog.setScene(dialog_scene);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setResizable(false);
        //dialog.showAndWait();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
