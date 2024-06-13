package com.Pet;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PetRowMapper implements RowMapper<Pet> {

    @Override
    public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
        Pet pet = new Pet();
        pet.setUsername(rs.getString("username"));
        pet.setName(rs.getString("name"));
        pet.setType(rs.getString("type"));
        pet.setHunger(rs.getInt("hunger"));
        pet.setDurst(rs.getInt("durst"));
        pet.setEnergie(rs.getInt("energie"));
        pet.setKomfort(rs.getInt("komfort"));
        pet.setCreatedDate(rs.getObject("created_date", LocalDate.class));
        pet.setLastFed(rs.getObject("last_fed", LocalDateTime.class));
        pet.setLastWatered(rs.getObject("last_watered", LocalDateTime.class));
        pet.setLastSlept(rs.getObject("last_slept", LocalDateTime.class));
        pet.setLastPetted(rs.getObject("last_petted", LocalDateTime.class));
        pet.setLastShowered(rs.getObject("last_showered", LocalDateTime.class));
        return pet;
    }
}