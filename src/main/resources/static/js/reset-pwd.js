// 发送邮箱验证码功能
function sendEmailCodeForResetPwd() {
    var code = ($('#code').val() || '').trim();
    var userName = ($('#userName').val() || '').trim();
    if (code && userName) {
        $.ajax({
            type: 'POST',
            url:"/alumni/user/sendEmailCodeForResetPwd",
            data: {
                userName: userName,
                kaptcha: code
            },
            success:function(result){
                if (result) {
                    if (result.status === '1') {
                        console.log('发送邮箱验证码失败');
                        alert(result.errMsg);
                    } else if (result.status === '0') {
                        console.log('发送邮箱验证码成功');
                        alert(result.msg);
                    } else {
                        console.log(result);
                    }
                }
            }
        });
    } else {
        console.log(code + ":" + userName)
        console.log('请输入完整的请求数据')
    }
}