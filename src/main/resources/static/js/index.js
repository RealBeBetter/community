$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 发送 Ajax 请求之前，将 CSRF 令牌设置到请求的消息头中
	let token = $("meta[name='_csrf']").attr("content");
	let header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function (e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});

	// 获取标题和正文
	let title = $("#recipient-name").val();
	let content = $("#message-text").val();
	// 发送异步请求， POST 请求
	$.post(
		CONTEXT_PATH + "/discuss/add",
		{"title": title, "content": content},
		function (data) {
			data = $.parseJSON(data);
			// 在提示框中显示提示消息
			$("#hintBody").text(data.message);
			// 显示提示框
			$("#hintModal").modal("show");
			// 显示提示框之后 2s 隐藏
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 刷新页面，判断是否成功
				if (data.code === 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);

}