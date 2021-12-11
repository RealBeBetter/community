function like(btn, entityType, entityId, entityUserId, postId) {
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