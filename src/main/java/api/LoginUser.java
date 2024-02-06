package api;

public class LoginUser {

    public String email;
    public String password;

    public LoginUser (String email, String password){
        this.email = email;
        this.password = password;
    }

    public LoginUser() {
    }

    public static LoginUser from (User user) {
        return new LoginUser(user.getEmail(), user.getPassword());
    }


}