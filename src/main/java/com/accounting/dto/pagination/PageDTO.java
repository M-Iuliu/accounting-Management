package com.accounting.dto.pagination;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageDTO<T> {
    private List<T> items;
    private PaginationDTO pagination;
}
