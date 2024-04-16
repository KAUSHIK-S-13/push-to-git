package com.d2d.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PageResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private long totalRecordCount;
    boolean hasNext;
    boolean hasPrevious;
    private T data;

}
