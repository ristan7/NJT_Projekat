package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.CourseDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Course;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseLevel;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.CourseStatus;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;

@Component
public class CourseMapper implements DtoEntityMapper<CourseDto, Course> {

    @Override
    public CourseDto toDto(Course e) {
        if (e == null) {
            return null;
        }

        Long authorId = (e.getAuthor() != null) ? e.getAuthor().getUserId() : null;
        Long courseLevelId = (e.getCourseLevel() != null) ? e.getCourseLevel().getCourseLevelId() : null;
        Long courseStatusId = (e.getCourseStatus() != null) ? e.getCourseStatus().getCourseStatusId() : null;

        Integer lessonCount = null;
        try {
            if (e.getLessons() != null && Hibernate.isInitialized(e.getLessons())) {
                lessonCount = e.getLessons().size();
            }
        } catch (Exception ignore) {
        }

        return new CourseDto(
                e.getCourseId(),
                e.getCourseTitle(),
                e.getCourseDescription(),
                authorId,
                courseLevelId,
                courseStatusId,
                e.getCreatedAt(),
                e.getUpdatedAt(),
                e.getPublishedAt(),
                lessonCount
        );
    }

    @Override
    public Course toEntity(CourseDto t) {
        if (t == null) {
            return null;
        }

        User author = (t.getAuthorId() != null) ? new User(t.getAuthorId()) : null;
        CourseLevel courseLevel = (t.getCourseLevelId() != null) ? new CourseLevel(t.getCourseLevelId()) : null;
        CourseStatus courseStatus = (t.getCourseStatusId() != null) ? new CourseStatus(t.getCourseStatusId()) : null;

        Course e = new Course();
        e.setCourseId(t.getCourseId());
        e.setCourseTitle(safeTrim(t.getCourseTitle()));
        e.setCourseDescription(safeTrim(t.getCourseDescription()));
        e.setAuthor(author);
        e.setCourseLevel(courseLevel);
        e.setCourseStatus(courseStatus);

        // Datumi i publish logika ostaju u entitetu
        return e;
    }

    /**
     * Primeni izmene iz DTO-a na postojeći entitet (UPDATE). - Ne menjamo autora (određuje se kroz autentifikaciju).
     */
    @Override
    public void apply(CourseDto t, Course e) {
        if (t == null || e == null) {
            return;
        }

        e.setCourseTitle(safeTrim(t.getCourseTitle()));
        e.setCourseDescription(safeTrim(t.getCourseDescription()));

        if (t.getCourseLevelId() != null) {
            e.setCourseLevel(new CourseLevel(t.getCourseLevelId()));
        }

        if (t.getCourseStatusId() != null) {
            e.setCourseStatus(new CourseStatus(t.getCourseStatusId()));
        }
        // Datumi se ne diraju; updatedAt se osvežava preko @PreUpdate u entitetu.
    }

    // Helper
    private String safeTrim(String s) {
        return (s == null) ? null : s.trim();
    }
}
