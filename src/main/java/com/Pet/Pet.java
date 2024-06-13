package com.Pet;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

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
    private int lastFed;
    private int lastWatered;
    private int lastSlept;
    private int lastPetted;
    private int lastShowered;

    public Pet() {}

    public Pet(String name, String type ) {
        this.name = name;
        this.type = type;
        this.createdDate = LocalDate.now();
        this.hunger = 0;
        this.durst = 0;
        this.energie = 0;
        this.komfort = 0;
        this.lastFed = 0;
        this.lastWatered = 0;
        this.lastSlept = 0;
        this.lastPetted = 0;
        this.lastShowered = 0;
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

    public int getLastFed() {
        return lastFed;
    }

    public void setLastFed(int lastFed) {
        this.lastFed = lastFed;
    }

    public int getLastWatered() {
        return lastWatered;
    }

    public void setLastWatered(int lastWatered) {
        this.lastWatered = lastWatered;
    }

    public int getLastSlept() {
        return lastSlept;
    }

    public void setLastSlept(int lastSlept) {
        this.lastSlept = lastSlept;
    }

    public int getLastPetted() {
        return lastPetted;
    }

    public void setLastPetted(int lastPetted) {
        this.lastPetted = lastPetted;
    }

    public int getLastShowered() {
        return lastShowered;
    }

    public void setLastShowered(int lastShowered) {
        this.lastShowered = lastShowered;
    }
}