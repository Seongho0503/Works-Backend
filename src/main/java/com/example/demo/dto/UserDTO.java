package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	
	private String token; // 로그인 성공 시 발급되는 인증 토큰(JWT 등)
	private String username;
	private String password;
	private Long id;
	
}
