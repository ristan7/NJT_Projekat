/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.e_learning_platforma_njt.entity.impl;

import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;
import java.io.Serializable;
import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.MaterialType;

/**
 *
 * @author mikir
 */
@Entity
@Table(name = "material")
public class Material implements MyEntity, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long materialId;

    @Column(name = "material_name", nullable = false, length = 100)
    private String materialName;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "url", nullable = false, length = 255)
    private String url;

    @ManyToOne(optional = false)
    @JoinColumn(name = "material_type_id", nullable = false)
    private MaterialType materialType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    public Material() {
    }

    public Material(Long materialId) {
        this.materialId = materialId;
    }

    public Material(Long materialId, String materialName, String content, String url, MaterialType materialType, Lesson lesson) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.content = content;
        this.url = url;
        this.materialType = materialType;
        this.lesson = lesson;
    }

    public Material(String materialName, String content, String url, MaterialType materialType, Lesson lesson) {
        this.materialName = materialName;
        this.content = content;
        this.url = url;
        this.materialType = materialType;
        this.lesson = lesson;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public void setMaterialType(MaterialType materialType) {
        this.materialType = materialType;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
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
        final Material other = (Material) obj;
        return Objects.equals(this.materialId, other.materialId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Material: ");
        if (materialName != null) {
            sb.append(materialName);
        }
        if (materialType != null) {
            sb.append(" (").append(materialType.getMaterialTypeName()).append(")");
        }
        return sb.toString();
    }
}
