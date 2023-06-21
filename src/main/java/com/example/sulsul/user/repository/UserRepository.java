package com.example.sulsul.user.repository;

import com.example.sulsul.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {



}
