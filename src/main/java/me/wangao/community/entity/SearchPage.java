package me.wangao.community.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * elasticsearch 搜索结果分页实体
 * @param <T>
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SearchPage<T> {

    private int current; // 当前页
    private int limit; // 每页的数量
    private long total; // 搜索结果总数
    private List<T> list; // 搜索到的结果

}
