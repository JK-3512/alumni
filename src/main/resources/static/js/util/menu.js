layui.define(["jquery"], function (exports) {
  var $ = layui.$;
  var menu = {
    comment_off: function () {
      $(".off").on("click", function () {
        var off = $(this).attr("off");
        var chi = $(this).children("i");
        var text = $(this).children("span");
        var cont = $(this).parents(".item").siblings(".review-version");
        if (off) {
          $(chi).attr("class", "layui-icon layui-icon-down");
          $(this).attr("off", "");
          $(cont).addClass("layui-hide");
        } else {
          $(chi).attr("class", "layui-icon layui-icon-up");
          $(this).attr("off", "true");
          $(cont).removeClass("layui-hide");
        }
      });
    },
    reply_off: function () {
      $(".edit").on("click", function () {
        var off = $(this).attr("off");
        var text = $(this).children("span");
        var cont = $(this).parents(".op-list").siblings(".comment");
        if (off) {
          $(text).text("回复");
          $(this).attr("off", "");
          $(cont).addClass("layui-hide");
        } else {
          $(text).text("取消回复");
          $(this).attr("off", "true");
          $(cont).removeClass("layui-hide");
        }
      });
    },
    enroll_off: function () {
      $("#enroll").on("click", function () {
        var off = $(this).attr("off");
        var cont = $(this).parent(".enroll-view").children(".enroll");
        if (off) {
          $(this).attr("off", "");
          $(cont).addClass("layui-hide");
        } else {
          $(this).attr("off", "true");
          $(cont).removeClass("layui-hide");
        }
      });
    }
  };
  exports("menu", menu);
});
