package com.example.navalbattle.Model;

//clase de excepciones que hereda de la clase padre (RuntimeException) para crear excepciones propias
public class boatPositionException extends RuntimeException {
    public boatPositionException(){}

    //firma de metodo que espera un mensaje
    public boatPositionException(String message){
        super(message);
    }
    //espera un mensaje y una causa
    public boatPositionException(String message, Throwable cause){
        super(message, cause);
    }
    // espera una causa
    public boatPositionException(Throwable cause){
        super(cause);
    }
}
