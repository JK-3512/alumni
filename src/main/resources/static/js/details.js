// 点赞
function like(btn, targetType, targetId) {
    $.post(
        "/alumni/user/like",
        {"targetType":targetType, "targetId":targetId},
        function(res) {
            //点赞成功
            res = $.parseJSON(res);
            if (res.code === 0) {
                $(btn).children("span").text(res.data.likeCount);
                //当前用户点赞/取消点赞过，都需要修改样式
                if (res.data.likeStatus === 1){
                    //点赞
                    $(btn).addClass("active");
                }else{
                    //取消点赞
                    $(btn).removeClass("active");
                }
            }
            else {
                alert(res.data.msg);
            }

        }
    )
}
//收藏
function collect(btn, targetType, targetId) {
    $.post(
        "/alumni/user/collect",
        {"targetType":targetType, "targetId":targetId},
        function(res) {
            res = $.parseJSON(res);
            //收藏成功
            if (res.code === 0) {
                $(btn).children("span").text(res.data.collectCount);
                //当前用户收藏/取消收藏过，都需要修改样式
                if (res.data.collectStatus === 1){
                    $(btn).addClass("active");
                }else{
                    $(btn).removeClass("active");
                }
            }
            else {
                alert(res.msg);
            }

        }
    )
}

//相册列表，点击其中一个跳转到详情页面
function getDetails(id) {
    $(location).attr('href', '/alumni/user/album/details/'+id);
}