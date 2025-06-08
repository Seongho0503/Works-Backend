package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;

	public UserEntity create(final UserEntity userEntity) {
		if (userEntity == null || userEntity.getUsername() == null) {
			throw new RuntimeException("Invalid arguments");
		}
		
		final String username = userEntity.getUsername();
		
		if (userRepository.existsByUsername(username)) {
			log.warn("Username already exists {}", username);
			throw new RuntimeException("Username already exists");
		}

		return userRepository.save(userEntity);
	}

	public UserEntity getByCredentials(final String username, final String password, final PasswordEncoder encoder) {
		final UserEntity originalUser = userRepository.findByUsername(username);

		if (originalUser != null && encoder.matches(password, originalUser.getPassword())) {
			return originalUser;
		}
		
		return null;
	}

	public void delete(UserEntity user) {
		if (user == null || user.getId() == null) {
			throw new IllegalArgumentException("삭제할 유저 정보가 올바르지 않습니다.");
		}
		userRepository.delete(user);
	}



}
