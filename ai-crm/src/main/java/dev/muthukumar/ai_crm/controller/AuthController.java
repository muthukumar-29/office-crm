package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.User;
import dev.muthukumar.ai_crm.repository.UserRepository;
import dev.muthukumar.ai_crm.service.UserService;
import dev.muthukumar.ai_crm.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> body){
//
//        String email = body.get("email");
//        String password = passwordEncoder.encode(body.get("password"));
//
//        if(userRepository.findByEmail(email).isPresent()){
//            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
//        }
//
//        userService.create(User.builder().email(email).password(password).build());
//
//        return new ResponseEntity<>("Registered Successfully", HttpStatus.CREATED);
//
//    }

    public static String generateUserId() {
        long time = System.currentTimeMillis() % 100000000; // last 8 digits of time
        String timePart = Long.toString(time, 36); // base36 for compactness

        int random = ThreadLocalRandom.current().nextInt(36 * 36); // 2 chars
        String randomPart = Integer.toString(random, 36);

        return "AI-EMP-" + timePart + randomPart; // max ~10 chars
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
            return new ResponseEntity<>("Invalid user!", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(email);
        return ResponseEntity.ok(Map.of("token", token));

    }

}
