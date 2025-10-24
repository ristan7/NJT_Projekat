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

        LessonDto dto = new LessonDto();
        dto.setLessonId(e.getLessonId());
        dto.setLessonTitle(e.getLessonTitle());
        dto.setLessonSummary(e.getLessonSummary());
        dto.setLessonOrderIndex(e.getLessonOrderIndex());
        dto.setLessonAvailable(e.isLessonAvailable());
        dto.setFreePreview(e.isFreePreview());           // NEW
        dto.setLessonTypeId(lessonTypeId);
        dto.setCourseId(courseId);
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    @Override
    public Lesson toEntity(LessonDto t) {
        if (t == null) {
            return null;
        }

        Lesson e = new Lesson();
        e.setLessonId(t.getLessonId());

        if (t.getLessonTitle() != null) {
            e.setLessonTitle(safeTrim(t.getLessonTitle()));
        }
        if (t.getLessonSummary() != null) {
            e.setLessonSummary(safeTrim(t.getLessonSummary()));
        }
        if (t.getLessonOrderIndex() != null) {
            e.setLessonOrderIndex(t.getLessonOrderIndex());
        }

        if (t.getLessonTypeId() != null) {
            e.setLessonType(new LessonType(t.getLessonTypeId()));
        }
        if (t.getCourseId() != null) {
            e.setCourse(new Course(t.getCourseId()));
        }

        e.setLessonAvailable(t.isLessonAvailable());
        e.setFreePreview(t.isFreePreview());             // NEW
        return e;
    }

    @Override
    public void apply(LessonDto t, Lesson e) {
        if (t == null || e == null) {
            return;
        }

        if (t.getLessonTitle() != null) {
            e.setLessonTitle(safeTrim(t.getLessonTitle()));
        }
        if (t.getLessonSummary() != null) {
            e.setLessonSummary(safeTrim(t.getLessonSummary()));
        }
        if (t.getLessonOrderIndex() != null) {
            e.setLessonOrderIndex(t.getLessonOrderIndex());
        }
        if (t.getLessonTypeId() != null) {
            e.setLessonType(new LessonType(t.getLessonTypeId()));
        }
        // course se ne menja u generalnom update-u

        e.setLessonAvailable(t.isLessonAvailable());
        e.setFreePreview(t.isFreePreview());             // NEW
    }

    private String safeTrim(String s) {
        return (s == null) ? null : s.trim();
    }
}
