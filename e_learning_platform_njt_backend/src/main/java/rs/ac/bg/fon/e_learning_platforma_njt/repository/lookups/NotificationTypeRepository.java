//package rs.ac.bg.fon.e_learning_platforma_njt.repository.lookups;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.springframework.stereotype.Repository;
//import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.NotificationType;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public class NotificationTypeRepository {
//
//    @PersistenceContext
//    private EntityManager em;
//
//    public List<NotificationType> findAll() {
//        return em.createQuery("SELECT t FROM NotificationType t ORDER BY t.notificationTypeName ASC", NotificationType.class)
//                .getResultList();
//    }
//}
