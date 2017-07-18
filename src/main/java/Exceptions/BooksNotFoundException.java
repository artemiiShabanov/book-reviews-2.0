package Exceptions;

public class BooksNotFoundException extends Exception {
    public BooksNotFoundException(){
        super();
    }
    public BooksNotFoundException(String msg){
        super(msg);
    }
}
