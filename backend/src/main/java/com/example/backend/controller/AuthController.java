package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.entity.User;
import com.example.backend.repo.UserRepository;
import com.example.backend.security.JwtUtil;
import com.example.backend.util.PasswordUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AuthController {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;

    public AuthController(UserRepository userRepo, JwtUtil jwtUtil, PasswordUtil passwordUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordUtil = passwordUtil;
    }

    @PostMapping("/register")
    public String register(@RequestBody LoginRequest req){
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        
        User user = new User(req.getEmail(), passwordUtil.hashPassword(req.getPassword()), "USER", 1);
        userRepo.save(user);
        return "User Registered";
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req){
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordUtil.checkPassword(req.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getPrivilegeLevel());
        return new LoginResponse(token, user.getEmail(), user.getRole(), user.getPrivilegeLevel());
    }

    // Temporary utility endpoint to generate password hashes
    // SECURITY: Uncomment only when needed to generate password hashes, then comment back
//    @GetMapping("/hash")
//    public String generateHash(@RequestParam String password, HttpServletRequest request) {
//        String remoteAddr = request.getRemoteAddr();
//        // Check for IPv4 and IPv6 localhost
//        if (!"127.0.0.1".equals(remoteAddr) && !"0:0:0:0:0:0:0:1".equals(remoteAddr)) {
//            throw new RuntimeException("This endpoint is only accessible from localhost");
//        }
//        return passwordUtil.hashPassword(password);
//    }

}
