// 点赞
function like(btn, targetType, targetId) {
    $.post(
        "/alumni/user/like",
        {"targetType":targetType, "targetId":targetId},
        function(data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                //点赞成功
                $(btn).children("span").text(data.likeCount);
                //当前用户点赞过了，需要添加样式
                if (data.likeStatus === 1){
                    $(btn).addClass("active");
                }else{
                    $(btn).removeClass("active");
                }
            }
            else {
                alert(data.msg);
            }

        }
    )
}
//收藏
function collect(btn, targetType, targetId) {
    $.post(
        "/alumni/user/collect",
        {"targetType":targetType, "targetId":targetId},
        function(data) {
            data = $.parseJSON(data);
            if (data.code === 0) {
                //点赞成功
                $(btn).children("span").text(data.collectCount);
                //当前用户点赞过了，需要添加样式
                if (data.collectStatus === 1){
                    $(btn).addClass("active");
                }else{
                    $(btn).removeClass("active");
                }
            }
            else {
                alert(data.msg);
            }

        }
    )
}