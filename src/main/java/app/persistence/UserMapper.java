package app.persistence;

import app.entities.User;

public class UserMapper {

    public static User login(String email, String password)
    {
        System.out.println("TODO: login(): check database");
        return new User(1, email, 0.0, false);
    }

    public static boolean register(String email, String password)
    {
        System.out.println("TODO: register");
        return false;
    }
}
