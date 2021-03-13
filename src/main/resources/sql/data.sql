--
-- Dumping data for table `comment`
--

# LOCK TABLES `comment` WRITE;
# /*!40000 ALTER TABLE `comment`
#     DISABLE KEYS */;
# INSERT INTO `comment`
# VALUES ();
# /*!40000 ALTER TABLE `comment`
#     ENABLE KEYS */;
# UNLOCK TABLES;

--
-- Dumping data for table `discuss_post`
--

LOCK TABLES `discuss_post` WRITE;
/*!40000 ALTER TABLE `discuss_post`
    DISABLE KEYS */;
INSERT INTO `discuss_post`(user_id, title, content, type, status, create_time, comment_count, score, node_id)
VALUES (3, '欢迎来到 flow！', '这是 Flow 的第一篇帖子！', 0, 0, now(), 0, 0, 1);
/*!40000 ALTER TABLE `discuss_post`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `message`
--

# LOCK TABLES `message` WRITE;
# /*!40000 ALTER TABLE `message`
#     DISABLE KEYS */;
#
# /*!40000 ALTER TABLE `message`
#     ENABLE KEYS */;
# UNLOCK TABLES;


--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user`
    DISABLE KEYS */;
INSERT INTO `user`(id, username, password, salt, email, type, status, activation_code, header_url, create_time)
VALUES (1, 'SYSTEM', 'SYSTEM', 'SYSTEM', 'system@sina.com', 0, 1, NULL, 'http://flow-img.waoyun.top/notify.png',
        '2021-02-13 02:11:03'),
       (2, 'su', '57ee1345597f3bb1d50054c299cca0f7', 'su', 'su@flow.com', 0, 1, NULL, 'http://flow-img.waoyun.top/avatar/0.svg',
        '2021-02-13 02:11:03'),
       (3, 'admin', 'f6fdffe48c908deb0f4c3bd36c032e72', 'admin', 'admin@flow.com', 0, 1, NULL, 'http://flow-img.waoyun.top/avatar/1.svg',
        '2021-02-13 02:11:03');
/*!40000 ALTER TABLE `user`
    ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `node` WRITE;
/*!40000 ALTER TABLE `node`
    DISABLE KEYS */;
insert into node(`name`, `desc`)
values ('热点杂谈', '可发起热点事件、话题进行讨论，严禁讨论政治主题。'),
       ('分享创造', '欢迎你在这里发布自己的最新作品！'),
       ('奇思妙想', '让你的创意在这里自由流动吧。'),
       ('项目相关', '在这里发布关于 Flow 相关的内容。');
/*!40000 ALTER TABLE `node`
    ENABLE KEYS */;
UNLOCK TABLES;