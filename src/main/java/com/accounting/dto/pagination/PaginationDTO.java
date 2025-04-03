package com.accounting.dto.pagination;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaginationDTO {
    private long length;
    private int pageSize;
    private List<Integer> pageSizeOptions;
    private int pageIndex;
}
