import Exceptions.DriverWasClosedException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import Exceptions.BookNotSelectedException;
import Exceptions.BooksNotFoundException;
import Exceptions.ReviewsNotFoundException;
import model.Book;
import model.Review;
import services.DateService;
import services.WebDriverService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OverviewController {

    @FXML
    private BorderPane bp;

    //Table items.
    @FXML
    private TableView<Review> reviewTable;
    @FXML
    private TableColumn<Review, String> textColumn;
    @FXML
    private TableColumn<Review, LocalDate> dateColumn;
    @FXML
    private TableColumn<Review, String> resourceColumn;

    //Chosen review items.
    @FXML
    private Label chosenDateLabel;
    @FXML
    private Label chosenSourceLabel;
    @FXML
    private Label chosenAuthorLabel;
    @FXML
    private TextArea chosenText;
    @FXML
    private HBox stars;

    //Search items.
    @FXML
    private Label bookName;
    @FXML
    private TextField nameField;
    @FXML
    private TextField authorField;
    @FXML
    private Button btnFind;

    // Main app ref.
    private MainApp mainApp;
    /**
     * Running by main app.
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Connecting data to review table
        reviewTable.setItems(mainApp.getReviewData());
    }

    /**
     * Initialization.
     * Running after fxml loading.
     */
    @FXML
    private void initialize() {
        textColumn.setCellValueFactory(
                cellData -> cellData.getValue().textProperty());

        dateColumn.setCellValueFactory(
                cellData -> cellData.getValue().dateProperty());

        resourceColumn.setCellValueFactory(
                cellData -> cellData.getValue().resourceProperty());

        reviewTable.setFixedCellSize(25.0);

        // Cleaning extra review info
        showReviewDetails(null);

        // Adding listener to row choosing.
        reviewTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showReviewDetails(newValue));

        nameField.textProperty().addListener((observable, oldValue, newValue) -> disable());
        authorField.textProperty().addListener((observable, oldValue, newValue) -> disable());
    }

    /**
     *Disable find button if it is necessary.
     * @return
     */
    private void disable() {
        btnFind.setDisable(nameField.getText().isEmpty() && authorField.getText().isEmpty());
    }

    /**
     * Filling fields from review parameter.
     * Review = null - cleaning all fields.
     *
     * @param review — review or null
     */
    private void showReviewDetails(Review review) {
        if (review != null) {
            // Filling.
            chosenAuthorLabel.setText(review.getAuthor());
            chosenSourceLabel.setText(review.getResource());
            chosenText.setVisible(true);
            chosenText.setText(review.getText());
            chosenDateLabel.setText(DateService.format(review.getDate()));
            stars.getChildren().clear();
            for (int i = 0; i < review.getMark(); i++) {
                ImageView img = new ImageView("images/star.png");
                img.setFitHeight(20);
                img.setFitWidth(20);
                stars.getChildren().add(img);
            }
            for (int i = review.getMark(); i < 10; i++) {
                ImageView img = new ImageView("images/empty-star.png");
                img.setFitHeight(20);
                img.setFitWidth(20);
                stars.getChildren().add(img);
            }
        } else {
            // Cleaning.
            chosenDateLabel.setText("");
            chosenSourceLabel.setText("");
            chosenAuthorLabel.setText("");
            chosenText.setVisible(false);
            stars.getChildren().clear();
        }
    }

    /**
    * Searching button pressed.
    */
    @FXML
    private void handleSearch() {
        try{
            bookName.setText(search(nameField.getText(), authorField.getText()));
        }
        catch (ReviewsNotFoundException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ooops");
            alert.setHeaderText("There are no reviews for this book");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("images/alert.png"));

            alert.showAndWait();
        }
        catch(BookNotSelectedException e) {
            //NOP
        }
        catch(BooksNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ooops");
            alert.setHeaderText("There are no such books");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("images/alert.png"));

            alert.showAndWait();
        } catch (DriverWasClosedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ooops");
            alert.setHeaderText("Some problems with a web. Try again.");
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("images/alert.png"));

            alert.showAndWait();
        }

    }

    /**
     * Main search method.
     * @param title
     * @param author
     * @return book title.
     * @throws ReviewsNotFoundException if there are no such books in the Internet.
     * @throws BookNotSelectedException if user did not select any book.
     */
    private String search(String title, String author) throws ReviewsNotFoundException, BookNotSelectedException, BooksNotFoundException, DriverWasClosedException {

        HashSet<Book> bookSet;
        Book selectedBook;
        ArrayList<Review> newReviews;
        List<Review> reviewData = mainApp.getReviewData();

        bookSet = WebDriverService.findBooksGoogle(title, author);

        switch(bookSet.size())
        {
            case 0:
                throw new BooksNotFoundException("Set is empty");
            case 1:
                selectedBook = bookSet.iterator().next();
                reviewData.clear();
                newReviews = WebDriverService.loadReviewsOzon(selectedBook);
                newReviews.addAll(WebDriverService.loadReviewsLabirint(selectedBook));
                if (newReviews.size() == 0) {
                    throw new ReviewsNotFoundException();
                }
                reviewData.addAll(newReviews);
                break;
            default:
                selectedBook = mainApp.showChooseBookDialog(FXCollections.observableArrayList(bookSet));
                if (selectedBook == null) throw new BookNotSelectedException("Book was not selected.");
                reviewData.clear();
                newReviews = WebDriverService.loadReviewsOzon(selectedBook);
                newReviews.addAll(WebDriverService.loadReviewsLabirint(selectedBook));
                if (newReviews.size() == 0) {
                    throw new ReviewsNotFoundException();
                }
                reviewData.addAll(newReviews);
                break;
        }

        return selectedBook.getTitle();
    }


}