package rs.ac.bg.fon.e_learning_platforma_njt.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Enrollment;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.MyAppRepository;

@Repository
public class EnrollmentRepository implements MyAppRepository<Enrollment, Long> {

    @PersistenceContext
    private EntityManager em;

    /* ===================== Basic CRUD ===================== */
    @Override
    public List<Enrollment> findAll() {
        return em.createQuery(
                "SELECT e FROM Enrollment e ORDER BY e.enrolledAt DESC",
                Enrollment.class
        ).getResultList();
    }

    @Override
    public Enrollment findById(Long id) throws Exception {
        Enrollment e = em.find(Enrollment.class, id);
        if (e == null) {
            throw new Exception("Enrollment not found: " + id);
        }
        return e;
    }

    @Override
    @Transactional
    public void save(Enrollment entity) {
        if (entity.getEnrollmentId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Enrollment e = em.find(Enrollment.class, id);
        if (e != null) {
            em.remove(e);
        }
    }

    /* ===================== Queries for common use-cases ===================== */
    /**
     * All enrollments for a student (newest first)
     */
    public List<Enrollment> findAllByStudentId(Long studentId) {
        return em.createQuery("""
                    SELECT e FROM Enrollment e
                    WHERE e.student.userId = :sid
                    ORDER BY e.enrolledAt DESC
                """, Enrollment.class)
                .setParameter("sid", studentId)
                .getResultList();
    }

    /**
     * All enrollments for a course (newest first)
     */
    public List<Enrollment> findAllByCourseId(Long courseId) {
        return em.createQuery("""
                    SELECT e FROM Enrollment e
                    WHERE e.course.courseId = :cid
                    ORDER BY e.enrolledAt DESC
                """, Enrollment.class)
                .setParameter("cid", courseId)
                .getResultList();
    }

    /**
     * Enrollments visible to teacher (across teacher's courses), optional status filter + pagination
     */
    public List<Enrollment> findAllForTeacher(Long teacherId, Long statusId, int offset, int limit) {
        String jpql = """
            SELECT e FROM Enrollment e
              JOIN e.course c
            WHERE c.author.userId = :tid
              %s
            ORDER BY e.enrolledAt DESC
        """.formatted(statusId != null ? "AND e.status.enrollmentStatusId = :sid" : "");

        var q = em.createQuery(jpql, Enrollment.class)
                .setParameter("tid", teacherId);
        if (statusId != null) {
            q.setParameter("sid", statusId);
        }
        if (offset > 0) {
            q.setFirstResult(offset);
        }
        if (limit > 0) {
            q.setMaxResults(limit);
        }
        return q.getResultList();
    }

    /**
     * Fetch for DTO: load student + course to avoid N+1 on mapping layer
     */
    public Enrollment findByIdWithCourseAndStudent(Long id) throws Exception {
        List<Enrollment> res = em.createQuery("""
                SELECT e FROM Enrollment e
                  JOIN FETCH e.student s
                  JOIN FETCH e.course c
                WHERE e.enrollmentId = :id
                """, Enrollment.class)
                .setParameter("id", id)
                .getResultList();
        if (res.isEmpty()) {
            throw new Exception("Enrollment not found: " + id);
        }
        return res.get(0);
    }

    /**
     * Single enrollment for (student, course) if exists
     */
    public Enrollment findByStudentAndCourse(Long studentId, Long courseId) {
        try {
            return em.createQuery("""
                    SELECT e FROM Enrollment e
                    WHERE e.student.userId = :sid
                      AND e.course.courseId = :cid
                    """, Enrollment.class)
                    .setParameter("sid", studentId)
                    .setParameter("cid", courseId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    /**
     * Does ACTIVE enrollment exist for (student, course)
     */
    public boolean existsActive(Long studentId, Long courseId, Long activeStatusId) {
        Long cnt = em.createQuery("""
                SELECT COUNT(e) FROM Enrollment e
                WHERE e.student.userId = :sid
                  AND e.course.courseId = :cid
                  AND e.status.enrollmentStatusId = :aid
                """, Long.class)
                .setParameter("sid", studentId)
                .setParameter("cid", courseId)
                .setParameter("aid", activeStatusId)
                .getSingleResult();
        return cnt != 0L;
    }

    /**
     * Count ACTIVE enrollments for a course (useful for stats on course card)
     */
    public long countActiveByCourse(Long courseId, Long activeStatusId) {
        return em.createQuery("""
                SELECT COUNT(e) FROM Enrollment e
                WHERE e.course.courseId = :cid
                  AND e.status.enrollmentStatusId = :aid
                """, Long.class)
                .setParameter("cid", courseId)
                .setParameter("aid", activeStatusId)
                .getSingleResult();
    }

    /**
     * Count by user + status
     */
    public long countByStudentAndStatus(Long studentId, Long statusId) {
        return em.createQuery("""
                SELECT COUNT(e) FROM Enrollment e
                WHERE e.student.userId = :sid
                  AND e.status.enrollmentStatusId = :stid
                """, Long.class)
                .setParameter("sid", studentId)
                .setParameter("stid", statusId)
                .getSingleResult();
    }

    /**
     * Free-text search (teacher scope) over course title or student username, with optional status filter
     */
    public List<Enrollment> searchForTeacher(Long teacherId, String q, Long statusId, int offset, int limit) {
        String like = q == null ? "" : q.trim().toLowerCase();
        String jpql = """
            SELECT e FROM Enrollment e
              JOIN e.course c
              JOIN e.student s
            WHERE c.author.userId = :tid
              AND (
                   :q = '' OR
                   LOWER(c.courseTitle) LIKE CONCAT('%', :q, '%') OR
                   LOWER(s.username)    LIKE CONCAT('%', :q, '%')
              )
              %s
            ORDER BY e.enrolledAt DESC
        """.formatted(statusId != null ? "AND e.status.enrollmentStatusId = :sid" : "");

        var query = em.createQuery(jpql, Enrollment.class)
                .setParameter("tid", teacherId)
                .setParameter("q", like);
        if (statusId != null) {
            query.setParameter("sid", statusId);
        }
        if (offset > 0) {
            query.setFirstResult(offset);
        }
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    /* ===================== Mutations ===================== */
    /**
     * Generic status change; timestamps are up to caller (service layer decides)
     */
    @Transactional
    public int updateStatus(Long enrollmentId, Long statusId, LocalDateTime completedAt, LocalDateTime cancelledAt) {
        return em.createQuery("""
                UPDATE Enrollment e
                   SET e.status.enrollmentStatusId = :sid,
                       e.completedAt = :cat,
                       e.cancelledAt = :xat
                 WHERE e.enrollmentId = :eid
                """)
                .setParameter("sid", statusId)
                .setParameter("cat", completedAt)
                .setParameter("xat", cancelledAt)
                .setParameter("eid", enrollmentId)
                .executeUpdate();
    }

    /**
     * Convenience: mark as COMPLETED (sets completedAt=now, clears cancelledAt)
     */
    @Transactional
    public int markCompleted(Long enrollmentId, Long completedStatusId, LocalDateTime now) {
        return updateStatus(enrollmentId, completedStatusId, now, null);
    }

    /**
     * Convenience: mark as CANCELLED (sets cancelledAt=now, clears completedAt)
     */
    @Transactional
    public int markCancelled(Long enrollmentId, Long cancelledStatusId, LocalDateTime now) {
        return updateStatus(enrollmentId, cancelledStatusId, null, now);
    }

    /* ===================== Pagination helpers ===================== */
    public List<Enrollment> findAllPaged(int offset, int limit) {
        var q = em.createQuery(
                "SELECT e FROM Enrollment e ORDER BY e.enrolledAt DESC",
                Enrollment.class
        );
        if (offset > 0) {
            q.setFirstResult(offset);
        }
        if (limit > 0) {
            q.setMaxResults(limit);
        }
        return q.getResultList();
    }

    /**
     * Student scope with optional status + paging
     */
    public List<Enrollment> findAllByStudentFiltered(Long studentId, Long statusId, int offset, int limit) {
        String jpql = """
            SELECT e FROM Enrollment e
            WHERE e.student.userId = :sid
              %s
            ORDER BY e.enrolledAt DESC
        """.formatted(statusId != null ? "AND e.status.enrollmentStatusId = :stid" : "");
        var q = em.createQuery(jpql, Enrollment.class).setParameter("sid", studentId);
        if (statusId != null) {
            q.setParameter("stid", statusId);
        }
        if (offset > 0) {
            q.setFirstResult(offset);
        }
        if (limit > 0) {
            q.setMaxResults(limit);
        }
        return q.getResultList();
    }
}
