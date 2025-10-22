package rs.ac.bg.fon.e_learning_platforma_njt.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Notification;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.MyAppRepository;

@Repository
public class NotificationRepository implements MyAppRepository<Notification, Long> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Notification> findAll() {
        // najčešći UI prikaz: novije prvo
        return em.createQuery(
                "SELECT n FROM Notification n ORDER BY n.sentAt DESC", Notification.class)
                .getResultList();
    }

    @Override
    public Notification findById(Long id) throws Exception {
        Notification n = em.find(Notification.class, id);
        if (n == null) {
            throw new Exception("Notification not found: " + id);
        }
        return n;
    }

    @Override
    @Transactional
    public void save(Notification entity) {
        if (entity.getNotificationId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Notification n = em.find(Notification.class, id);
        if (n != null) {
            em.remove(n);
        }
    }

    /* -------------------- DODATE METODE ZA FRONT -------------------- */
    /**
     * Lista po korisniku, opciono samo nepročitane, sa limitom i sortom po datumu (DESC).
     */
    public List<Notification> findAllByUserFiltered(Long userId, boolean unreadOnly, int limit) {
        String jpql = "SELECT n FROM Notification n "
                + "WHERE n.user.userId = :uid "
                + (unreadOnly ? "AND n.read = false " : "")
                + "ORDER BY n.sentAt DESC";
        var q = em.createQuery(jpql, Notification.class)
                .setParameter("uid", userId);
        if (limit > 0) {
            q.setMaxResults(limit);
        }
        return q.getResultList();
    }

    /**
     * Broj nepročitanih za badge u navbaru.
     */
    public long countUnreadByUser(Long userId) {
        return em.createQuery(
                "SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :uid AND n.read = false",
                Long.class)
                .setParameter("uid", userId)
                .getSingleResult();
    }

    @Transactional
    public int markReadForUser(Long notificationId, Long userId) {
        return em.createQuery("""
            UPDATE Notification n
               SET n.read = true
             WHERE n.notificationId = :nid
               AND n.user.userId = :uid
               AND n.read = false
            """)
                .setParameter("nid", notificationId)
                .setParameter("uid", userId)
                .executeUpdate();
    }

    @Transactional
    public int markAllReadForUser(Long userId) {
        return em.createQuery("""
            UPDATE Notification n
               SET n.read = true
             WHERE n.user.userId = :uid
               AND n.read = false
            """)
                .setParameter("uid", userId)
                .executeUpdate();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*
package rs.ac.bg.fon.e_learning_platforma_njt.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Repository;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.impl.Notification;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.MyAppRepository;

/**
 *
 * @author mikir
 */
 /*
@Repository
public class NotificationRepository implements MyAppRepository<Notification, Long> {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Notification> findAll() {
        return entityManager.createQuery("SELECT n FROM Notification n", Notification.class).getResultList();
    }
    
    @Override
    public Notification findById(Long id) throws Exception {
        Notification notification = entityManager.find(Notification.class, id);
        if (notification == null) {
            throw new Exception("Notification is not found!!"); //not found Exception 
        }
        return notification;
    }
    
    @Override
    @Transactional
    public void save(Notification entity) {
        if (entity.getNotificationId() == null) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }
    }
    
    @Override
    @Transactional
    public void deleteById(Long id) {
        Notification notification = entityManager.find(Notification.class, id);
        if (notification != null) {
            entityManager.remove(notification);
        }
    }

}
 */
