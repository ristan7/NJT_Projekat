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

        MaterialDto dto = new MaterialDto();
        dto.setMaterialId(e.getMaterialId());
        dto.setMaterialTitle(e.getMaterialTitle());
        dto.setMaterialOrderIndex(e.getMaterialOrderIndex());
        dto.setContent(e.getContent());
        dto.setResourceUrl(e.getResourceUrl());
        dto.setMaterialTypeId(materialTypeId);
        dto.setLessonId(lessonId);
        dto.setCreatedAt(e.getCreatedAt()); // READ_ONLY izlaz
        dto.setUpdatedAt(e.getUpdatedAt()); // READ_ONLY izlaz
        return dto;
    }

    @Override
    public Material toEntity(MaterialDto t) {
        if (t == null) {
            return null;
        }

        Material e = new Material();
        e.setMaterialId(t.getMaterialId()); // null za create
        if (t.getMaterialTitle() != null) {
            e.setMaterialTitle(safeTrim(t.getMaterialTitle()));
        }
        if (t.getMaterialOrderIndex() != null) {
            e.setMaterialOrderIndex(t.getMaterialOrderIndex());
        }
        if (t.getContent() != null) {
            e.setContent(t.getContent());
        }
        if (t.getResourceUrl() != null) {
            e.setResourceUrl(safeTrim(t.getResourceUrl()));
        }
        if (t.getMaterialTypeId() != null) {
            e.setMaterialType(new MaterialType(t.getMaterialTypeId()));
        }
        if (t.getLessonId() != null) {
            e.setLesson(new Lesson(t.getLessonId()));
        }
        // createdAt/updatedAt: @PrePersist/@PreUpdate
        return e;
    }

    /**
     * Partial UPDATE: - String/int polja menjamo samo ako su prosleđena (≠ null). - materialType menjamo samo ako je prosleđen ID. - lesson menjamo samo ako je prosleđen lessonId (inače ostaje postojeći).
     */
    @Override
    public void apply(MaterialDto t, Material e) {
        if (t == null || e == null) {
            return;
        }

        if (t.getMaterialTitle() != null) {
            e.setMaterialTitle(safeTrim(t.getMaterialTitle()));
        }
        if (t.getMaterialOrderIndex() != null) {
            e.setMaterialOrderIndex(t.getMaterialOrderIndex());
        }
        if (t.getContent() != null) {
            e.setContent(t.getContent());
        }
        if (t.getResourceUrl() != null) {
            e.setResourceUrl(safeTrim(t.getResourceUrl()));
        }
        if (t.getMaterialTypeId() != null) {
            e.setMaterialType(new MaterialType(t.getMaterialTypeId()));
        }
        // po novoj logici: lesson ne premeštamo slučajno; samo ako je eksplicitno prosleđen ID
        if (t.getLessonId() != null) {
            e.setLesson(new Lesson(t.getLessonId()));
        }
        // Datume ne diramo (updatedAt ide preko @PreUpdate)
    }

    private String safeTrim(String s) {
        return (s == null) ? null : s.trim();
    }
}
