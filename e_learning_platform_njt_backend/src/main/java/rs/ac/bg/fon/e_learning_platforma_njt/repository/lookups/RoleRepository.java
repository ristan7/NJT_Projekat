//package rs.ac.bg.fon.e_learning_platforma_njt.repository.lookups;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.springframework.stereotype.Repository;
//import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.Role;
//
//import java.util.List;
//
//@Repository
//public class RoleRepository {
//
//    @PersistenceContext
//    private EntityManager em;
//
//    public Role findById(Long id) {
//        var list = em.createQuery(
//                "SELECT r FROM Role r WHERE r.roleId = :id", Role.class)
//                .setParameter("id", id)
//                .getResultList();
//        return list.isEmpty() ? null : list.get(0);
//    }
//
//    public Role findByRoleName(String name) {
//        var list = em.createQuery(
//                "SELECT r FROM Role r WHERE r.roleName = :name", Role.class)
//                .setParameter("name", name)
//                .getResultList();
//        return list.isEmpty() ? null : list.get(0);
//    }
//
//    public List<Role> findAll() {
//        return em.createQuery("SELECT r FROM Role r", Role.class).getResultList();
//    }
//}
