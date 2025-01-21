package com.HTW.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findByUsername(String username);

    @Query(value = "SELECT u.username FROM v_pet.user u WHERE u.id = ?1", nativeQuery = true)
    String findUsernameById(Long id);
}