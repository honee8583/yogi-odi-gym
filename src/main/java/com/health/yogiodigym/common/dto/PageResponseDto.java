package com.health.yogiodigym.common.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PageResponseDto<T> {
    @Builder.Default
    private List<T> contents = new ArrayList<>();
    private int totalPage;
    private long totalElements;
    private boolean first;
    private boolean last;
    private int numberOfElements;

    public PageResponseDto(Page<?> page, List<T> dtos) {
        this.contents = dtos;
        this.totalPage = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.numberOfElements = page.getNumberOfElements();
    }
}
