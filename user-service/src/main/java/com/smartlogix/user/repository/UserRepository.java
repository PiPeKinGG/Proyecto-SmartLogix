package com.smartlogix.user.repository;

import com.smartlogix.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndPymeId(String email, Long pymeId);
    Optional<User> findByEmail(String email);
    List<User> findAllByPymeId(Long pymeId);
    boolean existsByEmail(String email);
}
