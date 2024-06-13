package com.Pet;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_pet")
@IdClass(PetId.class)
public class Pet {

    @Id
    @Column(name = "username")
    private String username;

    @Id
    @Column(name = "name")
    private String name;

    private String type;
    private int hunger;
    private int durst;
    private int energie;
    private int komfort;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "last_fed")
    private LocalDateTime lastFed;

    @Column(name = "last_watered")
    private LocalDateTime lastWatered;

    @Column(name = "last_slept")
    private LocalDateTime lastSlept;

    @Column(name = "last_petted")
    private LocalDateTime lastPetted;

    @Column(name = "last_showered")
    private LocalDateTime lastShowered;

    // Default constructor
    public Pet() {}

    // Constructor with all fields
    public Pet(String username, String name, String type, int hunger, int durst, int energie, int komfort, LocalDate createdDate, LocalDateTime lastFed, LocalDateTime lastWatered, LocalDateTime lastSlept, LocalDateTime lastPetted, LocalDateTime lastShowered) {
        this.username = username;
        this.name = name;
        this.type = type;
        this.hunger = hunger;
        this.durst = durst;
        this.energie = energie;
        this.komfort = komfort;
        this.createdDate = createdDate;
        this.lastFed = lastFed;
        this.lastWatered = lastWatered;
        this.lastSlept = lastSlept;
        this.lastPetted = lastPetted;
        this.lastShowered = lastShowered;
    }

    // Getters and setters for all fields
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getDurst() {
        return durst;
    }

    public void setDurst(int durst) {
        this.durst = durst;
    }

    public int getEnergie() {
        return energie;
    }

    public void setEnergie(int energie) {
        this.energie = energie;
    }

    public int getKomfort() {
        return komfort;
    }

    public void setKomfort(int komfort) {
        this.komfort = komfort;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getLastFed() {
        return lastFed;
    }

    public void setLastFed(LocalDateTime lastFed) {
        this.lastFed = lastFed;
    }

    public LocalDateTime getLastWatered() {
        return lastWatered;
    }

    public void setLastWatered(LocalDateTime lastWatered) {
        this.lastWatered = lastWatered;
    }

    public LocalDateTime getLastSlept() {
        return lastSlept;
    }

    public void setLastSlept(LocalDateTime lastSlept) {
        this.lastSlept = lastSlept;
    }

    public LocalDateTime getLastPetted() {
        return lastPetted;
    }

    public void setLastPetted(LocalDateTime lastPetted) {
        this.lastPetted = lastPetted;
    }

    public LocalDateTime getLastShowered() {
        return lastShowered;
    }

    public void setLastShowered(LocalDateTime lastShowered) {
        this.lastShowered = lastShowered;
    }
}