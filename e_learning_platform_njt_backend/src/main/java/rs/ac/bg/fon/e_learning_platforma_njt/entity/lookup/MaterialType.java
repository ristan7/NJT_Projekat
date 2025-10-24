package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@JsonIgnoreProperties({"materials"})
@Entity
@Table(name = "material_type")
public class MaterialType implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_type_id")
    private Long materialTypeId;

    @Column(name = "material_type_name", nullable = false, unique = true, length = 50)
    private String materialTypeName;

    public MaterialType() {
    }

    public MaterialType(Long materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public MaterialType(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public Long getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(Long materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MaterialType other = (MaterialType) obj;
        return Objects.equals(materialTypeId, other.materialTypeId)
                && Objects.equals(materialTypeName, other.materialTypeName);
    }

    @Override
    public String toString() {
        return "MaterialType: " + materialTypeName;
    }
}
