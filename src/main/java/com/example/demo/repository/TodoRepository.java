package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.TodoEntity;

import java.util.List;

/*
	TodoEntity를 관리하는 JPA 리포지토리 인터페이스
 */

@Repository
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
	
	List<TodoEntity> findByUserId(Long userId);

	@Query("SELECT t FROM TodoEntity t WHERE t.userId = ?1")
	TodoEntity findByUserIdQuery(Long userId);

}
