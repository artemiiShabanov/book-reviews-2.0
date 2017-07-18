import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class RootLayoutController {
    @FXML
    private void aboutHandler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About this application");
        alert.setHeaderText("It is the system for collecting book reviews\n\n" +
                "Here you can find opinions of people from all over the world for any book you want(almost any)");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("resources/images/info.png"));
        alert.showAndWait();
    }

    @FXML
    private void printTxtHendler() {

    }
// TODO: new features

}
