$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题内容
	let title = $("#recipient-name").val();
	let content = $("#message-text").val();
	let nodeId = $("#select-node").val();

	// 异步发送
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{ title, content, nodeId },
		function (data) {
			data = JSON.parse(data);
			// 在提示框中显示返回消息
			let hintModal = $("#hintModal");
			console.log(data);
			$("#hintBody").text(data.msg);
			hintModal.modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if (data.code === 0) {
					window.location.reload();
				}
			}, 2000);
		}
	)
}