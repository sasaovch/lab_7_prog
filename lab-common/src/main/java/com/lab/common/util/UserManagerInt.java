package com.lab.common.util;


import com.lab.common.data.User;

public interface UserManagerInt {
    boolean checkIn(User client);
    User authenticate(User client);
    boolean login(User client);
}
