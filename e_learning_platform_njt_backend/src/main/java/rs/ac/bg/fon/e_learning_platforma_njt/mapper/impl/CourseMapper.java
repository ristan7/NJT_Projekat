package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

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

        Integer lessonCount = 0;
        if (e.getLessons() != null) {
            // Napomena: ako želiš izbeći lazy inicijalizaciju, lessonCount može doći iz custom query-ja u servisu.
            lessonCount = e.getLessons().size();
        }

        return new CourseDto(
                e.getCourseId(),
                e.getCourseTitle(),
                e.getCourseDescription(),
                e.getCoursePrice(),
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
        e.setCourseId(t.getCourseId()); // null kod create, setovano kod update
        e.setCourseTitle(t.getCourseTitle());
        e.setCourseDescription(t.getCourseDescription());
        e.setCoursePrice(t.getCoursePrice());
        e.setAuthor(author);
        e.setCourseLevel(courseLevel);

        // koristimo setter da bi entitet eventualno postavio publishedAt kada status postane PUBLISHED
        e.setCourseStatus(courseStatus);

        // createdAt/updatedAt/publishedAt NE setujemo ovde (audit & biz. logika u entitetu/servisu)
        return e;
    }

    /**
     * Primeni izmene iz DTO-a na postojeći entitet (UPDATE).
     */
    public void apply(CourseDto t, Course e) {
        if (t == null || e == null) {
            return;
        }

        e.setCourseTitle(t.getCourseTitle());
        e.setCourseDescription(t.getCourseDescription());
        e.setCoursePrice(t.getCoursePrice());
        e.setAuthor(t.getAuthorId() != null ? new User(t.getAuthorId()) : null);
        e.setCourseLevel(t.getCourseLevelId() != null ? new CourseLevel(t.getCourseLevelId()) : null);

        // setter zadržava logiku publish timestamp-a
        e.setCourseStatus(t.getCourseStatusId() != null ? new CourseStatus(t.getCourseStatusId()) : null);

        // Datume NE diramo; updatedAt će se osvežiti preko @PreUpdate
        // publishedAt menja entitet kada status postane PUBLISHED (ako je ranije bio null)
    }
}
