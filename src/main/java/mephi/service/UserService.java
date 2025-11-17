package mephi.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mephi.dto.UserDto;
import mephi.entity.Enrollment;
import mephi.entity.User;
import mephi.mapper.UserMapper;
import mephi.repository.EnrollmentRepository;
import mephi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollRepository;
    private final UserMapper userMapper;

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public List<UserDto> getAllUsers() {
        List<UserDto> users = new ArrayList<>();

        for (var user : userRepository.findAll()) {
            users.add(userMapper.toDto(user));
        }
        return users;
    }

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        if (user.getProfile() != null) {
            user.getProfile().setUser(user);
        }
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setRole(userDto.getRole());

        User updated = userRepository.save(user);
        return userMapper.toDto(updated);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
