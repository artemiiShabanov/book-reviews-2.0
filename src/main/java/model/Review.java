package model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Class-model Review.
 */
public class Review {

    private final StringProperty text;
    private final StringProperty author;
    private final StringProperty resource;
    private final ObjectProperty<LocalDate> date;
    private final IntegerProperty mark;

    /**
     * Default constructor.
     * @param mark
     */
    public Review(IntegerProperty mark) {
        this(null, null, null, null, 0);
    }

    /**
     * Constructor.
     * @param text Text of the review
     * @param author Author
     * @param resource Resource
     * @param date Date of the review
     * @param mark
     */
    public Review(String text, String author, String resource, LocalDate date, int mark) {
        this.text = new SimpleStringProperty(text);
        this.author = new SimpleStringProperty(author);
        this.resource = new SimpleStringProperty(resource);
        this.date = new SimpleObjectProperty<>(date);
        this.mark = new SimpleIntegerProperty(mark);
    }

    @Override
    public String toString() {
        return author.getValue() + "\r\nоценка:" + mark.getValue().toString() + "\r\n" + text.getValue() + "\r\n" + date.toString() + "\t" + resource.getValue() + "\r\n";
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


    public int getMark() {
        return mark.get();
    }

    public IntegerProperty markProperty() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark.set(mark);
    }
}