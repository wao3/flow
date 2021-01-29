package me.wangao.community.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 封装分页的组件
 */
@Data
@Accessors(chain = true)
public class Page {
    // 当前页码
    private int current = 1;
    // 一页显示的上限
    private int limit = 10;
    // 数据总数，用于计算总页数
    private int rows;
    // 查询路径，复用分页链接
    private String path;

    public Page setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
        return this;
    }

    public Page setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
        return this;
    }

    public Page setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
        return this;
    }

    // 获取当前页的起始行
    public int getOffset() {
        return (current - 1) * limit;
    }

    // 获取总页数
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    // 获取页面显示的起始页码
    public int getFrom() {
        int from = current - 2;
        return Math.max(from, 1); // 如果小于 1 则返回 1
    }

    // 获取页面显示的终止页码
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return Math.min(total, to);
    }
}
