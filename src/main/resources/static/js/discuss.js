function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(CONTEXT_PATH + "/like", { entityType, entityId, entityUserId, postId }, function (data) {
        data = JSON.parse(data);
        if (data.code === 0) {
            $(btn).children("i").text(data.likeCount);
            $(btn).children("b").text(data.likeStatus === 1 ? "已赞" : "赞");
        } else {
            alert(data.msg)
        }
    })
}