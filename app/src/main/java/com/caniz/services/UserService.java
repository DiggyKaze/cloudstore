package com.caniz.services;

import com.caniz.models.AppUser;
import com.caniz.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Map<String, AppUser> users = new ConcurrentHashMap<>();

    public UserService(PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public void register(String username, String email, String password) {
        if (users.containsKey(username)) {
            throw new RuntimeException("Username already exists");
        }
        String hashedPassword = passwordEncoder.encode(password);
        users.put(username, new AppUser(username, email, hashedPassword));
    }

    // Returnerar JWT-token om login lyckas, annars null
    public String login(String username, String password) {
        AppUser user = users.get(username);
        if (user == null) return null;
        if (!passwordEncoder.matches(password, user.getPassword())) return null;
        return jwtUtil.generateToken(username);
    }

    public AppUser findByUsername(String username) {
        return users.get(username);
    }
}
