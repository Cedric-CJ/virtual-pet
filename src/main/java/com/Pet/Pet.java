package com.Pet;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private int hunger;
    private int durst;
    private int energie;
    private int komfort;
    private LocalDate createdDate;
    private LocalDateTime lastFed;
    private LocalDateTime lastWatered;
    private LocalDateTime lastSlept;
    private LocalDateTime lastPetted;
    private LocalDateTime lastShowered;

    public Pet() {}

    public Pet(String name, String type ) {
        this.name = name;
        this.type = type;
        this.createdDate = LocalDate.now();
        this.hunger = 500;
        this.durst = 50;
        this.energie = 500;
        this.komfort = 500;
        this.lastFed = LocalDateTime.now();
        this.lastWatered = LocalDateTime.now();
        this.lastSlept = LocalDateTime.now();
        this.lastPetted = LocalDateTime.now();
        this.lastShowered = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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