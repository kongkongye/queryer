package com.kongkongye.backend.queryer.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description. 分页 - 保存分页相关信息
 * @modificationHistory.
 */
public class Pagination<T> implements Serializable {

	/**
	 * serialVersionUID:
	 *
	 * @since v 1.1
	 */

	private static final long serialVersionUID = 998765088140195917L;

	/**
	 * 分页数据列表
	 */
	private List<T> dataList;

	/**
	 * 分页号码列表
	 */
	@Deprecated
	private List<String> noList;

	/**
	 * 记录总数
	 */
	private int total;

	/**
	 * 页总数
	 */
	private int totalPage;

	/**
	 * 当前页
	 */
	private int currentPage;

	/**
	 * 单页记录数
	 */
	private int pageSize;

	/**
	 * 当前页开始记录
	 */
	private int offset;

	/**
	 * 默认单页记录数
	 */
	private static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * 默认当前页
	 */
	private static final int DEFAULT_CURRENT_PAGE = 1;

	//private static final int DEAULT_SHOW_PAGE_BEFORE = 3;
	//private static final int DEAULT_SHOW_PAGE_AFTER = 3;

	public static final int DEFAULT_CRITICAL_1 = 5;
	public static final int DEFAULT_CRITICAL_2 = 9;
	public static final int DEFAULT_CRITICAL_3 = 13;
	public static final String DEFAULT_ELIPSIS = "...";

	public Pagination() {
	}

	/**
	 * 构造函数
	 *
	 * @param _pageSize    单页记录数
	 * @param _currentPage 当前页
	 * @param _allRow      总记录数
	 */
	public Pagination(Integer _pageSize, Integer _currentPage, int _allRow) {
		this(_pageSize, _currentPage, _allRow, false);
	}

	/**
	 * 创建一个新的实例PagingDTO.
	 *
	 * @param _pageSize     单页记录数
	 * @param _currentPage  当前页
	 * @param _allRow       总记录数
	 * @param isBuildPageNo 是否生成页码
	 */
	public Pagination(Integer _pageSize, Integer _currentPage, int _allRow,
					  boolean isBuildPageNo) {

		// 若 _currentPage 或 _pageSize 为空，则分别给这两个参数设置默认值
		if (_currentPage == null || _pageSize == null) {
			_currentPage = DEFAULT_CURRENT_PAGE;
			_pageSize = DEFAULT_PAGE_SIZE;
		}
		// 若 _pageSize <=0 则设置为默认值
		if (_pageSize <= 0) {
			_pageSize = DEFAULT_PAGE_SIZE;
		}
		// 若 _currentPage <= 0 则设置为默认值
		if (_currentPage <= 0) {
			_currentPage = DEFAULT_CURRENT_PAGE;
		}

		this.pageSize = _pageSize;
		this.currentPage = _currentPage;
		this.total = _allRow;
		this.totalPage = (total - 1) / pageSize + 1;
		/*if (currentPage > totalPage) {
			currentPage = totalPage;
		}*/
		this.offset = pageSize * (currentPage - 1);

		if (isBuildPageNo) {
			buildPageNoList();
		}
	}


	/**
	 * @author liulj
	 * @creationDate. 2013年8月7日 上午1:51:05
	 * @description. 创建页码列表
	 */
	@Deprecated
	public void buildPageNoList() {
/*		noList = new ArrayList<String>();
		int page_num = DEAULT_SHOW_PAGE_BEFORE+1+DEAULT_SHOW_PAGE_AFTER;
		int _first = currentPage - DEAULT_SHOW_PAGE_BEFORE;
		int first = _first<1?1:_first;
		if(first>1){
			noList.add("...");
		}
		
		int _last = currentPage + DEAULT_SHOW_PAGE_AFTER;
		_last = _last<page_num?page_num:_last;
		
		int last = _last>totalPage?totalPage:_last;
		
		for(int i=first;i<=last;i++){
			noList.add(String.valueOf(i));
		}
		
		if(last<totalPage){
			noList.add("...");
		}*/
		noList = new ArrayList<String>();

		if (totalPage <= DEFAULT_CRITICAL_1) {

			for (int i = 1; i <= totalPage; i++) {

				noList.add(String.valueOf(i));
			}

		} else if (totalPage > DEFAULT_CRITICAL_1 && totalPage <= DEFAULT_CRITICAL_2) {

			if (currentPage < DEFAULT_CRITICAL_1) {

				for (int i = 1; i <= DEFAULT_CRITICAL_1; i++) {

					noList.add(String.valueOf(i));
				}
				noList.add(DEFAULT_ELIPSIS);
			} else {

				for (int i = 1; i <= totalPage; i++) {

					noList.add(String.valueOf(i));
				}
			}
		} else if (totalPage > DEFAULT_CRITICAL_2 && totalPage <= DEFAULT_CRITICAL_3) {

			if (currentPage < DEFAULT_CRITICAL_1) {

				for (int i = 1; i <= DEFAULT_CRITICAL_1; i++) {

					noList.add(String.valueOf(i));
				}
				noList.add(DEFAULT_ELIPSIS);
			} else if (currentPage >= DEFAULT_CRITICAL_1 && currentPage < DEFAULT_CRITICAL_2) {

				for (int i = 1; i <= DEFAULT_CRITICAL_2; i++) {

					noList.add(String.valueOf(i));
				}
				noList.add(DEFAULT_ELIPSIS);
			} else {

				noList.add(String.valueOf(1));
				noList.add(String.valueOf(2));
				noList.add(DEFAULT_ELIPSIS);
				for (int i = DEFAULT_CRITICAL_2; i <= totalPage; i++) {

					noList.add(String.valueOf(i));
				}
			}
		} else {

			if (currentPage < DEFAULT_CRITICAL_1) {

				for (int i = 1; i <= DEFAULT_CRITICAL_1; i++) {

					noList.add(String.valueOf(i));
				}
				noList.add(DEFAULT_ELIPSIS);
			} else if (currentPage >= DEFAULT_CRITICAL_1 && currentPage < DEFAULT_CRITICAL_2) {

				for (int i = 1; i <= DEFAULT_CRITICAL_2; i++) {

					noList.add(String.valueOf(i));
				}
				noList.add(DEFAULT_ELIPSIS);
			} else if (currentPage > totalPage - DEFAULT_CRITICAL_1) {
				noList.add(String.valueOf(1));
				noList.add(String.valueOf(2));
				noList.add(DEFAULT_ELIPSIS);
				for (int i = totalPage - DEFAULT_CRITICAL_1 + 1; i <= totalPage; i++) {

					noList.add(String.valueOf(i));
				}
			} else if (currentPage <= totalPage - DEFAULT_CRITICAL_1) {
				noList.add(String.valueOf(1));
				noList.add(String.valueOf(2));
				noList.add(DEFAULT_ELIPSIS);
				for (int i = currentPage; i < currentPage + DEFAULT_CRITICAL_1; i++) {

					noList.add(String.valueOf(i));
				}
				noList.add(DEFAULT_ELIPSIS);
			}
		}
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	@Deprecated
	public List<String> getNoList() {
		return noList;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	/**
	 * 对列表每项进行操作
	 *
	 * @return 返回自身
	 */
	public Pagination<T> peek(Consumer<T> consumer) {
		for (T t : dataList) {
			consumer.accept(t);
		}
		return this;
	}

	/**
	 * total为list的大小
	 *
	 * @see #of(int, int, List, int)
	 */
	public static <T> Pagination<T> of(int pageSize, int page, List<T> list) {
		return of(pageSize, page, list, list.size());
	}

	/**
	 * @param pageSize 分页大小
	 * @param page     当前页
	 * @param list     当前页元素
	 * @param total    总数
	 */
	public static <T> Pagination<T> of(int pageSize, int page, List<T> list, int total) {
		Pagination result = new Pagination<>(pageSize, page, total);
		result.setDataList(list);
		return result;
	}

	/**
	 * 映射一种分页为另一种分页
	 *
	 * @param <F>    源类型
	 * @param <T>    目标类型
	 * @param origin 源分页
	 * @param mapper 映射方法
	 * @return 目标分页
	 */
	public static <F, T> Pagination<T> map(Pagination<F> origin, Function<F, T> mapper) {
		return of(origin.getPageSize(), origin.getCurrentPage(), origin.getDataList().stream().map(mapper).collect(Collectors.toList()), origin.getTotal());
	}
}
