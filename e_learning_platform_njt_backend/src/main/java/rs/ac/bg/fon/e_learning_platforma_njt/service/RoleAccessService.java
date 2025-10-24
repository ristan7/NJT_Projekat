package rs.ac.bg.fon.e_learning_platforma_njt.service;

public interface RoleAccessService {

    boolean isAdmin(String roleName);

    boolean isTeacher(String roleName);

    boolean isStudent(String roleName);
}
