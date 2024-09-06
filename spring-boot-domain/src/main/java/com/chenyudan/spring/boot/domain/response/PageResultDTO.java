package com.chenyudan.spring.boot.domain.response;

import java.io.Serializable;
import java.util.List;

/**
 * Description: TODO
 *
 * @author chenyu
 * @since 2024/9/6 14:47
 */
public class PageResultDTO<T extends ResultDTO> implements Serializable {

    private static final long serialVersionUID = -5227579442869738781L;

    private final Long pageNo;
    private final Long pageSize;
    private final Long total;
    private final List<T> result;

    public PageResultDTO(Long pageNo, Long pageSize, Long total, List<T> result) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.total = total;
        this.result = result;
    }

    public Long getPageNo() {
        return pageNo;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public Long getTotal() {
        return total;
    }

    public List<T> getResult() {
        return result;
    }
}
