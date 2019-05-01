package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;

public class Controller_dialog {

    @FXML PasswordField password;
    @FXML CheckBox spyeye;
    @FXML Button OkButton;
    @FXML Button CancelButton;

    private Controller main;

    @FXML
    public void initialize(){
        main = Main.ctrl;

        CancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Button)event.getTarget()).getScene().getWindow().hide();
                main.key="";
                password.setText("");
                password.setPromptText("");
                main.todo="nothing";
            }
        });
        OkButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ((Button)event.getTarget()).getScene().getWindow().hide();
                main.key=password.getText();
                password.setText("");
                password.setPromptText("");
                if (main.todo.compareTo("change")==0) main.ChangeFile();
                if (main.todo.compareTo("unchange")==0) main.UnChangeFile();
                main.todo="nothing";
            }
        });
        password.requestFocus();
    }
    @FXML
    private void setPasswordVisible()
    {
        if (spyeye.isSelected())
        {
            password.setPromptText(password.getText());
            password.setText("");
        }
        else password.setPromptText("");
    }

}
