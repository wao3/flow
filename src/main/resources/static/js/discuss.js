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

$(function (){

    $("#topBtn").click(postCtl("top"));
    $("#wonderfulBtn").click(postCtl("wonderful"));
    $("#deleteBtn").click(postCtl("delete"));

    // 对帖子进行操作
    function postCtl(operation) {
        let url = CONTEXT_PATH + "/discuss/" + operation;
        let postId = $("#postId").data("id");
        return function () {
            $.post(url, {id: postId}, (data) => {
                data = JSON.parse(data);
                if (data.code === 0) {
                    $("#" + operation + "Btn").attr("disabled", "disabled");
                    if (operation === "delete") {
                        location.href = CONTEXT_PATH + "/index";
                    }
                } else {
                    alert(data.msg);
                }
            })
        };
    }
});