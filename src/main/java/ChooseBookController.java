import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.Book;

public class ChooseBookController {

    //items
    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;

    private Stage dialogStage;
    private Book selectedBook = null;

    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(
                cellData -> cellData.getValue().titleProperty());

        authorColumn.setCellValueFactory(
                cellData -> cellData.getValue().authorProperty());
    }

    /**
     * Stage setter.
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Setting book list.
     * @param bookList
     */
    public void setBooks(ObservableList<Book> bookList) {
        bookTable.setItems(bookList);
    }

    /**Getter for selected book.
    *
    * @return
    */
    public Book getSelectedBook() {
        return selectedBook;
    }

    /**
     * Button Select clicked.
     */
    @FXML
    private void handleSelect() {
        selectedBook = bookTable.getSelectionModel().getSelectedItem();
        dialogStage.close();
    }

    /**
     * Button Cancel clicked.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }




}
