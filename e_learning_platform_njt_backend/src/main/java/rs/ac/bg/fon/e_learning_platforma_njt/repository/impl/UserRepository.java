package rs.ac.bg.fon.e_learning_platforma_njt.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
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

}
