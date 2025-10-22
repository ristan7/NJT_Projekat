package rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.lookups;

import org.springframework.stereotype.Component;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.lookups.NotificationTypeDto;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup.NotificationType;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.DtoEntityMapper;

@Component
public class NotificationTypeMapper implements DtoEntityMapper<NotificationTypeDto, NotificationType> {

    @Override
    public NotificationTypeDto toDto(NotificationType e) {
        if (e == null) {
            return null;
        }
        NotificationTypeDto dto = new NotificationTypeDto();
        dto.setId(e.getNotificationTypeId());
        dto.setName(e.getNotificationTypeName()); // ako se u entitetu zove getTypeName(), ovde promeni!
        return dto;
    }

    @Override
    public NotificationType toEntity(NotificationTypeDto t) {
        if (t == null) {
            return null;
        }
        NotificationType entity = new NotificationType();
        entity.setNotificationTypeId(t.getId());
        entity.setNotificationTypeName(t.getName());
        return entity;
    }

    public void apply(NotificationTypeDto dto, NotificationType entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setNotificationTypeName(dto.getName());
    }

}
