package com.ecommerce.auth.exception;

import com.ecommerce.auth.entity.User;

public class UserAlreadyExistException extends RuntimeException{

    public UserAlreadyExistException(String message){
        super(message);
    }
}
