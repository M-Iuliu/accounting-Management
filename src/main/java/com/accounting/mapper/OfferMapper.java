package com.accounting.mapper;

import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferPatchDTO;
import com.accounting.dto.offer.OfferShortDTO;
import com.accounting.entity.Offer;
import org.mapstruct.*;

/**
 * MapStruct mapper interface for Offer entity and DTOs.
 * Provides compile-time safe mapping between entity and DTO objects.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OfferMapper {

    /**
     * Maps Offer entity to OfferDTO
     * @param offer the source entity
     * @return the mapped DTO
     */
    @Mapping(target = "client", ignore = true)
    OfferDTO toDTO(Offer offer);

    /**
     * Maps Offer entity to OfferShortDTO
     * @param offer the source entity
     * @return the mapped short DTO
     */
    OfferShortDTO toShortDTO(Offer offer);

    /**
     * Maps OfferForm to new Offer entity
     * @param form the source form
     * @return new Offer entity
     */
    @Mapping(target = "offerId", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletionDate", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    Offer toEntity(OfferForm form);

    /**
     * Updates existing Offer entity from OfferPatchDTO
     * Only non-null fields from patch DTO will update the entity
     * @param patchDTO the source patch DTO with updates
     * @param offer the target entity to update
     */
    @Mapping(target = "offerId", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "offerDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletionDate", ignore = true)
    @Mapping(target = "obs", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    void updateEntityFromPatch(OfferPatchDTO patchDTO, @MappingTarget Offer offer);
}
