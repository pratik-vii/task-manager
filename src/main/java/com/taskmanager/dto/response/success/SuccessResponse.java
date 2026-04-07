package com.taskmanager.dto.response.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.taskmanager.dto.response.PagedData;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Data
@JsonInclude(NON_NULL)
public class SuccessResponse<T> {
	@Setter(AccessLevel.PRIVATE)
	private boolean success;

	private String message;
	private T data;
	private Integer currentPage;
	private Integer pageSize;
	private Integer maxPages;
	private Long totalCount;

	public SuccessResponse() {
		this.success = true;
	}

	public static <T> SuccessResponse<List<T>> fromPagedData(PagedData<T> pagedData) {
		SuccessResponse<List<T>> response = new SuccessResponse<>();
		response.setCurrentPage(pagedData.getCurrentPage());
		response.setPageSize(pagedData.getPageSize());
		response.setMaxPages(pagedData.getMaxPages());
		response.setTotalCount(pagedData.getTotalCount());
		response.setData(pagedData.getData());
		return response;
	}
}
