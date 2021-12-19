$(function () {
    /*设置在页面加载结束将按钮绑定单击事件*/
    if ($("#topBtn").text() === "置顶") {
        $("#topBtn").click(setTop);
    } else {
        $("#topBtn").click(cancelTop);
    }
    if ($("#wonderfulBtn").text() === "加精") {
        $("#wonderfulBtn").click(setWonderful);
    } else {
        $("#wonderfulBtn").click(cancelWonderful);
    }
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId, postId) {
    // 发送 Ajax 请求之前，将 CSRF 令牌设置到请求的消息头中
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $.post(
        /*访问路径*/
        CONTEXT_PATH + "/like",
        /*携带参数*/
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId, "postId": postId},
        /*回调函数*/
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus === 1 ? "已赞" : "赞");
            } else {
                alert(data.message);
            }
        }
    );
}

function setTop() {
    // 发送 Ajax 请求之前，将 CSRF 令牌设置到请求的消息头中
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#topBtn").text(data.type === 1 ? "取消置顶" : "置顶");
                location.reload();
                // 置顶之后将按钮设置不可点击
                // $("#topBtn").attr("disabled", "disabled");
            } else {
                alert(data.message);
            }
        }
    );
}

function cancelTop() {
    // 发送 Ajax 请求之前，将 CSRF 令牌设置到请求的消息头中
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/cancelTop",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#topBtn").text(data.type === 1 ? "取消置顶" : "置顶");
                location.reload();
                // 取消置顶之后将按钮设置不可点击
                // $("#topBtn").attr("disabled", "disabled");
            } else {
                alert(data.message);
            }
        }
    );

}

function setWonderful() {
    // 发送 Ajax 请求之前，将 CSRF 令牌设置到请求的消息头中
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#wonderfulBtn").text(data.status === 1 ? "取消加精" : "加精");
                location.reload();
                // 加精之后将按钮设置不可点击
                // $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.message);
            }
        }
    );
}

function cancelWonderful() {
    // 发送 Ajax 请求之前，将 CSRF 令牌设置到请求的消息头中
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/cancelWonderful",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                $("#wonderfulBtn").text(data.status === 1 ? "取消加精" : "加精");
                location.reload();
                // 取消加精之后将按钮设置不可点击
                // $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.message);
            }
        }
    );
}

function setDelete() {
    // 发送 Ajax 请求之前，将 CSRF 令牌设置到请求的消息头中
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id": $("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                // 删除之后跳转到首页
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.message);
            }
        }
    );
}

