package com.tox;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import com.thinksky.info.PostInfo;
import com.thinksky.info.UserInfo;
import com.thinksky.info.WeiboCommentInfo;
import com.thinksky.info.WeiboInfo;

import java.util.ArrayList;
import java.util.List;

public class Url {

    public static SharedPreferences sp = null;
    public static Context context = null;
    public static String VERSION = "";

    /*新写的微博接口*/
    public static String HTTPURL = "http://192.168.15.36:8081/opensns/api.php";
    public static String USERHEADURL = "http://192.168.15.36:8081/opensns/";
//    public static String MYWEIBO = "/weibo/listMyFollowingWeibo&uid=";
    public static String WEIBO = "/weibo/listAllWeibo";
    public static String MYFOLLOWINGWEIBO = "/weibo/listMyFollowingWeibo";
    public static String SENDWEIBO = "/weibo/sendWeibo";
    public static String DELETEWEIBO = "/weibo/deleteWeibo";
    public static String SENDWEIBOCOMMENT = "/weibo/sendComment";
    public static String COMMENTS = "/weibo/listComment";
    public static String UPLOADIMGURL = HTTPURL + "?s=/public/uploadImage";
    public static String UPLOADTOUXIANG = HTTPURL + "?s=/public/uploadAvatar";
    public static String REPOSTWEIBO = "/weibo/sendRepost";
    public static String SUPPORTWEIBO = "/weibo/supportWeibo";
    public static String WEIBOWORDLIMIT = "/weibo/getWeiboWordsLimit";
    //临时保存上传压缩后的图片
    public static String UPLOADTEMPORARYPATH = Environment.getExternalStorageDirectory() + "/tox/cache/";
    public static String CHECKUPDATE = "/public/checkUpdate";

    /**
     * 资讯接口
     */
    //获取分类信息
    public static String NEWSCATEGORY = "/news/getCategory";
    //获取所有资讯信息
    public static String NEWSALL = "/news/getNewsAll";
    //获取我的咨询信息
    public static String MYNEWSALL = "/news/getMyNewsAll";
    //获取热门资讯信息
    public static String HOTNEWSALL = "/news/getHotNewsAll";
    //获取资讯详情
    public static String NEWSDETAIL = "/news/getNewsDetail";
    //获取资讯的评论列表
    public static String NEWSCOMMENTS = "/news/getNewsComments";
    //发送资讯评论
    public static String SENDNEWSCOMMENT = "/news/sendNewsComment";
    //发送资讯
    public static String SENDNEWS = "/news/sendNews";
    //删除资讯评论
    public static String DELNEWSREPLY = "/news/delNewsComment";

    /**
     * 专辑的接口
     */
    //获取专辑模块
    public static String ISSUEMODULES = "/issue/getIssueModules";
    //获取专辑信息
    public static String ISSUELIST = "/Issue/getIssuelist";
    //获取专辑详情
    public static String GETISSUEDETAIL = "/Issue/getIssueDetail";
    //获取专辑评论信息
    public static String GETISSUECOMMENTS = "/Issue/getIssueComments";
    //给帖子评论
    public static String SENDISSUECOMMENT = "/Issue/sendIssueComment";
    //给帖子点赞
    public static String SUPPORTISSUE = "/issue/supportIssue";
    //发表专辑
    public static String SENDISSUE = "/issue/sendIssue";
    /**
     * 活动的接口
     */
    //获取活动当前分类信息接口
    public static String EVENTMODULES = "/event/getEventModules";
    //获取某分类下活动信息接口
    public static String EVENTSALL = "/event/getEventsAll";
    //获取活动详情接口
    public static String EVENTDETAIL = "/event/eventDetail";
    //获取评论回复信息
    public static String EVENTCOMMENTS = "/event/getEventComments";
    //获取我的活动信息接口
    public static String WEEVENTS = "/event/getWeEvents";
    //获取某活动报名人的信息接口
    public static String PEOPLEINFOEVENTS = "/event/getPeopleInfoEvents";
    // 活动报名接口
    public static String JOINEVENTS = "/event/joinEvents";
    //活动提前关闭接口
    public static String ENDEVENTS = "/event/endEvents";
    //删除活动接口
    public static String DELETEEVENTS = "/event/deleteEvents";
    //活动的添加与编辑接口
    public static String ADDEVENTS = "/event/addEvents";
    //发布活动评论
    public static String POSTEVENTCOMMENT = "/event/sendEventComment";

    /**
     * 群组的接口
     */
    //获取群组分类信息
    public static String GROUPTYPE = "/group/getGroupType";
    //获取某一分类下的群组信息
    public static String GROUPALL = "/group/getGroupAll";
    //获取我的群组信息
    public static String WEGROUPALL = "/group/getWeGroupAll";
    //获取群组详细信息
    public static String GROUPINFO = "/group/getGroupDetail";
    //获取群组下帖子
    public static String POSTALL = "/group/getPostAll";
    //获取群组置顶区帖子
    public static String TOPPOST = "/group/getTopPost";
    //获取群组下帖子分类
    public static String POSTCATEGORY = "/group/PostCategory";
    //创建群组和编辑群组
    public static String ADDGROUP = "/group/addGroup";
    //获取帖子的回复信息
    public static String POSTREPLY = "/group/getPostReply";
    //获取帖子的品论和点赞数
    public static String POSTPRM = "/group/getPostPRM";
    //获取帖子的楼中楼回复
    public static String POSTLZL = "/group/PostLzl";
    //发布和编辑条子
    public static String SEND = "/group/sendPost";
    //加入群组
    public static String JOINGROUP = "/group/joinGroup";
    //退出群组
    public static String QUITGROUP = "/group/quitGroup";
    //帖子点赞
    public static String SUPPORT = "/group/postSupport";
    //帖子回复
    public static String POSTGROUPCOMMENT = "/group/doReplyPost";
    //公告信息
    public static String GROUPNOTICE = "/group/getNotice";
    //热帖的接口
    public static String HOTPOST = "/group/getHotPost";
    //帖子楼中楼回复
    public static String REPLYGROUPCOMMENTS = "/group/doPostLzl";
    //解散群组
    public static String DISMISSGROUP = "/group/endGroup";

    /**
     * 论坛的接口
     */
    //获取论坛模块
    public static String FORUMMODULES = "/forum/getForumModules";
    //获取帖子列表
    public static String POST = "/forum/getPosts";
    //获取帖子列表
    public static String POSTCOMMENTS = "/forum/getPostComments";
    //获取评论的评论列表
    public static String FORUMCOMMENTS = "/forum/getComments";
    //给帖子点赞
    public static String SUPPORTPOST = "/forum/supportPost";
    //给帖子回复
    public static String SENDPOSTCOMMENT = "/forum/sendPostComment";
    //回复评论
    public static String SENDCOMMENNT = "/forum/sendComment";
    //收藏帖子
    public static String COLLECTPOST = "/forum/collectionPost";
    //发布帖子
    public static String SENDPOST = "/forum/sendPost";
    // 在论坛首页被点击的帖子
    public static String PostID = "";
    //点击首页帖子进入
    public static String detailPostID = "";
    /*用户相关的接口*/
    public static String LOGIN = "/user/login";
    public static String LOGOUT = "/user/logout";
    public static String REGISTER = "/user/register";
    public static String VERIFY = "/user/sendVerify";
    public static String USERINFO = "/user/getProfile";
    public static String SETUSERINFO = "/user/setProfile";
    public static String OTHERINFO = "/user/getFieldSet";
    public static String SETOTHERINFO = "/user/setField";
    public static String LOGINCHECK = "/user/beforeRegister";
    public static String CHECKWAY = "/user/regSwitch";

    //加关注接口
    public static String USERDOFOLLOW = "/user/doFollow";
    //取消关注接口
    public static String USERENDFOLLOW = "/user/endFollow";
    public static String CHECKRANK = "/public/getRank";
    public static String CHECKINFO = "/public/getCheckInfo";
    public static String CHECKIN = "/public/CheckIn";
    public static String AUDIT = "audit.php?";
    public static String YINGCAI = "yingcai.php?";
    public static String SHILING = "shiling.php?";
    public static String CHUANYUE = "chuanyue.php?";
    public static String NEAR = "near.php?";
    public static String UASHAMED = "uashamed.php?";
    public static String ADDVALUE = "addvalue.php";
    public static String REGISTET = "adduser.php";
    public static String ADDCOMMENT = "addcomment.php";
    public static String QIMGURL = "http://534429149.haoqie.net/qiubai/Valueimg/";
    public static boolean IMGFLAG = false;
    /**
     * 自己的信息
     */
    public static UserInfo MYUSERINFO = null;

    public static String USERID = "";

    public static String SESSIONID = "";
    public static String SharedPreferenceName = "userInfo";


    //图片本地保存位置
    public static String IMGLOCAL = Environment.getExternalStorageDirectory() + "/tox/photos/";
    public static String SAVEIMAGE = Environment.getExternalStorageDirectory() + "/tox/saveImage/";
    public static int IMGTYPE_WEIBO = 1;
    public static int IMGTYPE_HEAD = 2;
    public static int IMGTYPE_BIG = 3;
    public static String IMGWEIBOURL = "/sdcard/tox/weibo/";
    public static int CheckIn = 1;
    public static int UnCheckIn = 0;
    public static int WEIBOWORDS = 0;
    public static long LASTPOSTTIME = 0;
    public static long SESSIONIDLIFE = 5;

    //要插入微博
    public static WeiboInfo weiboInfo = null;
    //要插入的帖子
    public static PostInfo postInfo = null;
    //是否要插入帖子
    public static boolean is2InsertPost = false;
    //是否要插入微博
    public static boolean is2InsertWeibo = false;
    //要插入的微博评论
    public static WeiboCommentInfo weiboCommentInfo = null;
    //是否要插入微博评论
    public static boolean is2InsertWeiboCom = false;

    public static int SUPPORTED = 1;
    public static int NOSUPPORT = 0;

    public static String getApiUrl(String apiName) {
        return HTTPURL + "?s=" + apiName;
    }

    //用来记录上一个activity
    public static String activityFrom = "";

    public static String getFROM() {
        return Build.BRAND;
    }

    public static List<String> getDeleteFilesPath() {
        List<String> list = new ArrayList<String>();
        list.add(Environment.getExternalStorageDirectory() + "/tox/cache");
        list.add(Environment.getExternalStorageDirectory() + "/tox/photos");
        return list;
    }


    public static int Type_landlord = 1;
    public static int Type_postCom = 2;

}
