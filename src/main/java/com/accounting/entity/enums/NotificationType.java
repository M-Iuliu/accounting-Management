package com.accounting.entity.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    URGENT("urgent"), ACTIVE("active"), HISTORY("history");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

}
