package rs.ac.bg.fon.e_learning_platforma_njt.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Course;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.User;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.MyAppRepository;

/**
 * Repozitorijum za entitet User. Implementira MyAppRepository<User, Long> i sadrži dodatne metode koje se koriste u autentifikaciji (findByUsername, existsByEmail, ...).
 *
 * @author mikir
 */
@Repository
public class UserRepository implements MyAppRepository<User, Long> {

    @PersistenceContext
    private EntityManager em;

    // ================== STANDARD CRUD ==================
    @Override
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public User findById(Long id) throws Exception {
        User u = em.find(User.class, id);
        if (u == null) {
            throw new Exception("User not found");
        }
        return u;
    }

    @Transactional
    @Override
    public void save(User entity) {
        if (entity.getUserId() == null) {
            em.persist(entity); // kreiranje novog
        } else {
            em.merge(entity); // ažuriranje postojećeg
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        User u = em.find(User.class, id);
        if (u != null) {
            em.remove(u);
        }
    }

    // ================== CUSTOM METODE ==================
    /**
     * Pronalazi korisnika po username-u (koristi se pri autentifikaciji).
     */
    public User findByUsername(String username) {
        List<User> list = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :un", User.class)
                .setParameter("un", username)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Proverava da li već postoji korisnik sa zadatim username-om.
     */
    public boolean existsByUsername(String username) {
        Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :un", Long.class)
                .setParameter("un", username)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Proverava da li već postoji korisnik sa zadatim email-om.
     */
    public boolean existsByEmail(String email) {
        Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :em", Long.class)
                .setParameter("em", email)
                .getSingleResult();
        return count > 0;
    }

    /**
     * Pronalazi korisnika po email adresi.
     */
    public User findByEmail(String email) {
        List<User> list = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :em", User.class)
                .setParameter("em", email)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public User findByUsernameWithRole(String username) {
        List<User> list = em.createQuery(
                "SELECT u FROM User u "
                + "LEFT JOIN FETCH u.role "
                + "WHERE u.username = :un", User.class)
                .setParameter("un", username)
                .getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> findAllByRoleId(Long roleId) {
        return em.createQuery(
                "SELECT u FROM User u WHERE u.role.roleId = :rid", User.class
        ).setParameter("rid", roleId).getResultList();
    }

    public List<User> findAllByRoleName(String roleName) {
        return em.createQuery(
                "SELECT u FROM User u WHERE u.role.roleName = :rn", User.class
        ).setParameter("rn", roleName).getResultList();
    }

    public List<Course> findAllPaged(int offset, int limit) {
        var q = em.createQuery(
                "SELECT c FROM Course c ORDER BY c.createdAt DESC", Course.class);
        if (offset > 0) {
            q.setFirstResult(offset);
        }
        if (limit > 0) {
            q.setMaxResults(limit);
        }
        return q.getResultList();
    }

    /**
     * Courses authored by teacher, with optional status filter and paging
     */
    public List<Course> findAllByAuthorAndStatus(Long authorId, Long statusId, int offset, int limit) {
        String jpql = """
        SELECT c FROM Course c
        WHERE c.author.userId = :uid
          %s
        ORDER BY c.createdAt DESC
        """.formatted(statusId != null ? "AND c.courseStatus.courseStatusId = :sid" : "");
        var q = em.createQuery(jpql, Course.class).setParameter("uid", authorId);
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

}
