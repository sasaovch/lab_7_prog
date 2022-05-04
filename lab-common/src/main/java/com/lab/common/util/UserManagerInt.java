package com.lab.common.util;


import com.lab.common.data.User;

public interface UserManagerInt {
    boolean checkIn(User user);
    User register(User user);
    ResultStatusWorkWithColl authenticate(User user);
}
