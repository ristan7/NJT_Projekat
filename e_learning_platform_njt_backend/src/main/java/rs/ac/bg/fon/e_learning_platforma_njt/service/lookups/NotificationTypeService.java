package rs.ac.bg.fon.e_learning_platforma_njt.service.lookups;

import org.springframework.stereotype.Service;
import rs.ac.bg.fon.e_learning_platforma_njt.dto.impl.lookups.NotificationTypeDto;
import rs.ac.bg.fon.e_learning_platforma_njt.repository.lookups.NotificationTypeRepository;

import java.util.List;
import java.util.stream.Collectors;
import rs.ac.bg.fon.e_learning_platforma_njt.mapper.impl.lookups.NotificationTypeMapper;

@Service
public class NotificationTypeService {

    private final NotificationTypeRepository repo;
    private final NotificationTypeMapper mapper;

    public NotificationTypeService(NotificationTypeRepository repo, NotificationTypeMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<NotificationTypeDto> findAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }
    
}
