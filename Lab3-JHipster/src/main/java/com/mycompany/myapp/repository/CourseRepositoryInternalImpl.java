package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Course;
import com.mycompany.myapp.domain.Student;
import com.mycompany.myapp.repository.rowmapper.CourseRowMapper;
import com.mycompany.myapp.repository.rowmapper.InstructorRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Course entity.
 */
@SuppressWarnings("unused")
class CourseRepositoryInternalImpl extends SimpleR2dbcRepository<Course, Long> implements CourseRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final InstructorRowMapper instructorMapper;
    private final CourseRowMapper courseMapper;

    private static final Table entityTable = Table.aliased("course", EntityManager.ENTITY_ALIAS);
    private static final Table instructorTable = Table.aliased("instructor", "instructor");

    private static final EntityManager.LinkTable studentLink = new EntityManager.LinkTable(
        "rel_course__student",
        "course_id",
        "student_id"
    );

    public CourseRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        InstructorRowMapper instructorMapper,
        CourseRowMapper courseMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Course.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.instructorMapper = instructorMapper;
        this.courseMapper = courseMapper;
    }

    @Override
    public Flux<Course> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Course> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CourseSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(InstructorSqlHelper.getColumns(instructorTable, "instructor"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(instructorTable)
            .on(Column.create("instructor_id", entityTable))
            .equals(Column.create("id", instructorTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Course.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Course> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Course> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Course> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Course> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Course> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Course process(Row row, RowMetadata metadata) {
        Course entity = courseMapper.apply(row, "e");
        entity.setInstructor(instructorMapper.apply(row, "instructor"));
        return entity;
    }

    @Override
    public <S extends Course> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Course> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(studentLink, entity.getId(), entity.getStudents().stream().map(Student::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(studentLink, entityId);
    }
}
