$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	let toName = $("#recipient-name").val();
	let content = $("#message-text").val();
	$.post(CONTEXT_PATH + "/letter/send", {toName, content}, function (data) {
		data = JSON.parse(data);
		$("#hintBody").text(data.msg);

		$("#hintModal").modal("show");
		setTimeout(function(){
			$("#hintModal").modal("hide");
			location.reload();
		}, 2000);
	})


}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}