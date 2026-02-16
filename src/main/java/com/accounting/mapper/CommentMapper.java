package com.accounting.mapper;

import com.accounting.dto.CommentDTO;
import com.accounting.entity.Comment;
import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * MapStruct mapper for Comment entity and DTOs.
 * Provides compile-time safe mapping between entity and DTO representations.
 * Handles conversion between Date and LocalDateTime.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    /**
     * Maps Comment entity to CommentDTO
     * Converts enums to their string representations
     * @param comment the comment entity
     * @return CommentDTO representation
     */
    @Mapping(target = "contextType", source = "contextType")
    @Mapping(target = "commentType", source = "commentType")
    @Mapping(target = "date", source = "date")
    CommentDTO toDTO(Comment comment);

    /**
     * Maps CommentDTO to Comment entity
     * Converts string representations back to enums
     * @param dto the comment DTO
     * @return Comment entity
     */
    @Mapping(target = "commentId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "contextType", source = "contextType")
    @Mapping(target = "commentType", source = "commentType")
    @Mapping(target = "date", source = "date")
    Comment toEntity(CommentDTO dto);

    /**
     * Maps ContextType enum to String
     */
    default String mapContextType(ContextType contextType) {
        return contextType != null ? contextType.toString() : null;
    }

    /**
     * Maps String to ContextType enum
     */
    default ContextType mapContextType(String contextType) {
        return contextType != null ? ContextType.valueOf(contextType.toUpperCase()) : null;
    }

    /**
     * Maps CommentType enum to String
     */
    default String mapCommentType(CommentType commentType) {
        return commentType != null ? commentType.toString() : null;
    }

    /**
     * Maps String to CommentType enum
     */
    default CommentType mapCommentType(String commentType) {
        return commentType != null ? CommentType.valueOf(commentType.toUpperCase()) : null;
    }

    /**
     * Maps LocalDateTime to Date
     */
    default Date mapLocalDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Maps Date to LocalDateTime
     */
    default LocalDateTime mapDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
