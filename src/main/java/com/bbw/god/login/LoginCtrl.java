package com.bbw.god.login;

import com.bbw.App;
import com.bbw.common.*;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.CfgChannelService;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.limit.GameBlackIpService;
import com.bbw.god.game.monitor.MonitorUser;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.limit.UserLimit;
import com.bbw.god.gameuser.limit.UserLimitService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.login.event.LoginEventPublisher;
import com.bbw.god.login.review.ChannelReviewService;
import com.bbw.god.login.strategy.LoginCheckStrategy;
import com.bbw.god.login.strategy.LoginCheckStrategyFactory;
import com.bbw.god.login.strategy.LoginResult;
import com.bbw.god.security.token.AuthToken;
import com.bbw.god.security.token.SingleTokenService;
import com.bbw.god.server.ServerStatus;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.uac.entity.AccountEntity;
import com.bbw.god.uac.service.AccountService;
import com.bbw.god.uac.service.BasePlatService;
import com.bbw.god.validator.GodValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ??????
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018???9???27??? ??????5:44:22
 */
@Slf4j
@RestController
public class LoginCtrl {
    @Autowired
    public HttpServletRequest request;
    @Autowired
    private CfgChannelService cfgChannelService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private LoginCheckStrategyFactory loginCheckStrategyFactory;
    @Autowired
    private UserLimitService userLimitService;
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private SingleTokenService singleTokenService;
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private MailService mailService;
    @Autowired
    private MonitorUser monitorUser;
    @Autowired
    private InsRoleInfoService insRoleInfoService;
    @Autowired
    private BasePlatService basePlatService;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private App app;
    @Autowired
    private ChannelReviewService channelReviewService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private GameBlackIpService gameBlackIpService;


    /**
     * ????????????
     *
     * @return
     */
    @RequestMapping(value = "account!needLogin")
    public Rst needLogin() {
        String msg = LM.I.getMsg("need.login");
        return Rst.businessFAIL(msg);
    }

    /**
     * ????????????
     *
     * @return
     */
    @RequestMapping(value = "account!login")
    public RDLogin login(LoginVO loginVO, HttpServletRequest request) {
        log.debug("account!login " + loginVO);
        String accountToLog = "???" + loginVO.getEmail() + "???";
        log.info(accountToLog + " login to server " + loginVO.getServerId());
        long begin = System.currentTimeMillis();
        //????????????
        GodValidator.validateEntity(loginVO);
        String ip = IpUtil.getIpAddr(this.request);
        //????????????
        log.info(accountToLog + " to check server status,???????????????" + (System.currentTimeMillis() - begin));
        CfgServerEntity loginServer = getAndCheckServer(loginVO, ip);
        // ????????????
        log.info(accountToLog + " to check channel,???????????????" + (System.currentTimeMillis() - begin));
        CfgChannelEntity channel = getAndCheckChannel(loginVO.getPlat());
        // ????????????????????????????????????
        log.info(accountToLog + " to do login stategy,???????????????" + (System.currentTimeMillis() - begin));
        LoginResult loginResult = checkLogin(loginVO, channel);
        //????????????
        log.info(accountToLog + " to check role is exist,???????????????" + (System.currentTimeMillis() - begin));
        CfgGame gameConfig = Cfg.I.getUniqueConfig(CfgGame.class);
        Optional<Long> uidOp = serverUserService.getUidByOriginSidAndUsername(loginServer.getId(), loginResult.getAccountName());
        // ????????????????????????????????????
        if (!uidOp.isPresent()) {
            if (!gameConfig.getWhiteAccounts().contains(loginVO.getEmail()) && !ableCreateRole(loginServer, request)) {
                // ?????????????????????????????????
                throw new ExceptionForClientTip("server.full");
            }
            if (!channelReviewService.isIosChecking(channel.getPlatCode(), loginVO.getClientVersion())) {
                // ??????????????????????????????????????????????????????????????????
                // ???????????????,??????2
                return RDLogin.toCreateRole(loginResult.getAccountName());
            }
            channelReviewService.doAfterLoginAsIosChecking(channel, loginVO, loginResult, loginServer, request);
        }
        // ??????????????????
        checkLoginLimit(uidOp.get(), ip);
        // ????????????????????????
        log.info(accountToLog + " to get and update gameuser,???????????????" + (System.currentTimeMillis() - begin));
        GameUser user = getGameUser(uidOp.get(), loginResult, loginServer, channel);
        LoginPlayer player = LoginPlayer.fromGameUser(user, loginServer, ip, loginVO.getDeviceId(), loginVO.getOaid(), loginVO.getPushToken());
        AccountEntity account = this.accountService.findByAccount(user.getRoleInfo().getUserName());
        player.setOpenId(account.getOpenId());
        LoginInfo loginInfo = new LoginInfo(user, IpUtil.getIpAddr(this.request), loginVO.getPushToken());
        log.info(accountToLog + " to get gu info,???????????????" + (System.currentTimeMillis() - begin));
        long getGuInfoBeginTime = System.currentTimeMillis();
        RDGameUser rdGameUser = this.userLoginService.getGameUserInfo(loginInfo, player);
        long useTime = System.currentTimeMillis() - getGuInfoBeginTime;
        if (useTime > 10000) {
            log.error("??????????????????????????????" + useTime);
        } else if (useTime > 2000) {
            log.warn("??????????????????????????????" + useTime);
        }
        // ???????????????sync???map??????????????????????????????????????????????????????????????????
        syncLockUtil.putToLockMap(user.getId());
        // ??????????????????????????????????????????
        log.info(accountToLog + " to pub login event,???????????????" + (System.currentTimeMillis() - begin));
        LoginEventPublisher.pubLoginEvent(player);
        this.monitorUser.monitor(user);
        AuthToken authToken = singleTokenService.generateToken(uidOp.get(), player);
        rdGameUser.setToken(authToken.getToken());
        rdGameUser.setTokenExpiredTime(authToken.getExpiredDate().getTime());
        return rdGameUser;
    }

    private boolean checkIp(HttpServletRequest request) {
        String ip = IpUtil.getIpAddr(request);
        CfgGame cfgGame = Cfg.I.getUniqueConfig(CfgGame.class);
        if (!(cfgGame.getGmWhiteIps().contains(ip) || this.app.runAsDev() || this.app.runAsTest())) {
            return false;
        }
        return true;
    }

    private boolean ableCreateRole(CfgServerEntity loginServer, HttpServletRequest request) {
        return true;
//        if (checkIp(request)) {
//            return true;
//        }
//        Date beginTime = loginServer.getBeginTime();
//        int daysBetween = DateUtil.getDaysBetween(beginTime, DateUtil.now());
//        CfgGame config = Cfg.I.getUniqueConfig(CfgGame.class);
//        return daysBetween <= config.getAbleEnterServerInDays();
    }

    /**
     * ??????
     *
     * @param request
     * @return
     */
    @RequestMapping("/loginOut")
    @ResponseBody
    public Rst loginOut(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.setAttribute(session.getId(), null);
            session.invalidate();
        }
        return Rst.businessOK();
    }

    /**
     * ????????????????????????6???????????????
     *
     * @param account ????????????
     * @return
     */
    @RequestMapping("/getLoginData")
    public RDServerData getLoginData(String account, String channelCode) {
        // ??????account???ins_role_info???????????????????????????????????????????????????
        List<InsRoleInfoEntity> insRoleInfoEntities = this.insRoleInfoService.getByUsername(account).stream().
                sorted(Comparator.comparing(InsRoleInfoEntity::getLastLoginDate).reversed()).collect(Collectors.toList());
        Integer groupId = this.basePlatService.fetchOne(channelCode).getGroupId();

        List<RDServerData.ServerData> dataList = new ArrayList<>();
        for (InsRoleInfoEntity insRoleInfoEntity : insRoleInfoEntities) {
            CfgServerEntity server = ServerTool.getServer(insRoleInfoEntity.getSid());
            if (server == null) {
                continue;
            }
            if (server.getGroupId() == groupId.intValue()) {
                RDServerData.ServerData serverData = new RDServerData.ServerData();
                serverData.setSid(insRoleInfoEntity.getOriginSid());
                serverData.setLevel(insRoleInfoEntity.getLevel());
                serverData.setSname(insRoleInfoEntity.getServerName());
                dataList.add(serverData);
            }
            if (dataList.size() >= 6) {
                break;
            }
        }
        RDServerData rdServerData = new RDServerData();
        rdServerData.setServerDataList(dataList);
        return rdServerData;
    }

    /**
     * ?????????????????????????????????????????????
     * ???????????????????????????????????????????????????
     *
     * @param loginVO
     */
    private CfgServerEntity getAndCheckServer(LoginVO loginVO, String ip) {
        CfgGame gameConfig = Cfg.I.getUniqueConfig(CfgGame.class);
        CfgServerEntity loginServer = ServerTool.getServer(loginVO.getServerId());
        boolean isWhiteAccount = gameConfig.getWhiteAccounts().contains(loginVO.getEmail());
        boolean isWhiteIp = gameConfig.getGmWhiteIps().contains(ip);
        boolean isNeedCheck = !isWhiteAccount && !isWhiteIp;
        if (isNeedCheck) {
            ServerStatus serverStatus = loginServer.getServerStatus();
            if (serverStatus == ServerStatus.PREDICTING) {
                String tipDetail = DateUtil.toString(loginServer.getBeginTime(), "M???d??? H:mm");
                throw new ExceptionForClientTip("server.predicting", tipDetail);
            }
            if (serverStatus == ServerStatus.MAINTAINING) {
                String tipDetail = DateUtil.toString(loginServer.getMtEndTime(), "H:mm");
                throw new ExceptionForClientTip("server.maintaining", tipDetail);
            }
        }
        return loginServer;
    }

    /**
     * ?????????????????????
     *
     * @param platCode
     * @return
     */
    private CfgChannelEntity getAndCheckChannel(String platCode) {
        Optional<CfgChannelEntity> channel = this.cfgChannelService.getByPlatCode(platCode);
        if (!channel.isPresent()) {
            throw new ExceptionForClientTip("channel.not.exists");
        }
        return channel.get();
    }

    /**
     * ??????????????????
     *
     * @param loginVO
     * @param channel
     * @return
     */
    private LoginResult checkLogin(LoginVO loginVO, CfgChannelEntity channel) {
        LoginCheckStrategy strategy = this.loginCheckStrategyFactory.getLoginCheckStrategy(loginVO.getUserType());
        LoginResult loginResult = strategy.check(this.request, channel);
        if (!loginResult.pass()) {
            // ???????????????
            throw ExceptionForClientTip.fromMsg(loginResult.getMsg());
        }
        return loginResult;
    }

    /**
     * ??????????????????
     *
     * @param uid
     */
    private void checkLoginLimit(long uid, String ip) {
        //??????????????????
        UserLimit loginLimit = this.userLimitService.getLoginLimit(uid);
        if (loginLimit != null) {
            throw new ExceptionForClientTip("login.limit", DateUtil.toDateString(loginLimit.getLimitEnd()));
        }
        //ip????????????
        boolean isBlackIp = gameBlackIpService.ifBlackIp(ip);
        if (isBlackIp) {
            throw new ExceptionForClientTip("login.ip.limit");
        }
    }

    private GameUser getGameUser(long uid, LoginResult accountLoginResult, CfgServerEntity loginServer, CfgChannelEntity channel) {
        GameUser user = this.gameUserService.getGameUserWithUserData(uid);
        if (loginServer.getMergeSid() != loginServer.getId()) {
            Optional<InsRoleInfoEntity> role = this.roleInfo.getUidAtLoginServer(loginServer.getId(), accountLoginResult.getAccountName());
            if (channel.getId() != user.getRoleInfo().getChannelId()
                    || (null != role.get().getInviCode() && !role.get().getInviCode().equals(user.getRoleInfo().getMyInvitationCode()))
                    || (role.get().getSid() != user.getServerId())) {
                user.getRoleInfo().setChannelId(channel.getId());
                user.getRoleInfo().setMyInvitationCode(role.get().getInviCode());
                user.updateRoleInfo();
                user.updateServerId(role.get().getSid());// ????????????????????????????????????????????????
            }
        }
        this.gameUserService.setActiveSid(user.getId(), user.getServerId());
        //???????????????
        boolean b = BbwSensitiveWordUtil.contains(user.getRoleInfo().getNickname());
        if (b) {
            String title = LM.I.getMsgByUid(user.getId(), "mail.revise.username.title");
            String content = LM.I.getMsgByUid(user.getId(), "mail.revise.username.content");
            this.mailService.sendSystemMail(title, content, user.getId());
        }
        return user;
    }
}
