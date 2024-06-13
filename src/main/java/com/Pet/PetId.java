package com.Pet;

import java.io.Serializable;
import java.util.Objects;

public class PetId implements Serializable {
    private String username;
    private String name;

    public PetId() {}

    public PetId(String username, String name) {
        this.username = username;
        this.name = name;
    }

    // Getters, setters, equals, and hashCode methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetId petId = (PetId) o;
        return Objects.equals(username, petId.username) && Objects.equals(name, petId.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name);
    }
}