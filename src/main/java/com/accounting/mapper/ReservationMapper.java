package com.accounting.mapper;

import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.dto.reservation.ReservationPatchDTO;
import com.accounting.dto.reservation.ReservationShortDTO;
import com.accounting.entity.Reservation;
import org.mapstruct.*;

/**
 * MapStruct mapper for Reservation entity and DTOs.
 * Provides compile-time safe mapping between entity and various DTO representations.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReservationMapper {

    /**
     * Maps Reservation entity to ReservationDTO
     * @param reservation the reservation entity
     * @return ReservationDTO representation
     */
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "childrenAge", ignore = true)
    @Mapping(target = "uploadedFiles", ignore = true)
    @Mapping(target = "commentList", ignore = true)
    @Mapping(target = "offerId", source = "offer.offerId")
    @Mapping(target = "balanceDueDate", source = "paymentDueDate")
    @Mapping(target = "transportType", source = "transport")
    ReservationDTO toDTO(Reservation reservation);

    /**
     * Maps Reservation entity to ReservationShortDTO
     * @param reservation the reservation entity
     * @return ReservationShortDTO representation
     */
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "personNo", ignore = true)
    @Mapping(target = "balanceDueDate", source = "paymentDueDate")
    ReservationShortDTO toShortDTO(Reservation reservation);

    /**
     * Maps ReservationForm to Reservation entity
     * @param form the reservation form
     * @return Reservation entity
     */
    @Mapping(target = "reservationId", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "offer", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "uploadedFiles", ignore = true)
    @Mapping(target = "bookingRef", ignore = true)
    @Mapping(target = "bookedDate", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletionDate", ignore = true)
    @Mapping(target = "price", source = "totalPrice")
    @Mapping(target = "receipted", source = "remainingCost")
    @Mapping(target = "paymentDueDate", source = "paymentDeadlineDate")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    Reservation toEntity(ReservationForm form);

    /**
     * Updates existing Reservation entity from ReservationPatchDTO
     * Only non-null fields from patch will update the entity
     * @param patchDTO the patch DTO with updates
     * @param reservation the reservation entity to update
     */
    @Mapping(target = "reservationId", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "offer", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "uploadedFiles", ignore = true)
    @Mapping(target = "bookingRef", ignore = true)
    @Mapping(target = "bookedDate", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletionDate", ignore = true)
    @Mapping(target = "paymentDueDate", source = "balanceDueDate")
    @Mapping(target = "transport", source = "transportType")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    void updateEntityFromPatch(ReservationPatchDTO patchDTO, @MappingTarget Reservation reservation);
}
