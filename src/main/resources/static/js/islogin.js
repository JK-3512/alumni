$(function () {
    $.ajax({
        url: '/alumni/user/isLogin',
        type: 'get',
        dataType: 'json',
        success: (res) => {
            if (res.code === 0){
                //已登录，登录按钮隐藏,设置用户头像
                $(".avatar").css("display","block");
                $(".login").css("display","none");
                $(".avatar img").attr("src", res.data.headPic);
            }else{
                //未登录，用户头像隐藏
                $(".avatar").css("display","none");
                $(".login").css("display","block");
            }
        }
    });
});