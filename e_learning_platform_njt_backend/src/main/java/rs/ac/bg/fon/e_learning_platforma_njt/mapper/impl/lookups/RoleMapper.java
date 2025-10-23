//package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.lookups;
//
//import org.springframework.stereotype.Component;
//import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.lookups.RoleDto;
//import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.Role;
//import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;
//
//@Component
//public class RoleMapper implements DtoEntityMapper<RoleDto, Role> {
//
//    @Override
//    public RoleDto toDto(Role e) {
//        if (e == null) {
//            return null;
//        }
//        return new RoleDto(e.getRoleId(), e.getRoleName());
//    }
//
//    @Override
//    public Role toEntity(RoleDto t) {
//        if (t == null) {
//            return null;
//        }
//        Role r = new Role();
//        r.setRoleId(t.getRoleId());     // dovoljan je ID za FK binding
//        r.setRoleName(t.getRoleName()); // opciono (ok je imati i name)
//        return r;
//    }
//}
