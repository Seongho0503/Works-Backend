package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.security.TokenProvider;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserController {

	private final UserService userService;

	private final TokenProvider tokenProvider;

	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
		try {
			if (userDTO == null || userDTO.getPassword() == null) {
				throw new RuntimeException("Invalid Password value.");
			}

			UserEntity user = UserEntity.builder()
				.username(userDTO.getUsername())
				.password(passwordEncoder.encode(userDTO.getPassword()))
				.build();

			UserEntity registeredUser = userService.create(user);
			UserDTO responseUserDTO = UserDTO.builder()
				.id(registeredUser.getId())
				.username(registeredUser.getUsername())
				.build();

			return ResponseEntity.ok().body(responseUserDTO);
		}
		catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder()
				.error(e.getMessage())
				.build();
			
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
		UserEntity user = userService.getByCredentials(userDTO.getUsername(), userDTO.getPassword(), passwordEncoder);

		if (user != null) {
			final String token = tokenProvider.create(user);
			
			final UserDTO responseUserDTO = UserDTO.builder()
				.username(user.getUsername())
				.id(user.getId())
				.token(token)
				.build();
			
			return ResponseEntity.ok().body(responseUserDTO);
		}
		else {
			ResponseDTO responseDTO = ResponseDTO.builder()
				.error("Login failed.")
				.build();
			
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

	@PostMapping("/delete")
	public ResponseEntity<?> deleteUser(@RequestBody UserDTO userDTO) {
		try {
			// 사용자 인증 정보로 사용자 조회
			UserEntity user = userService.getByCredentials(
					userDTO.getUsername(), userDTO.getPassword(), passwordEncoder
			);

			if (user != null) {
				userService.delete(user);  // 사용자 삭제
				return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다.");
			} else {
				return ResponseEntity.badRequest().body("회원 정보를 확인할 수 없습니다.");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("회원 탈퇴 중 오류 발생: " + e.getMessage());
		}
	}


}
