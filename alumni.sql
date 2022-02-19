create table t_activity_enroll
(
    activity_id int  not null comment '活动ID',
    alumni_id   int  not null comment '校友ID',
    enroll_time date not null comment '报名时间'
)
    comment '活动登记表';

create table t_admin
(
    id        int auto_increment
        primary key,
    user_name varchar(16)          not null comment '用户名',
    pwd       varchar(32)          null comment '密码(加密后)',
    state     tinyint(1) default 1 not null comment '状态(0.冻结 1.启用)'
)
    comment '管理员表';

create table t_album_pic
(
    album_id int          not null comment '相册表ID',
    picture  varchar(255) not null comment '图片路径'
)
    comment '图片表';

create table t_collect
(
    user_id     int     not null,
    target_type tinyint not null,
    target_id   tinyint null
)
    comment '收藏表';

create table t_comment
(
    id           int auto_increment
        primary key,
    target_type  tinyint      not null comment '目标类型:具体哪个业务',
    target_id    tinyint      not null comment '目标ID:具体哪个业务对应ID',
    content      varchar(255) not null comment '评论内容',
    from_user_id int          not null comment '评论用户ID',
    to_user_id   int          null comment '评论目标用户id(存在是回复,不存在是评论)',
    create_time  datetime     not null comment '创建时间'
)
    comment '评论回复表';

create table t_news
(
    id          int auto_increment
        primary key,
    sort        tinyint           not null comment '类别',
    title       varchar(32)       null comment '标题',
    cover       varchar(255)      not null comment '封面',
    content     longtext          not null comment '内容(富文本)',
    star        int     default 0 not null comment '点赞',
    collect     int     default 0 not null comment '收藏',
    create_time date              not null comment '创建时间',
    state       tinyint default 1 null comment '状态(0.待审核 1.审核通过 2.审核不通过)',
    admin_id    int               not null comment '管理员ID',
    constraint t_news_t_admin_id_fk
        foreign key (admin_id) references t_admin (id)
)
    comment '新闻资讯表';

create table t_user
(
    id           int auto_increment
        primary key,
    sno          int                  not null comment '学工号',
    pwd          varchar(32)          not null comment '密码(加密后)',
    user_name    varchar(16)          null comment '用户名',
    sex          tinyint              null comment '性别(0.男 1.女)',
    head_pic     varchar(255)         null comment '头像地址',
    birthday     date                 null comment '出生日期',
    native_place varchar(16)          null comment '籍贯(至市级)',
    major        varchar(32)          null comment '专业',
    school_date  varchar(10)          null comment '在校时间(年范围)',
    education    varchar(5)           null comment '学历',
    email        varchar(64)          null comment '邮箱',
    phone        int                  null comment '手机',
    wechat       varchar(32)          null comment '微信',
    qq           int                  null comment 'QQ',
    state        tinyint(1) default 1 null comment '状态(0.冻结 1.启用)'
)
    comment '校友表';

create table t_activity
(
    id            int auto_increment
        primary key,
    sort          tinyint           not null comment '分类',
    cover         varchar(255)      not null comment '封面',
    title         varchar(32)       not null comment '标题',
    activity_date varchar(16)       not null comment '活动日期(范围)',
    contact       varchar(32)       not null comment '联系方式',
    close_date    date              not null comment '报名截止日期',
    number_limit  int               not null comment '人数上限',
    region        varchar(64)       not null comment '活动地点(至区县级)',
    adress        varchar(64)       not null comment '详细地点',
    content       longtext          not null comment '内容',
    star          int     default 0 not null comment '点赞',
    collect       int     default 0 not null comment '收藏',
    create_time   date              not null comment '创建时间',
    state         tinyint default 0 null comment '状态(0.待审核 1.审核通过 2.审核不通过)',
    user_id       int               not null comment '校友ID',
    constraint t_activity_t_user_id_fk
        foreign key (user_id) references t_user (id)
)
    comment '活动表';

create table t_album
(
    id          int auto_increment
        primary key,
    sort        tinyint           not null comment '分类',
    title       varchar(32)       not null comment '标题',
    content     longtext          not null comment '内容',
    star        int     default 0 not null comment '点赞',
    collect     int     default 0 not null comment '收藏',
    create_time date              not null comment '创建时间',
    state       tinyint default 0 null comment '状态(0.待审核 1.审核通过 2.审核不通过)',
    user_id     int               not null comment '校友ID',
    constraint t_album_t_user_id_fk
        foreign key (user_id) references t_user (id)
)
    comment '相册表';

create table t_posts
(
    id          int auto_increment
        primary key,
    sort        tinyint           not null,
    content     longtext          not null comment '内容',
    star        int     default 0 not null comment '点赞',
    collect     int               not null comment '收藏',
    create_time date              not null comment '创建时间',
    state       tinyint default 0 null comment '状态(0.待审核 1.审核通过 2.审核不通过)',
    user_id     int               not null comment '校友ID',
    constraint t_posts_t_user_id_fk
        foreign key (user_id) references t_user (id)
)
    comment '帖子表';


