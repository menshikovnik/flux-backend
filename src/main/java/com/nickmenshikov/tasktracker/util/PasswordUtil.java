package com.nickmenshikov.tasktracker.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    public static String hashPassword(String password) {
        int logRounds = 12;

        String salt = BCrypt.gensalt(logRounds);

        return BCrypt.hashpw(password, salt);
    }

    public static boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
