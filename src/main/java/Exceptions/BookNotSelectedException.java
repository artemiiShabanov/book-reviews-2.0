package Exceptions;


public class BookNotSelectedException extends Exception {
    public BookNotSelectedException(){
        super();
    }
    public BookNotSelectedException(String msg){
        super(msg);
    }
}
