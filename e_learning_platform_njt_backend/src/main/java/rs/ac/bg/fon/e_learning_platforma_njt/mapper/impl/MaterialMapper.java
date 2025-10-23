package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.MaterialDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Lesson;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Material;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.MaterialType;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;

@Component
public class MaterialMapper implements DtoEntityMapper<MaterialDto, Material> {

    @Override
    public MaterialDto toDto(Material e) {
        if (e == null) {
            return null;
        }

        Long materialTypeId = (e.getMaterialType() != null) ? e.getMaterialType().getMaterialTypeId() : null;
        Long lessonId = (e.getLesson() != null) ? e.getLesson().getLessonId() : null;

        MaterialDto dto = new MaterialDto(
                e.getMaterialId(),
                e.getMaterialTitle(),
                e.getMaterialOrderIndex(),
                e.getContent(),
                e.getResourceUrl(),
                materialTypeId,
                lessonId,
                e.getCreatedAt(), // read-only izlaz
                e.getUpdatedAt() // read-only izlaz
        );
        return dto;
    }

    @Override
    public Material toEntity(MaterialDto t) {
        if (t == null) {
            return null;
        }

        MaterialType type = (t.getMaterialTypeId() != null) ? new MaterialType(t.getMaterialTypeId()) : null;
        Lesson lesson = (t.getLessonId() != null) ? new Lesson(t.getLessonId()) : null;

        Material e = new Material();
        e.setMaterialId(t.getMaterialId());                 // null kod create, setovano kod update
        e.setMaterialTitle(t.getMaterialTitle());
        e.setMaterialOrderIndex(t.getMaterialOrderIndex());
        e.setContent(t.getContent());
        e.setResourceUrl(t.getResourceUrl());
        e.setMaterialType(type);
        e.setLesson(lesson);

        // createdAt / updatedAt NE postavljamo: @PrePersist/@PreUpdate u entitetu
        return e;
    }

    /**
     * Primeni izmene iz DTO-a na postojeći entitet (UPDATE).
     */
    public void apply(MaterialDto t, Material e) {
        if (t == null || e == null) {
            return;
        }

        e.setMaterialTitle(t.getMaterialTitle());
        e.setMaterialOrderIndex(t.getMaterialOrderIndex());
        e.setContent(t.getContent());
        e.setResourceUrl(t.getResourceUrl());
        e.setMaterialType(t.getMaterialTypeId() != null ? new MaterialType(t.getMaterialTypeId()) : null);
        e.setLesson(t.getLessonId() != null ? new Lesson(t.getLessonId()) : null);

        // createdAt / updatedAt NE diramo; updatedAt će se osvežiti preko @PreUpdate
    }
}
