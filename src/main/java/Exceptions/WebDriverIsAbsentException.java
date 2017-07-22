package Exceptions;

public class WebDriverIsAbsentException extends Exception {
    public WebDriverIsAbsentException(){
        super();
    }
    public WebDriverIsAbsentException(String msg){
        super(msg);
    }
}