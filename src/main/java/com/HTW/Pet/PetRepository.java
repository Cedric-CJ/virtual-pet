package com.HTW.Pet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    boolean existsByUserIdAndName(Long userId, String name);
}