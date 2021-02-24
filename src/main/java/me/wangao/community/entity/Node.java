package me.wangao.community.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Node {
    private int id;
    private String name;
    private String desc;
}
