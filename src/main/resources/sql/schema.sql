create database if not exists flow;

use flow;

SET character_set_client = utf8mb4;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(50) NOT NULL,
    `salt` varchar(50) NOT NULL,
    `email` varchar(100) DEFAULT NULL,
    `type` int(11) DEFAULT NULL COMMENT '0-普通用户; 1-超级管理员; 2-版主;',
    `status` int(11) DEFAULT NULL COMMENT '0-未激活; 1-已激活;',
    `activation_code` varchar(100) DEFAULT NULL,
    `header_url` varchar(200) DEFAULT NULL,
    `create_time` timestamp NULL DEFAULT NOW(),
    PRIMARY KEY (`id`),
    KEY `index_username` (`username`(20)),
    KEY `index_email` (`email`(20)),
    KEY `create_time`(`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `from_id` int(11) DEFAULT NULL,
    `to_id` int(11) DEFAULT NULL,
    `conversation_id` varchar(45) NOT NULL,
    `content` text,
    `status` int(11) DEFAULT NULL COMMENT '0-未读;1-已读;2-删除;',
    `create_time` timestamp NULL DEFAULT NOW(),
    PRIMARY KEY (`id`),
    KEY `index_from_id` (`from_id`),
    KEY `index_to_id` (`to_id`),
    KEY `index_conversation_id` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL,
    `entity_type` int(11) DEFAULT NULL,
    `entity_id` int(11) DEFAULT NULL,
    `target_id` int(11) DEFAULT NULL,
    `content` text,
    `status` int(11) DEFAULT NULL,
    `create_time` timestamp NULL DEFAULT NOW(),
    PRIMARY KEY (`id`),
    KEY `index_user_id` (`user_id`) /*!80000 INVISIBLE */,
    KEY `index_entity_id` (`entity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `discuss_post`;
create table `discuss_post` (
    `id` int primary key not null auto_increment,
    `user_id` varchar(45) not null,
    `node_id` int not null default 1,
    `title` varchar(255) not null,
    `content` text,
    `type` int default 0 comment '0-普通; 1-置顶',
    `status` int default 0 comment '0-正常; 1-精华; 2-拉黑',
    `create_time` timestamp default NOW(),
    `comment_count` int default 0,
    `score` double default null,
    key `index_user_id` (`user_id`),
    key `node_id` (`node_id`),
    key `type_and_create_time`(`type`, `create_time`),
    KEY `create_time`(`create_time`)
)engine = InnoDB default charset = utf8;

DROP TABLE IF EXISTS `login_ticket`;
CREATE TABLE `login_ticket` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL,
    `ticket` varchar(45) NOT NULL,
    `status` int(11) DEFAULT '0' COMMENT '0-有效; 1-无效;',
    `expired` timestamp NOT NULL,
    PRIMARY KEY (`id`),
    KEY `index_ticket` (`ticket`(20))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `node`;
CREATE TABLE `node` (
    `id` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
    `name` varchar(32) not null unique comment '节点名',
    `desc` varchar(255) default null comment '节点描述',
    key idx_name(`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '节点';