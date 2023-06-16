package az.spring.utils;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
