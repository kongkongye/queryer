package com.kongkongye.backend.queryer.common;

import java.io.Serializable;

public class Paging implements Serializable {
	private static final long serialVersionUID = -5490150883179194968L;
	public static final Paging ONE = new Paging(1, 1);

	public Integer page = 1;
	public Integer pageSize = 10;

	public Paging(int page, int pageSize) {
		this.page = page;
		this.pageSize = pageSize;
	}

	public Paging() {
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		//防止恶意请求过大页面
		this.pageSize = Math.min(100000, pageSize);
	}
}
