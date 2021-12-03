package com.nowcoder.community.entity;

/**
 * @author : Real
 * @date : 2021/11/13 0:47
 * @description : 封装分页相关组件
 */
public class Page {

    /**
     * 当前页面
     */
    private int current = 1;

    /**
     * 显示上限数量
     */
    private int limit = 10;

    /**
     * 数据总数，用于计算总页数
     */
    private int rows;

    /**
     * 查询路径，复用分页链接
     */
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        // 避免非法数据传进
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        // 设置合法的上限数据
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取当前页的起始行
     *
     * @return 起始行
     */
    public int getOffset() {
        // current * limit - limit
        return (current - 1) * limit;
    }

    /**
     * 页面上要显示总页码，计算出总页数
     *
     * @return 总页数
     */
    public int getTotal() {
        // rows / limit ，需要进一处理
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码，页面中的五个页码标签中最开始
     *
     * @return 开始页面
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结尾页码，页面下方页码标签的最末尾一个
     *
     * @return
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
