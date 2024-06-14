package com.HTW.Pet;

import java.io.Serializable;
import java.util.Objects;

public class PetId implements Serializable {
    private Long userId;
    private String name;

    // Default constructor
    public PetId() {}

    // Parameterized constructor
    public PetId(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Override equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetId petId = (PetId) o;
        return Objects.equals(userId, petId.userId) && Objects.equals(name, petId.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }
}