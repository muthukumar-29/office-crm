package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.User;
import dev.muthukumar.ai_crm.repository.UserRepository;
import dev.muthukumar.ai_crm.service.UserService;
import dev.muthukumar.ai_crm.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public static String generateUserId() {
        long time = System.currentTimeMillis() % 100000000;
        String timePart = Long.toString(time, 36);
        int random = ThreadLocalRandom.current().nextInt(36 * 36);
        String randomPart = Integer.toString(random, 36);
        return "AI-EMP-" + timePart + randomPart;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        user.setUserId(generateUserId());
        String email = user.getEmail();
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        if (userRepository.findByEmail(email).isPresent()) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        }

        userService.create(user);
        return new ResponseEntity<>("Registered Successfully", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (userRepository.findByEmail(email).isEmpty()) {
            return new ResponseEntity<>("User not found!", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByEmail(email).get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new ResponseEntity<>("Invalid credentials!", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(email);

        // ✅ Fixed: return full user info so frontend can store role/name/userId
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                        "token",  token,
                        "userId", user.getUserId(),
                        "name",   user.getName(),
                        "email",  user.getEmail(),
                        "role",   user.getRole() != null ? user.getRole().name() : "EMPLOYEE"
                )
        ));
    }
}