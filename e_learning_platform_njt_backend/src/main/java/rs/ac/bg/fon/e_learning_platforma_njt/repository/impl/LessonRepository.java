package rs.ac.bg.fon.e_learning_platforma_njt.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Lesson;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.MyAppRepository;

@Repository
public class LessonRepository implements MyAppRepository<Lesson, Long> {

    @PersistenceContext
    private EntityManager em;

    /* ===================== Basic CRUD (iz MyAppRepository) ===================== */
    @Override
    public List<Lesson> findAll() {
        return em.createQuery("SELECT l FROM Lesson l", Lesson.class)
                .getResultList();
    }

    @Override
    public Lesson findById(Long id) throws Exception {
        Lesson l = em.find(Lesson.class, id);
        if (l == null) {
            throw new Exception("Lesson not found: " + id);
        }
        return l;
    }

    @Override
    @Transactional
    public void save(Lesson entity) {
        if (entity.getLessonId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Lesson l = em.find(Lesson.class, id);
        if (l != null) {
            em.remove(l);
        }
    }

    /* ===================== Dodatne korisne metode (za sada) ===================== */
    /**
     * Sve lekcije datog kursa, sortirane po lessonOrderIndex ASC.
     */
    public List<Lesson> findAllByCourseId(Long courseId) {
        return em.createQuery(
                "SELECT l FROM Lesson l "
                + "WHERE l.course.courseId = :cid "
                + "ORDER BY l.lessonOrderIndex ASC", Lesson.class)
                .setParameter("cid", courseId)
                .getResultList();
    }

    /**
     * Učitavanje jedne lekcije zajedno sa materijalima (JOIN FETCH), već sortiranim po @OrderBy.
     */
    public Lesson findByIdWithMaterials(Long lessonId) throws Exception {
        List<Lesson> result = em.createQuery(
                "SELECT DISTINCT l FROM Lesson l "
                + "LEFT JOIN FETCH l.materials m "
                + "WHERE l.lessonId = :id", Lesson.class)
                .setParameter("id", lessonId)
                .getResultList();

        if (result.isEmpty()) {
            throw new Exception("Lesson not found: " + lessonId);
        }
        return result.get(0);
    }

    /**
     * Koliko lekcija ima u kursu — korisno za statistiku ili validacije.
     */
    public long countByCourseId(Long courseId) {
        return em.createQuery(
                "SELECT COUNT(l) FROM Lesson l WHERE l.course.courseId = :cid", Long.class)
                .setParameter("cid", courseId)
                .getSingleResult();
    }

    /**
     * Sledeći indeks redosleda za dati kurs (COALESCE(MAX)+1). Koristi se kada dodajemo novu lekciju na kraj liste.
     */
    public int getNextOrderIndexForCourse(Long courseId) {
        Integer next = em.createQuery(
                "SELECT COALESCE(MAX(l.lessonOrderIndex), 0) + 1 "
                + "FROM Lesson l WHERE l.course.courseId = :cid", Integer.class)
                .setParameter("cid", courseId)
                .getSingleResult();
        return next != null ? next : 1;
    }
    
}
