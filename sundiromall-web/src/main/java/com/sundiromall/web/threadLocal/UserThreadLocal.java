package com.sundiromall.web.threadLocal;


import com.sundiromall.web.bean.User;

public class UserThreadLocal {

    private static final ThreadLocal<User> LOCAL = new ThreadLocal<>();
    
    public static void set(User user) {
        LOCAL.set(user);
    }
    
    public static User get() {
        return LOCAL.get();
    }
}
