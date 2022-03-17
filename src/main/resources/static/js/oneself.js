layui.define(["jquery"], function (exports) {
    var $ = layui.$;
    var oneself = {
        /* 点击按钮，下拉菜单在 显示/隐藏 之间切换 */
        more: function () {
            $(".icon").on("click", function () {
                var off = $(this).attr("off");
                var content = $(this).parents(".dropdown").children(".dropdown-content");
                if (off) {
                    $(this).attr("off", "");
                    $(content).removeClass("show");
                } else {
                    $(this).attr("off", "true");

                    $(content).addClass("show");
                }
            });
            // 点击下拉菜单意外区域隐藏
            window.onclick = function (event) {
                if (!event.target.matches('.icon')) {
                    var dropdowns = document.getElementsByClassName("dropdown-content");
                    var i;
                    for (i = 0; i < dropdowns.length; i++) {
                        var openDropdown = dropdowns[i];
                        if (openDropdown.classList.contains('show')) {
                            openDropdown.classList.remove('show');
                        }
                    }
                }
            }
        }

    };
    exports("oneself", oneself);
});