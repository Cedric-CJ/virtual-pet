package com.HTW.Pet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, PetId> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Pet p " +
            "WHERE p.userId = :userId AND p.name = :name " +
            "OR EXISTS (SELECT 1 FROM ApplicationDead ad WHERE ad.userId = :userId AND ad.name = :name)")
    boolean existsByUserIdAndName(Long userId, String name);
}