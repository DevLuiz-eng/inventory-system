package com.luizdev.inventory_system_remastered.repositories;

import com.luizdev.inventory_system_remastered.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndActiveTrue(String email);

    Page<User> findByActiveTrueOrderByNameAsc(Pageable pageable);

    boolean existsByEmail(String email);

    List<User> findByActiveTrueOrderByNameAsc();

}
