package com.mycompany.myapp.repository.rowmapper;

import com.mycompany.myapp.domain.Course;
import com.mycompany.myapp.domain.enumeration.CourseLevel;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Course}, with proper type conversions.
 */
@Service
public class CourseRowMapper implements BiFunction<Row, String, Course> {

    private final ColumnConverter converter;

    public CourseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Course} stored in the database.
     */
    @Override
    public Course apply(Row row, String prefix) {
        Course entity = new Course();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setStartDate(converter.fromRow(row, prefix + "_start_date", LocalDate.class));
        entity.setEndDate(converter.fromRow(row, prefix + "_end_date", LocalDate.class));
        entity.setLevel(converter.fromRow(row, prefix + "_level", CourseLevel.class));
        entity.setPrice(converter.fromRow(row, prefix + "_price", Float.class));
        entity.setInstructorId(converter.fromRow(row, prefix + "_instructor_id", Long.class));
        return entity;
    }
}
