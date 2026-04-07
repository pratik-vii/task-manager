package com.taskmanager.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PagedData<T> {
	private List<T> data;
	private Integer currentPage;
	private Integer pageSize;
	private Integer maxPages;
	private Long totalCount;
}
