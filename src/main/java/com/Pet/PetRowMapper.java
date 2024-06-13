package com.Pet;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PetRowMapper implements RowMapper<Pet> {
    @Override
    public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
        Pet pet = new Pet();
        pet.setId(rs.getLong("id"));
        pet.setName(rs.getString("name"));
        pet.setType(rs.getString("type"));
        pet.setHunger(rs.getInt("hunger"));
        pet.setDurst(rs.getInt("durst"));
        pet.setEnergie(rs.getInt("energie"));
        pet.setKomfort(rs.getInt("komfort"));
        pet.setCreatedDate(rs.getDate("created_date").toLocalDate());
        pet.setLastFed(rs.getInt("lastFed"));
        pet.setLastWatered(rs.getInt("lastWatered"));
        pet.setLastSlept(rs.getInt("lastSlept"));
        pet.setLastPetted(rs.getInt("lastPetted"));
        pet.setLastShowered(rs.getInt("lastShowered"));
        return pet;
    }
}