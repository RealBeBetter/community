$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	// 发送 Ajax 请求之前，将 CSRF 令牌设置到请求的消息头中
	let token = $("meta[name='_csrf']").attr("content");
	let header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});

	let name = $("#recipient-name").val();
	let text = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":name,"content":text},
		function (data) {
			data = $.parseJSON(data);
			if (data.code === 0) {
				$("#hintBody").text("发送成功！");
			} else {
				$("#hintBody").text(data.message);
			}
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				location.reload();
			}, 2000);
		}
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}