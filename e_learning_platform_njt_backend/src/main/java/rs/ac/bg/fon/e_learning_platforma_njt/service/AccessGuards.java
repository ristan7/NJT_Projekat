package rs.ac.bg.fon.e_learning_platforma_njt.security;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import rs.ac.bg.fon.e_learning_platforma_njt.service.CourseService;
import rs.ac.bg.fon.e_learning_platforma_njt.service.LessonService;

/**
 * Centralni guard za uloge i (privremeno) plaćanje. Implementira interfejse koje traže CourseService i LessonService.
 *
 * Napomena: - Metode primaju roleName kao argument (tako su definisane u servisima), pa ovde ne čitamo SecurityContext – odluka o roli dolazi iz kontrolera/servisa. - hasPaid() je privremeni stub i uvek vraća true dok ne povežemo payment/enrollment sloj.
 */
@Service
@Primary
public class AccessGuards
        implements CourseService.RoleAccessService,
        LessonService.RoleAccessService,
        LessonService.PaymentAccessService {

    /* ================== RoleAccessService (Course & Lesson) ================== */
    @Override
    public boolean isAdmin(String roleName) {
        return equalsRole(roleName, "ADMIN");
    }

    @Override
    public boolean isTeacher(String roleName) {
        return equalsRole(roleName, "TEACHER");
    }

    @Override
    public boolean isStudent(String roleName) {
        return equalsRole(roleName, "STUDENT");
    }

    private boolean equalsRole(String roleName, String expected) {
        if (roleName == null) {
            return false;
        }
        // prihvata i "ADMIN" i "ROLE_ADMIN" varijante
        String r = roleName.trim().toUpperCase();
        String e = expected.trim().toUpperCase();
        return r.equals(e) || r.equals("ROLE_" + e);
    }

    /* ================== PaymentAccessService (Lesson) ================== */
    /**
     * TODO: zameniti stvarnom proverom (enrollment/payment). Dok ne integrišemo plaćanja, vraća true da bi development tekao bez blokade.
     */
    @Override
    public boolean hasPaid(Long userId, Long courseId) {
        return true;
    }
    
}
