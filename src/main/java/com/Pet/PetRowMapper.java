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
        pet.setLastFed(rs.getTimestamp("lastFed").toLocalDateTime());
        pet.setLastWatered(rs.getTimestamp("lastWatered").toLocalDateTime());
        pet.setLastSlept(rs.getTimestamp("lastSlept").toLocalDateTime());
        pet.setLastPetted(rs.getTimestamp("lastPetted").toLocalDateTime());
        pet.setLastShowered(rs.getTimestamp("lastShowered").toLocalDateTime());
        return pet;
    }
}