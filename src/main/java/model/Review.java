package model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Class-model Review.
 */
public class Review {

    private final StringProperty text;
    private final StringProperty author;
    private final StringProperty resource;
    private final ObjectProperty<LocalDate> date;

    /**
     * Default constructor.
     */
    public Review() {
        this(null, null, null, null);
    }

    /**
     * Constructor.
     * @param text Text of the review
     * @param author Author
     * @param resource Resource
     * @param date Date of the review
     */
    public Review(String text, String author, String resource, LocalDate date) {
        this.text = new SimpleStringProperty(text);
        this.author = new SimpleStringProperty(author);
        this.resource = new SimpleStringProperty(resource);
        this.date = new SimpleObjectProperty<>(date);
    }

    @Override
    public String toString() {
        return author + "\n\n" + text + "\n\n" + date.toString() + "\t" + resource + "\n\n";
    }

    //Getters and setters.

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public StringProperty textProperty() {
        return text;
    }


    public String getResource() {
        return resource.get();
    }

    public void setResource(String resource) {
        this.resource.set(resource);
    }

    public StringProperty resourceProperty() {
        return resource;
    }


    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public StringProperty authorProperty() {
        return author;
    }


    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
}