package Exceptions;

public class ReviewsNotFoundException extends Exception {
    public ReviewsNotFoundException(){
        super();
    }
    public ReviewsNotFoundException(String msg){
        super(msg);
    }
}
