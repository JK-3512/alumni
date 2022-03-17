layui.use('dropdown', function () {
    var dropdown = layui.dropdown
    // 下拉菜单
    dropdown.render({
        elem: '.avatar' //可绑定在任意元素中，此处以上述按钮为例
        , data: [{
            title: '我的主页'
            , templet: '<i class="layui-icon layui-icon-username"></i> {{d.title}}'
            , href: '/alumni/user/my/oneself'
        }, { type: '-' }, {
            title: '我的收藏'
            , templet: '<i class="layui-icon layui-icon-star"></i> {{d.title}}'
            , href: '/alumni/user/my/oneself'
        }, { type: '-' }, {
            title: '我要发布'
            , templet: '<i class="layui-icon layui-icon-edit"></i> {{d.title}}'
            , href: '#'
            , child: [{
                title: '活动'
                , href: '/alumni/user/activity/publish'
            }, { type: '-' }, {
                title: '相册'
                , href: '/alumni/user/album/publish'
            }, { type: '-' }, {
                title: '帖子'
                , href: '/alumni/user/post/publish'
            }]
        }, { type: '-' }, {
            title: '退出'
            , templet: '<i class="layui-icon layui-icon-logout"></i> {{d.title}}'
            , href: '/alumni/user/logout'
        }]
    });
});


