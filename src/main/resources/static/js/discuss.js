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
                $(btn).children("b").text(data.likeStatus===1?"已赞":"赞");
            } else {
                alert(data.message);
            }
        }
    );
}