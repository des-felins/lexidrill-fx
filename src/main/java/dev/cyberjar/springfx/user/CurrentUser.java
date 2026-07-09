package dev.cyberjar.springfx.user;

import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    private User user;

    public void set(User user) {
        this.user = user;
    }

    public User getRequired() {
        if (user == null) {
            throw new IllegalStateException("No user is currently signed in");
        }
        return user;
    }

    public Long getRequiredUserId() {
        return getRequired().getId();
    }

    public void clear() {
        this.user = null;
    }

}
