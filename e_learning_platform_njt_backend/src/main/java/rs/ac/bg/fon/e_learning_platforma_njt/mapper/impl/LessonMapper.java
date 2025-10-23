package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.LessonDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Course;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Lesson;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.LessonType;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;

@Component
public class LessonMapper implements DtoEntityMapper<LessonDto, Lesson> {

    @Override
    public LessonDto toDto(Lesson e) {
        if (e == null) {
            return null;
        }

        Long lessonTypeId = (e.getLessonType() != null) ? e.getLessonType().getLessonTypeId() : null;
        Long courseId = (e.getCourse() != null) ? e.getCourse().getCourseId() : null;

        return new LessonDto(
                e.getLessonId(),
                e.getLessonTitle(),
                e.getLessonSummary(),
                e.getLessonOrderIndex(),
                lessonTypeId,
                courseId,
                /* lessonAvailable */ e.isLessonAvailable(),
                /* createdAt */ e.getCreatedAt(),
                /* updatedAt */ e.getUpdatedAt()
        );
    }

    @Override
    public Lesson toEntity(LessonDto t) {
        if (t == null) {
            return null;
        }

        LessonType lessonType = (t.getLessonTypeId() != null) ? new LessonType(t.getLessonTypeId()) : null;
        Course course = (t.getCourseId() != null) ? new Course(t.getCourseId()) : null;

        Lesson e = new Lesson();
        e.setLessonId(t.getLessonId()); // null kod create, setovano kod update
        e.setLessonTitle(t.getLessonTitle());
        e.setLessonSummary(t.getLessonSummary());
        e.setLessonOrderIndex(t.getLessonOrderIndex());
        e.setLessonType(lessonType);
        e.setCourse(course);
        e.setLessonAvailable(Boolean.TRUE.equals(t.getLessonAvailable()));

        // createdAt/updatedAt ne postavljamo ovde; @PrePersist/@PreUpdate u entitetu
        return e;
    }

    /**
     * Primeni izmene DTO-a na postojeći entitet (UPDATE).
     */
    public void apply(LessonDto t, Lesson e) {
        if (t == null || e == null) {
            return;
        }

        e.setLessonTitle(t.getLessonTitle());
        e.setLessonSummary(t.getLessonSummary());
        e.setLessonOrderIndex(t.getLessonOrderIndex());
        e.setLessonType(t.getLessonTypeId() != null ? new LessonType(t.getLessonTypeId()) : null);
        e.setCourse(t.getCourseId() != null ? new Course(t.getCourseId()) : null);
        if (t.getLessonAvailable() != null) {
            e.setLessonAvailable(t.getLessonAvailable());
        }
        // createdAt/updatedAt ne diramo; updatedAt će se osvežiti preko @PreUpdate
    }
}
