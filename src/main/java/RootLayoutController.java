import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Review;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class RootLayoutController {

    // Main app ref.
    private MainApp mainApp;
    /**
     * Running by main app.
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    @FXML
    private void aboutHandler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About this application");
        alert.setHeaderText("It is the system for collecting book reviews\n\n" +
                "Here you can find opinions of people from all over the world for any book you want(almost any)");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("images/info.png"));
        alert.showAndWait();
    }

    @FXML
    private void printTxtHendler() {
        //Getting data
        List<Review> list= mainApp.getReviewData();
        if (list.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ooops");
            alert.setHeaderText("Review list is empty.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("images/alert.png"));

            alert.showAndWait();
            return;
        }

        //Asking for file name
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        //Saving
        try(FileWriter writer = new FileWriter(file, false))
        {
            for(Review r: list) {
                writer.write(r.toString() + "\r\n");
            }
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

}
