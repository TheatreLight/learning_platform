package mephi.service;

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
public class UserService {
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollRepository;
    private final UserMapper userMapper;

    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow();
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
}
