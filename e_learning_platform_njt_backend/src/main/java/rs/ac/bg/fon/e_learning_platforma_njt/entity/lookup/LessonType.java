package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LessonType other = (LessonType) obj;
        if (!Objects.equals(this.lessonTypeName, other.lessonTypeName)) {
            return false;
        }
        return Objects.equals(this.lessonTypeId, other.lessonTypeId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LessonType: ").append(lessonTypeName);
        return sb.toString();
    }

}
