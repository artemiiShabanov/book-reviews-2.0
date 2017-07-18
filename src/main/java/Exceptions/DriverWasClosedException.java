package Exceptions;



public class DriverWasClosedException extends Exception {
    public DriverWasClosedException(){
        super();
    }
    public DriverWasClosedException(String msg){
        super(msg);
    }
}
