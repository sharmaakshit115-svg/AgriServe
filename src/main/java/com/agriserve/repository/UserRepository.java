package com.agriserve.repository;

import com.agriserve.entity.User;
import com.agriserve.entity.enums.Role;
import com.agriserve.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);

    Page<User> findAllByRole(Role role, Pageable pageable);

    Page<User> findAllByStatus(Status status, Pageable pageable);

    List<User> findAllByRole(Role role);
}
