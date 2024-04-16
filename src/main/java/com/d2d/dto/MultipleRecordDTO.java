package com.d2d.dto;

import java.util.ArrayList;
import java.util.List;

public class MultipleRecordDTO {

    private Boolean isChecked = false;

    private Boolean pagination = false;

    private Integer pageSize;

    private Boolean filter = false;

    private List<String> filteredFieldList;

    private Boolean sort = false;

    private String sortField;

    private Boolean isSearch = false;

    private List<String> searchFieldList;

    public List<String> getSearchFieldList() {
        return searchFieldList;
    }

    public void setSearchFieldList(List<String> searchFieldList) {
        this.searchFieldList = searchFieldList;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean checked) {
        isChecked = checked;
    }

    public Boolean getPagination() {
        return pagination;
    }

    public void setPagination(Boolean pagination) {
        this.pagination = pagination;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getFilter() {
        return filter;
    }

    public void setFilter(Boolean filter) {
        this.filter = filter;
    }

    public List<String> getFilteredFieldList() {
        if (filteredFieldList == null) {
            filteredFieldList = new ArrayList<>();
        }
        return filteredFieldList;
    }

    public void setFilteredFieldList(List<String> filteredFieldList) {
        this.filteredFieldList = filteredFieldList;
    }

    public Boolean getSort() {
        return sort;
    }

    public void setSort(Boolean sort) {
        this.sort = sort;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public Boolean getIsSearch() {
        return isSearch;
    }

    public void setIsSearch(Boolean search) {
        isSearch = search;
    }
}
