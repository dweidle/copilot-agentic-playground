package de.weidle.copilotagenticplayground.greeting.adapter.in.web;

import de.weidle.copilotagenticplayground.greeting.domain.model.Greeting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GreetingWebMapper {

    GreetingWebMapper INSTANCE = Mappers.getMapper(GreetingWebMapper.class);

    @Mapping(source = "message", target = "message")
    GreetingResponse toResponse(Greeting greeting);
}
