package com.offcn.user.except;

public class UserExisException extends RuntimeException{
    public UserExisException(){
        super("用户账户存在");
    }
}
