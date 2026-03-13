package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.model.User;
import dev.muthukumar.ai_crm.repository.UserRepository;
import dev.muthukumar.ai_crm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String generateUserId() {
        int random = ThreadLocalRandom.current().nextInt(1000, 10000); // 4 digits
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return "AI-" + random + "-" + date;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        user.setUserId(generateUserId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getById(id), HttpStatus.OK);
    }

    @GetMapping("/userId/{userId}")
    public ResponseEntity<User> getByUserId(@PathVariable String userId) {
        return new ResponseEntity<>(userService.getByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<User>> getByUserId(@RequestParam int page, @RequestParam int size) {
        Page<User> userPage = userService.getByPages(page, size);
        return new ResponseEntity<>(userPage, HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        return new ResponseEntity<>(userService.update(id, user), HttpStatus.OK);
    }

    @PutMapping("/userId/{userId}")
    public ResponseEntity<User> updateByUserId(@PathVariable String userId, @RequestBody User user) {
        return new ResponseEntity<>(userService.updateByUserId(userId, user), HttpStatus.OK);
    }

    @DeleteMapping("/id/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @DeleteMapping("/userId/{userId}")
    public void deleteByUserId(@PathVariable String userId) {
        userService.deleteByUserId(userId);
    }

    @PostMapping("/userId/{userId}/password")
    public User updatePassword(@PathVariable String userId, @RequestParam String newPassword) {
        return userService.updatePassword(userId, newPassword);
    }

//    @PostMapping(value = "/userId/{userId}/profile", consumes = "multipart/form-data")
//    public User updateProfile(@PathVariable String userId, @RequestParam("profileImage") MultipartFile profileImage) throws Exception {
//        return userService.updateProfileImage(userId, profileImage);
//    }

}