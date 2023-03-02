package com.bbw.god.gameuser;

import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.CR;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.GameTool;
import com.bbw.god.gameuser.dice.RDGainDice;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.res.dice.RDDiceCapacity;
import com.bbw.god.gameuser.res.dice.UserDiceCapacityService;
import com.bbw.god.login.*;
import com.bbw.god.notify.rednotice.RedNoticeService;
import com.bbw.god.rd.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author suhq
 * @version 创建时间：2018年9月11日 上午9:10:34
 */
@RestController
public class GameUserController extends AbstractController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private GameUserLogic gameUserLogic;
    @Autowired
    private GameUserShakeLogic gameUserShakeLogic;
    @Autowired
    private UserLoginService loginService;
    @Autowired
    private GameUserService userService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UserDiceCapacityService userDiceCapacityService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private RedNoticeService redNoticeService;

    @GetMapping("gu!updateOnlineStatus")
    public Rst updateOnlineStatus(String account, @RequestParam(defaultValue = "1") int online) {
        // TODO:心跳处理
        // online 类型string 在线1 不在线0
        // account 类型string 游戏内所有平台统一后账号
        return Rst.businessOK();
    }

    @GetMapping("gu!resNum")
    public Rst resNum() {
        Rst rst = Rst.businessOK();
        GameUser usr = this.getGameUser();
        rst.put("copper", usr.getCopper());
        rst.put("gold", usr.getGold());
        return rst;
    }

    /**
     * 摇骰子
     *
     * @param diceNum
     * @return
     */
    @GetMapping(CR.GameUser.SHAKE_DICE)
    public RDAdvance shakeDice(int diceNum, Integer newerGuide) {
        /*if (null != newerGuide) {
            return newerGuideService.shakeDice(getUserId(), diceNum, newerGuide);
        }*/
        return this.gameUserShakeLogic.shakeDice(getUserId(), diceNum);
    }

    /**
     * 玩家（使用漫步鞋效果）选择方向
     *
     * @param direction
     * @return
     */
    @GetMapping(CR.GameUser.CHOOSE_DIRECTION)
    public RDAdvance chooseDirection(int direction) {
        return this.gameUserShakeLogic.chooseDirection(getUserId(), direction);
    }

    /**
     * 设置玩家漫步靴自动使用的状态
     *
     * @param statusForMBX
     * @return
     */
    @GetMapping(CR.GameUser.CHANGE_STATUS_FOR_MBX)
    public RDCommon changeStatusForMBX(int statusForMBX) {
        return this.gameUserLogic.changeStatusForMBX(getUserId(), statusForMBX);
    }

    /**
     * 跨0点|客户端从后台唤醒，请求玩家信息
     *
     * @return
     */
    @GetMapping(CR.GameUser.GAIN_USER_INFO)
    public RDGameUser gainUserInfo() {
        GameUser gu = this.userService.getGameUser(getUserId());
        LoginInfo loginInfo = new LoginInfo(gu, IpUtil.getIpAddr(this.request), "");
        return this.loginService.getSimpleGuInfo(loginInfo);
    }

    /**
     * 点击角色头像获取玩家的统计信息
     *
     * @return
     */
    @GetMapping(CR.GameUser.GAIN_USER_STATISTIC_INFO)
    public RDGuStatistic gainUserStatisticInfo() {
        return this.gameUserLogic.gainUserStatisticInfo(getUserId());
    }

    /**
     * 获取玩家未捐赠的特产
     *
     * @return
     */
    @GetMapping(CR.GameUser.GAIN_UNFILLED_SPECIALS)
    public RDGuStatistic gainUnfilledSpecials() {
        return this.gameUserLogic.gainUnfilledSpecials(getUserId());
    }

    /**
     * 客户端定时来获得数据 未读邮件、添加好友通知、
     *
     * @return
     */
    @GetMapping(CR.GameUser.GAIN_NEW_INFO)
    public RDNoticeInfo gainNewInfo() {
        RDNoticeInfo rd = new RDNoticeInfo();
        GameUser gu = getGameUser();
        List<String> allNotices = redNoticeService.getAllNotice(gu);
        rd.addRedNotices(allNotices);
        this.loginService.getNoticeInfo(gu, allNotices, rd);
        return rd;
    }

    /**
     * 设置头像
     *
     * @return
     */
    @GetMapping(CR.GameUser.SET_HEAD)
    public RDSuccess setHead(int head) {
        return this.gameUserLogic.setHead(getUserId(), head);
    }

    /**
     * 获取额外的特殊头像列表
     *
     * @return
     */
    @GetMapping(CR.GameUser.LIST_HEAD)
    public RDGameUser getHeads() {
        return this.gameUserLogic.getHeadList(getUserId());
    }

    /**
     * 设置头像框
     *
     * @param icon
     * @return
     */
    @GetMapping(CR.GameUser.SET_HEAD_ICON)
    public RDSuccess setHeadIcon(int icon) {
        return this.gameUserLogic.setHeadIcon(getUserId(), icon);
    }

    @GetMapping(CR.GameUser.SET_EMOTICON)
    public RDSuccess setEmoticon(Integer id) {
        return this.gameUserLogic.setEmoticon(getUserId(), id);
    }

    /**
     * 获取所有拥有的头像框
     *
     * @return
     */
    @GetMapping(CR.GameUser.LIST_HEAD_ICON)
    public RDGameUser getHeadIcons() {
        return this.gameUserLogic.getHeadIconList(getUserId());
    }

    /**
     * 重命名
     *
     * @param newNickname
     * @return
     */
    @GetMapping(CR.GameUser.RENAME)
    public RDSuccess rename(String newNickname) {
        if (StrUtil.isNull(newNickname)) {
            throw new ExceptionForClientTip("createrole.nickname.empty");
        }
        newNickname = newNickname.replaceAll(" ", "");
        int maxNicknameLength = GameTool.maxNicknameLength();
        if (newNickname.length() == 0 || newNickname.length() > maxNicknameLength) {
            throw new ExceptionForClientTip("createrole.nickname.valid.length", maxNicknameLength);
        }
        LoginPlayer user = getUser();
        if (SensitiveWordUtil.isNotPass(newNickname, user.getChannelId(), user.getOpenId())) {
            throw new ExceptionForClientTip("createrole.not.sensitive.words");
        }
        return this.gameUserLogic.rename(getUserId(), newNickname);
    }

    /**
     * 0时后可领取税收
     *
     * @return
     */
    @GetMapping(CR.GameUser.GAIN_COPPER)
    public RDSalaryCopper gainCopper() {
        return this.gameUserLogic.getCopper(getUserId());
    }

    /**
     * 获取战斗状态
     *
     * @return
     */
    @GetMapping(CR.GameUser.GAIN_STATUS)
    public RDFightInfo gainFightInfo() {
        return this.gameUserLogic.gainFightInfo(getUserId(), this.getServerId());
    }

    /**
     * 设置头像
     *
     * @return
     */
    @GetMapping(CR.GameUser.BUY_DICE)
    public RDDiceBuy buyDice() {
        return this.gameUserLogic.buyDice(getUserId());
    }

    /**
     * 获得分享奖励，三级城以上及五星卡牌
     *
     * @return
     */
    @GetMapping(CR.GameUser.GAIN_SHARE_AWARD)
    public RDCommon gainShareAward(String type, String cardId) {
        return this.gameUserLogic.getShareAward(getUserId(), type, cardId);
    }

    /**
     * 通过兑换码获得礼包
     *
     * @return
     */
    @GetMapping(CR.Pack.EXCHANGE)
    public RDCommon exchangePack(String exchangeCode) {
        if (StrUtil.isBlank(exchangeCode)) {
            throw new ExceptionForClientTip("exchange.code.not.blank");
        }
        exchangeCode = exchangeCode.trim();
        exchangeCode = exchangeCode.replace(" ", "");
        if (StrUtil.isBlank(exchangeCode)) {
            throw new ExceptionForClientTip("exchange.code.not.blank");
        }
        long guId = getUserId();
        if (!newerGuideService.isPassNewerGuide(guId)){
            throw new ExceptionForClientTip("exchange.pack.need.pass.newer.guide");
        }
        String account = getAccount();
        int channelId = getUser().getChannelId();
        // 拼接兑换地址
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(Cfg.I.getUniqueConfig(CfgGame.class).getUacBaseUrl());
        urlBuffer.append("pack!gainPackAward?");
        urlBuffer.append("account=" + HttpClientUtil.urlEncode(account));
        urlBuffer.append("&plat=" + channelId);
        urlBuffer.append("&exchangeCode=" + exchangeCode);
        CfgServerEntity server = Cfg.I.get(getServerId(), CfgServerEntity.class);
        urlBuffer.append("&server=" + HttpClientUtil.urlEncode(server.getName() + guId));
        // 兑换发放
        return this.gameUserLogic.gainPacks(guId, urlBuffer.toString(),exchangeCode);
    }

    /**
     * 客户端定时获取体力
     *
     * @return
     */
    @GetMapping(CR.GameUser.INC_DICE)
    public RDGainDice gainDice() {
        return this.gameUserLogic.gainDice(getUserId());
    }

    @GetMapping(CR.GameUser.GAIN_TIANLING_BAG_STATUS)
    public RDLingYinStatus getTianlingStatus() {
        return this.gameUserLogic.getTianlingStatus(getUserId());
    }

    @GetMapping(CR.GameUser.GAIN_TIANLING_BAG)
    public RDCommon gainTianlingBag() {
        return this.privilegeService.getTianlingAward(getUserId());
    }

    /**
     * 获取玩家显示的信息
     *
     * @return
     */
    @GetMapping(CR.GameUser.GAIN_USER_SHOW_INFO)
    public RDUserInfo gainUserShowInfo(Long uid) {
        if (null == uid || uid <= 0) {
            throw new ExceptionForClientTip("request.param.not.valid");
        }
        return gameUserLogic.getUserInfoByUid(getUserId(), uid);
    }

    @GetMapping(CR.GameUser.MENU_OPEN)
    public RDOpenMenu getOpenMenus() {
        return gameUserLogic.getOpenMenuList(getUserId());
    }

    @GetMapping(CR.GameUser.USER_DICE_CAPACITY)
    public RDDiceCapacity getUserDiceCapacity() {
        return userDiceCapacityService.getDiceCapacityInfo(getGameUser());
    }

    @GetMapping(CR.GameUser.BUY_USER_DICE_BY_CAPACITY)
    public RDDiceCapacity buyUserDiceByCapacity() {
        return userDiceCapacityService.buyDiceByCapacity(getUserId());
    }

}
