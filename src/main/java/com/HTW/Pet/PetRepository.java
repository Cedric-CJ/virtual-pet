package com.HTW.Pet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, PetId> {

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
            "FROM v_pet.pet p " +
            "WHERE p.user_id = :userId AND p.name = :name " +
            "OR EXISTS (SELECT 1 FROM v_pet.dead ad WHERE ad.user_id = :userId AND ad.name = :name)",
            nativeQuery = true)
    boolean existsByUserIdAndName(Long userId, String name);
}