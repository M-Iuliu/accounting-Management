package com.accounting.mapper;

import com.accounting.dto.client.ClientAddEditForm;
import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.entity.Client;
import org.mapstruct.*;

/**
 * MapStruct mapper interface for Client entity and DTOs.
 * Provides compile-time safe mapping between entity and DTO objects.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {

    /**
     * Maps Client entity to ClientDTO
     * @param client the source entity
     * @return the mapped DTO
     */
    @Mapping(target = "offerList", ignore = true)
    @Mapping(target = "reservationList", ignore = true)
    ClientDTO toDTO(Client client);

    /**
     * Maps Client entity to ClientShortDTO
     * @param client the source entity
     * @return the mapped short DTO
     */
    @Mapping(target = "fullName", expression = "java(client.getFullName())")
    ClientShortDTO toShortDTO(Client client);

    /**
     * Maps ClientAddEditForm to new Client entity
     * @param form the source form
     * @return new Client entity
     */
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletionDate", ignore = true)
    @Mapping(target = "offerList", ignore = true)
    @Mapping(target = "reservationList", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    Client toEntity(ClientAddEditForm form);

    /**
     * Updates existing Client entity from ClientAddEditForm
     * Only non-null fields from form will update the entity
     * @param form the source form with updates
     * @param client the target entity to update
     */
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletionDate", ignore = true)
    @Mapping(target = "offerList", ignore = true)
    @Mapping(target = "reservationList", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    void updateEntityFromForm(ClientAddEditForm form, @MappingTarget Client client);
}
