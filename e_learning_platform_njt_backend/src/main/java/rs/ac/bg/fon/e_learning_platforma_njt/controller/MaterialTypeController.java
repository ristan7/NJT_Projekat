// MaterialTypeController.java
package rs.ac.bg.fon.e_learning_platforma_njt.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.MaterialType;

import java.util.List;

@RestController
@RequestMapping("/api/material-types")
@CrossOrigin(origins = "http://localhost:3000")
public class MaterialTypeController {

    @PersistenceContext
    private EntityManager em;

    @GetMapping
    public ResponseEntity<List<MaterialType>> listAll() {
        List<MaterialType> list = em.createQuery(
                "SELECT mt FROM MaterialType mt ORDER BY mt.materialTypeName", MaterialType.class
        ).getResultList();
        return ResponseEntity.ok(list);
    }
}
