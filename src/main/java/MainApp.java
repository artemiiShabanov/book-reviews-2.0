import Exceptions.DriverWasClosedException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import Exceptions.BookNotSelectedException;
import Exceptions.BooksNotFoundException;
import Exceptions.ReviewsNotFoundException;
import model.Book;
import model.Review;
import services.WebDriverService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class MainApp extends Application {

    private BorderPane rootLayout;
    private Stage primaryStage;
    public Stage getPrimaryStage() { return primaryStage; }
    /**
     * Book reviews - list(observable).
     */
    private ObservableList<Review> reviewData = FXCollections.observableArrayList();
    /**
     * Getter for review list.
     * @return
     */
    public ObservableList<Review> getReviewData() {
        return reviewData;
    }

    /**
     * Constructor.
     */
    public MainApp() {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("BOOK REVIEWS");

        primaryStage.getIcons().add(new Image(("images/icon.png")));

        initRootLayout();

        showOverview();
    }

    @Override
    public void stop() throws Exception {
        WebDriverService.finish();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Giving opportunity to choose the book.
     * @param books - books to choose.
     * @return selected book.
     */
    public Book showChooseBookDialog(ObservableList<Book> books) {
        try {
            // Loading fxml.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ChooseBookDialog.fxml"));
            BorderPane page = loader.load();

            // Dialog window Stage.
            Stage dialogStage = new Stage();
            dialogStage.getIcons().add(new Image(("images/choice.png")));
            dialogStage.setTitle("Choose book");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ChooseBookController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setBooks(books);

            dialogStage.showAndWait();

            return controller.getSelectedBook();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Initializes the root layout.
     */
    private void initRootLayout() {
        try {
            // Loading from fxml.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Display the scene containing the root layout.
            primaryStage.setScene(new Scene(rootLayout));
            primaryStage.show();
            primaryStage.setResizable(false);

            // Giving the controller access to the main application.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows information about the reviews in the root layout.
     */
    private void showOverview() {
        try {
            // Loading from fxml.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Overview.fxml"));
            BorderPane personOverview = loader.load();

            rootLayout.setCenter(personOverview);

            // Giving the controller access to the main application.
            OverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


