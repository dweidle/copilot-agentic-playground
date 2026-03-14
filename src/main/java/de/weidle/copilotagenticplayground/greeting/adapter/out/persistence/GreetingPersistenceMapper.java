package de.weidle.copilotagenticplayground.greeting.adapter.out.persistence;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GreetingPersistenceMapper {

    GreetingPersistenceMapper INSTANCE = Mappers.getMapper(GreetingPersistenceMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    GreetingLogEntity toEntity(Greeting greeting);
}
