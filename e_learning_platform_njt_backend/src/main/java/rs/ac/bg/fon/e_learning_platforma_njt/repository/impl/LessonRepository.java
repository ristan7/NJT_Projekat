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

    @Override
    public List<Lesson> findAll() {
        return em.createQuery("SELECT l FROM Lesson l", Lesson.class).getResultList();
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

    /**
     * Sve lekcije kursa (za teachera/admina) po redosledu.
     */
    public List<Lesson> findAllByCourseId(Long courseId) {
        return em.createQuery(
                "SELECT l FROM Lesson l WHERE l.course.courseId = :cid ORDER BY l.lessonOrderIndex ASC",
                Lesson.class
        ).setParameter("cid", courseId).getResultList();
    }

    /**
     * Student bez enrolementa vidi samo available + freePreview.
     */
    public List<Lesson> findVisibleForGuestStudent(Long courseId) {
        return em.createQuery(
                "SELECT l FROM Lesson l "
                + "WHERE l.course.courseId = :cid AND l.lessonAvailable = true AND l.freePreview = true "
                + "ORDER BY l.lessonOrderIndex ASC",
                Lesson.class
        ).setParameter("cid", courseId).getResultList();
    }

    /**
     * Student sa enrolementom vidi samo available (preview može biti true/false – svejedno).
     */
    public List<Lesson> findVisibleForEnrolledStudent(Long courseId) {
        return em.createQuery(
                "SELECT l FROM Lesson l "
                + "WHERE l.course.courseId = :cid AND l.lessonAvailable = true "
                + "ORDER BY l.lessonOrderIndex ASC",
                Lesson.class
        ).setParameter("cid", courseId).getResultList();
    }

    public Lesson findByIdWithMaterials(Long lessonId) throws Exception {
        var result = em.createQuery(
                "SELECT DISTINCT l FROM Lesson l LEFT JOIN FETCH l.materials m WHERE l.lessonId = :id",
                Lesson.class
        ).setParameter("id", lessonId).getResultList();
        if (result.isEmpty()) {
            throw new Exception("Lesson not found: " + lessonId);
        }
        return result.get(0);
    }

    public long countByCourseId(Long courseId) {
        return em.createQuery(
                "SELECT COUNT(l) FROM Lesson l WHERE l.course.courseId = :cid", Long.class
        ).setParameter("cid", courseId).getSingleResult();
    }

    public int getNextOrderIndexForCourse(Long courseId) {
        Integer next = em.createQuery(
                "SELECT COALESCE(MAX(l.lessonOrderIndex), 0) + 1 FROM Lesson l WHERE l.course.courseId = :cid",
                Integer.class
        ).setParameter("cid", courseId).getSingleResult();
        return next != null ? next : 1;
    }

    public List<Lesson> findAllByCourseIdPaged(Long courseId, int offset, int limit) {
        var q = em.createQuery(
                "SELECT l FROM Lesson l WHERE l.course.courseId = :cid ORDER BY l.lessonOrderIndex ASC",
                Lesson.class
        ).setParameter("cid", courseId);
        if (offset > 0) {
            q.setFirstResult(offset);
        }
        if (limit > 0) {
            q.setMaxResults(limit);
        }
        return q.getResultList();
    }

// LessonRepository.java
    public long countMaterialsByLessonId(Long lessonId) {
        return em.createQuery(
                "SELECT COUNT(m) FROM Material m WHERE m.lesson.lessonId = :lid", Long.class
        )
                .setParameter("lid", lessonId)
                .getSingleResult();
    }
}
