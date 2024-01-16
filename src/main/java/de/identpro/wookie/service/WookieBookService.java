package de.identpro.wookie.service;

import de.identpro.wookie.model.User;
import de.identpro.wookie.util.TokenGenerator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class WookieBookService {
    private static final Logger LOG = LoggerFactory.getLogger(WookieBookService.class);

    @Inject
    TokenGenerator tokenGenerator;

    public User findUser(final String username) {
        return User.findByName(username);
    }

    public boolean exists(final String username) {
        return User.findByName(username) != null;
    }

    public boolean isValidPassword(final User user, final String pwd) {
        return user.password.equals(pwd);
    }

    public String getUserToken(final User user) {
        String jwt = null;

        try {
            jwt = this.tokenGenerator.generateUserJWT(user);
            LOG.info("Generated JSON Web Token {}", jwt);
        } catch (Exception e) {
            LOG.error("JWT Error {}", e.getMessage());
        }
        return jwt;
    }

}
