package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import services.StringService;


/**
 * Class-model Book.
 */
public class Book{
    private StringProperty title;
    private StringProperty author;

    /**
     * Constructor.
     * @param title
     * @param author
     */
    public Book(String title, String author) {
        this.title = new SimpleStringProperty(title);
        this.author = new SimpleStringProperty(author);
    }

    //Careful methods.
    @Override
    public boolean equals(Object obj) {
        Book that = (Book)obj;
        return StringService.equalStrings(this.getTitle(), that.getTitle()) && StringService.equalAuthors(this.getAuthor(), that.getAuthor());
    }
    @Override
    public int hashCode() {
        return StringService.smartHash(title.get()) + 1 * StringService.stupidHash(author.get());
    }

    //Getters and setters.

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String name) {
        this.title.set(name);
    }

    public StringProperty titleProperty() { return title;}

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public StringProperty authorProperty() { return author;}


}
