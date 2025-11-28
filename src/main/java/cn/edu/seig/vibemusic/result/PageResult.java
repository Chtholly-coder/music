package cn.edu.seig.vibemusic.result;

import java.util.List;

/**
 * 分页结果类
 * 用于封装分页查询的返回数据
 * @param <T> 列表数据类型
 */
public class PageResult<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页数据列表
     */
    private List<T> records;

    // 构造方法
    public PageResult() {}

    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    // Getter和Setter方法
    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}


