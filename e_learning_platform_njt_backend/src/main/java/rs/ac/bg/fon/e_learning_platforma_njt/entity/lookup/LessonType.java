package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@JsonIgnoreProperties({"lessons"})
@Entity
@Table(name = "lesson_type")
public class LessonType implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_type_id")
    private Long lessonTypeId;

    @Column(name = "lesson_type_name", nullable = false, unique = true, length = 50)
    private String lessonTypeName;

    public LessonType() {
    }

    public LessonType(Long lessonTypeId) {
        this.lessonTypeId = lessonTypeId;
    }

    public LessonType(String lessonTypeName) {
        this.lessonTypeName = lessonTypeName;
    }

    public Long getLessonTypeId() {
        return lessonTypeId;
    }

    public void setLessonTypeId(Long lessonTypeId) {
        this.lessonTypeId = lessonTypeId;
    }

    public String getLessonTypeName() {
        return lessonTypeName;
    }

    public void setLessonTypeName(String lessonTypeName) {
        this.lessonTypeName = lessonTypeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LessonType other = (LessonType) obj;
        return Objects.equals(lessonTypeId, other.lessonTypeId)
                && Objects.equals(lessonTypeName, other.lessonTypeName);
    }

    @Override
    public String toString() {
        return "LessonType: " + lessonTypeName;
    }
}
