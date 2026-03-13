package dev.muthukumar.ai_crm.service;

import dev.muthukumar.ai_crm.model.User;
import dev.muthukumar.ai_crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public User getByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public Page<User> getByPages(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    public User update(Long id, User updated) {
        User existing = getById(id);
        if (!existing.getEmail().equals(updated.getEmail())
                && userRepository.existsByEmail(updated.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setRole(updated.getRole());
        existing.setEmploymentType(updated.getEmploymentType());
        existing.setPosition(updated.getPosition());
        existing.setDateOfJoining(updated.getDateOfJoining());

        return userRepository.save(existing);
    }

    public User updateByUserId(String userId, User updated) {
        User existing = getByUserId(userId);
        if (!existing.getEmail().equals(updated.getEmail())
                && userRepository.existsByEmail(updated.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setRole(updated.getRole());
        existing.setEmploymentType(updated.getEmploymentType());
        existing.setPosition(updated.getPosition());
        existing.setDateOfJoining(updated.getDateOfJoining());

        return userRepository.save(existing);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteByUserId(String userId) {
        User user = getByUserId(userId);
        userRepository.delete(user);
    }

    public User updatePassword(String userId, String newPassword) {
        User user = getByUserId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

//    public User updateProfileImage(String userId, MultipartFile profileImage) throws Exception {
//        User user = getByUserId(userId);
//        if (profileImage != null && !profileImage.isEmpty()) {
//            String imageUrl = supabaseStorageService.uploadProfileImage(profileImage);
//            user.setEmployeeProfileUrl(imageUrl);
//        }
//
//        return userRepository.save(user);
//    }

}
