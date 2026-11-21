
package gui.controller.auth;

import com.hotel.manager.UserManager;
import com.hotel.model.User;

public class LoginController {

    private final UserManager userManager = new UserManager();

    public User login(String email, String password) {

        if (email == null || password == null
                || email.isBlank() || password.isBlank()) {
            return null;
        }

        for (User user : userManager.findAllUsers()) {

            if (user.getEmail() != null
                    && user.getEmail().equalsIgnoreCase(email.trim())) {

                if (user.login(email, password)) {
                    return user;
                }

                return null;
            }
        }

        return null;
    }
}
