package me.wangao.community.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String conversationId;
    private String content;
    private Integer status;
    private Date createTime;
}
