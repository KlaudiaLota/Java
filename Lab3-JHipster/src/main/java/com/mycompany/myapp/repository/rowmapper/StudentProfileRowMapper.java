package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.StudentProfile;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link StudentProfile}, with proper type conversions.
 */
@Service
public class StudentProfileRowMapper implements BiFunction<Row, String, StudentProfile> {

    private final ColumnConverter converter;

    public StudentProfileRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link StudentProfile} stored in the database.
     */
    @Override
    public StudentProfile apply(Row row, String prefix) {
        StudentProfile entity = new StudentProfile();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setBio(converter.fromRow(row, prefix + "_bio", String.class));
        entity.setLinkedinUrl(converter.fromRow(row, prefix + "_linkedin_url", String.class));
        entity.setGithubUrl(converter.fromRow(row, prefix + "_github_url", String.class));
        entity.setProfilePictureUrl(converter.fromRow(row, prefix + "_profile_picture_url", String.class));
        return entity;
    }
}
