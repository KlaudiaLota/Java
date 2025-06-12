package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Student;
import com.mycompany.myapp.domain.enumeration.Gender;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Student}, with proper type conversions.
 */
@Service
public class StudentRowMapper implements BiFunction<Row, String, Student> {

    private final ColumnConverter converter;

    public StudentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Student} stored in the database.
     */
    @Override
    public Student apply(Row row, String prefix) {
        Student entity = new Student();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setBirthDate(converter.fromRow(row, prefix + "_birth_date", LocalDate.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", Gender.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setRegistrationDate(converter.fromRow(row, prefix + "_registration_date", Instant.class));
        entity.setProfileId(converter.fromRow(row, prefix + "_profile_id", Long.class));
        return entity;
    }
}
