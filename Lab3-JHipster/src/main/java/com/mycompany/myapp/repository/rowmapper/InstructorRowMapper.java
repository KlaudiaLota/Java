package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Instructor;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Instructor}, with proper type conversions.
 */
@Service
public class InstructorRowMapper implements BiFunction<Row, String, Instructor> {

    private final ColumnConverter converter;

    public InstructorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Instructor} stored in the database.
     */
    @Override
    public Instructor apply(Row row, String prefix) {
        Instructor entity = new Instructor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setBio(converter.fromRow(row, prefix + "_bio", String.class));
        entity.setHireDate(converter.fromRow(row, prefix + "_hire_date", Instant.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Float.class));
        return entity;
    }
}
