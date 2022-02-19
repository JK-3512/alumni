layui.define(["mm", "jquery"], function (exports) {
  var $ = layui.$,
      mm = layui.mm;
  var menu = {
    init: function () {
      // $(".menu").on("click", function () {
      //   if ($(this).hasClass("on")) {
      //     $(this).removeClass("on");
      //     $(".header-down-nav").removeClass("layui-show");
      //   } else {
      //     $(this).addClass("on");
      //     $(".header-down-nav").addClass("layui-show");
      //   }
      // });
      // window.onresize = function () {
      //   var curwidth = document.documentElement.clientWidth;
      //   if (curwidth > 760) {
      //     $(".header-down-nav").removeClass("layui-show");
      //     $(".menu").removeClass("on");
      //   }
      // };
      // var count = $(".list-cont .cont").length;
      // $(".volume span").text(count);
      $(".op-list .like").on("click", function () {
        var oSpan = $(this).children("span");
        var num = parseInt($(oSpan).text());
        var off = $(this).attr("off");
        if (off) {
          //开始点赞了，然后点一下
          $(this).removeClass("active");
          off = true;
          $(oSpan).text(num - 1);
          $(this).attr("off", "");
        } else {
          //开始没点赞，然后点一下
          $(this).addClass("active");
          off = false;
          $(oSpan).text(num + 1);
          $(this).attr("off", "true");
        }
      });
    },
    off: function () {
      $(".off").on("click", function () {
        var off = $(this).attr("off");
        var chi = $(this).children("i");
        var text = $(this).children("span");
        var cont = $(this).parents(".item").siblings(".review-version");
        if (off) {
          $(text).text("展开");
          $(chi).attr("class", "layui-icon layui-icon-down");
          $(this).attr("off", "");
          $(cont).addClass("layui-hide");
        } else {
          $(text).text("收起");
          $(chi).attr("class", "layui-icon layui-icon-up");
          $(this).attr("off", "true");
          $(cont).removeClass("layui-hide");
        }
      });
    },
    comment_off: function () {
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
