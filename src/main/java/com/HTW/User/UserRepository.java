package com.HTW.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findByUsername(String username);

    @Query("SELECT u.username FROM ApplicationUser u WHERE u.id = ?1")
    String findUsernameById(Long id);
}