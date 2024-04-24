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
    protected String name;
    private String type;
    private String asciiArt;
    protected int hunger;
    protected int durst;
    protected int energie;
    protected int komfort;
    private LocalDate createdDate;
    private int lastFed;
    private int lastWatered;
    private int lastSlept;
    private int lastPetted;
    private int lastShowered;

    public Pet() {
    }

    public Pet(String name, String type, String asciiArt) {
        this.name = name;
        this.type = type;
        this.asciiArt = asciiArt;
        this.createdDate = LocalDate.now();
        this.hunger = 500;
        this.durst = 50;
        this.energie = 500;
        this.komfort = 500;
        this.lastFed = 0;
        this.lastWatered = 0;
        this.lastSlept = 0;
        this.lastPetted = 0;
        this.lastShowered = 0;
    }
    public void tick() {
        if (++lastFed >= 12) { // Annahme: 2x pro Tag, wenn ein "Tick" als eine halber Tag zählt
            hunger -= 10;
            lastFed = 0;
        }
        if (++lastWatered >= 24) { // 1x pro Tag
            durst -= 20;
            lastWatered = 0;
        }
        if (++lastSlept >= 24) { // 1x pro Tag
            energie -= 15;
            lastSlept = 0;
        }
        if (++lastPetted >= 8) { // 3x pro Tag
            komfort -= 5;
            lastPetted = 0;
        }
        if (++lastShowered >= 168) { // 1x pro Woche, wenn ein "Tick" als eine Stunde zählt
            komfort -= 10;
            lastShowered = 0;
        }
        hunger = Math.max(hunger, 0);
        durst = Math.max(durst, 0);
        energie = Math.max(energie, 0);
        komfort = Math.max(komfort, 0);
    }
    public void essengeben(){
        hunger = Math.min(hunger + 50, 100);
        lastFed = 0;
    }
    public void wassergeben(){
        durst = Math.min(durst + 100, 100);
        lastWatered = 0;
    }
    public void schlafen(){
        energie = 100;
        lastSlept = 0;
    }
    public void streicheln(){
        komfort = Math.min(komfort + 10, 100);
        lastPetted = 0;
    }
    public void duschen(){
        komfort = Math.min(komfort + 100, 100);
        lastShowered = 0;
    }
    public String getName(){
        return name;
    }

    public void spielen(){
        komfort = Math.min(komfort + 10, 100);
        lastPetted = 0;
        energie = Math.min(energie - 10, 100);
    };

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public String getType() {
        return type;
    }
    public String getAsciiArt() {
        return asciiArt;
    }
    public int getHunger() {
        return hunger;
    }
    public int getDurst() {
        return durst;
    }
    public int getEnergie() {
        return energie;
    }
    public int getKomfort() {
        return komfort;
    }
    public Long getId() {
        return id;
    }
}