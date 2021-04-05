package io.educare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.educare.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	   Optional<User> findByUsername(String username);
	}
