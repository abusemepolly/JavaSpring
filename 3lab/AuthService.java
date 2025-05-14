package service;

/** Простая аутентификация по жёстко заданным логин/пароль */
public class AuthService {
    public static String login(String login, String password) {
        if ("admin".equals(login) && "admin".equals(password)) {
            return "admin";
        }
        if ("user".equals(login) && "user".equals(password)) {
            return "user";
        }
        return null;
    }
}
