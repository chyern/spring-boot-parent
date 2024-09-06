package com.chenyudan.spring.boot.domain.request;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/9/6 14:51
 */
public class PageRequestDTO extends RequestDTO {

    private Long pageNo = 1L;
    private Long pageSize = 20L;

    public Long getPageNo() {
        return pageNo;
    }

    public void setPageNo(Long pageNo) {
        this.pageNo = pageNo;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
