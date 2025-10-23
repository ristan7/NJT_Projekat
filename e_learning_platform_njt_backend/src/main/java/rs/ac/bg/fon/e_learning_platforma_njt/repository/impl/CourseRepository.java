package rs.ac.bg.fon.e_learning_platforma_njt.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;

import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Course;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.MyAppRepository;

@Repository
public class CourseRepository implements MyAppRepository<Course, Long> {

    @PersistenceContext
    private EntityManager em;

    /* ===================== Basic CRUD (iz MyAppRepository) ===================== */
    @Override
    public List<Course> findAll() {
        return em.createQuery(
                "SELECT c FROM Course c ORDER BY c.createdAt DESC", Course.class)
                .getResultList();
    }

    @Override
    public Course findById(Long id) throws Exception {
        Course c = em.find(Course.class, id);
        if (c == null) {
            throw new Exception("Course not found: " + id);
        }
        return c;
    }

    @Override
    @Transactional
    public void save(Course entity) {
        if (entity.getCourseId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Course c = em.find(Course.class, id);
        if (c != null) {
            em.remove(c);
        }
    }

    /* ===================== Dodatne korisne metode (za sada) ===================== */
    /**
     * Kursevi jednog autora (Teacher), novi prvi.
     */
    public List<Course> findAllByAuthorId(Long authorId) {
        return em.createQuery(
                "SELECT c FROM Course c "
                + "WHERE c.author.userId = :uid "
                + "ORDER BY c.createdAt DESC", Course.class)
                .setParameter("uid", authorId)
                .getResultList();
    }

    /**
     * Kursevi po statusu (npr. PUBLISHED) – status je lookup, proslediti njegov ID.
     */
    public List<Course> findAllByStatusId(Long statusId) {
        return em.createQuery(
                "SELECT c FROM Course c "
                + "WHERE c.courseStatus.courseStatusId = :sid "
                + "ORDER BY c.createdAt DESC", Course.class)
                .setParameter("sid", statusId)
                .getResultList();
    }

    /**
     * Detalj kursa sa lekcijama (bez materijala). Oslanjamo se na @OrderBy u Course.lessons.
     */
    public Course findByIdWithLessons(Long id) throws Exception {
        List<Course> result = em.createQuery(
                "SELECT DISTINCT c FROM Course c "
                + "LEFT JOIN FETCH c.lessons l "
                + "WHERE c.courseId = :id", Course.class)
                .setParameter("id", id)
                .getResultList();

        if (result.isEmpty()) {
            throw new Exception("Course not found: " + id);
        }
        return result.get(0);
    }

    /**
     * Broj lekcija u kursu – efikasno za DTO (lessonCount) bez učitavanja liste.
     */
    public long countLessons(Long courseId) {
        return em.createQuery(
                "SELECT COUNT(l) FROM Lesson l WHERE l.course.courseId = :cid", Long.class)
                .setParameter("cid", courseId)
                .getSingleResult();
    }

    /**
     * Jednostavna pretraga kurseva po naslovu (case-insensitive, sadrži).
     */
    public List<Course> searchByTitle(String q) {
        return em.createQuery(
                "SELECT c FROM Course c "
                + "WHERE LOWER(c.courseTitle) LIKE LOWER(CONCAT('%', :q, '%')) "
                + "ORDER BY c.createdAt DESC", Course.class)
                .setParameter("q", q != null ? q.trim() : "")
                .getResultList();
    }
}
