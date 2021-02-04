$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	let btn = this;
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.post(CONTEXT_PATH + "/follow", { entityType: 3, entityId: $("#entityId").val() }, data => {
			data = JSON.parse(data);
			if (data.code === 0) {
				location.reload();
			} else {
				alert(data.msg);
			}
		})
	} else {
		// 取消关注
		$.post(CONTEXT_PATH + "/unfollow", { entityType: 3, entityId: $("#entityId").val() }, data => {
			data = JSON.parse(data);
			if (data.code === 0) {
				location.reload();
			} else {
				alert(data.msg);
			}
		})
	}
}