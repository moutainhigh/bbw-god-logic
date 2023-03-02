package com.bbw.god.login;

import com.bbw.common.*;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.CfgChannelService;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.GameTool;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.login.event.LoginEventPublisher;
import com.bbw.god.login.repairdata.RepairInitDataService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.security.token.AuthToken;
import com.bbw.god.security.token.SingleTokenService;
import com.bbw.god.server.RoleVO;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.uac.entity.AccountBindEntity;
import com.bbw.god.uac.entity.AccountEntity;
import com.bbw.god.uac.service.AccountBindService;
import com.bbw.god.uac.service.AccountService;
import com.bbw.god.validator.GodValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 角色创建接口
 *
 * @author: suhq
 * @date: 2021/11/25 11:22 上午
 */
@Slf4j
@RestController
public class RoleCreateCtrl {
    @Autowired
    public HttpServletRequest request;
    @Autowired
    private CfgChannelService cfgChannelService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountBindService accountBindService;
    @Autowired
    private SingleTokenService singleTokenService;
    @Autowired
    private RepairInitDataService repairInitDataService;

    /**
     * 创建角色
     *
     * @param param
     * @return
     */
    @GetMapping("gu!createRole")
    public RDLogin createRole(RoleVO param) {
        log.debug("account!createRole " + param);
        if (StrUtil.isBlank(param.getNickname())) {
            throw new ExceptionForClientTip("createrole.nickname.empty");
        } else {
            //除去昵称中的所有空格
            param.setNickname(param.getNickname().replaceAll(" ", ""));
        }
        int maxNicknameLength = GameTool.maxNicknameLength();
        if (param.getNickname().length() == 0 || param.getNickname().length() > maxNicknameLength) {
            throw new ExceptionForClientTip("createrole.nickname.valid.length", maxNicknameLength);
        }
        // 校验角色创建参数
        GodValidator.validateEntity(param);
        log.debug(param.toString());

        // 创建角色信息
        Optional<CfgChannelEntity> optional = cfgChannelService.getByPlatCode(param.getChannelCode());
        if (!optional.isPresent()) {
            throw new ExceptionForClientTip("error.channel");
        }

        AccountEntity account = this.accountService.findByAccount(param.getUserName());
        if (account == null) {
            //处理微信绑定后的新角色创建
            List<AccountBindEntity> binds = this.accountBindService.getWechatBindAccountEntity(param.getUserName());
            if (ListUtil.isNotEmpty(binds)) {
                param.setUserName(binds.get(0).getPlayerAccount());
                account = this.accountService.findByAccount(param.getUserName());

            }
        }
        param.setMyInviCode(account.getInvitationCode());
        param.setIp(IpUtil.getIpAddr(this.request));
        // 验证敏感词汇
        if (SensitiveWordUtil.isNotPass(param.getNickname().trim(), optional.get().getId(), account.getOpenId())) {
            throw new ExceptionForClientTip("createrole.not.sensitive.words");
        }
        // 验证玩家输入邀请码是否在该区服（原始区服ID）已有角色
        if (StrUtil.isNotBlank(param.getInvitationCode())) {
            Optional<Long> inviterId = this.serverUserService.getUidByInvitationCode(param.getServerId(), param.getInvitationCode());
            // 邀请人不存在，通知客户端
            if (!inviterId.isPresent()) {
                throw new ExceptionForClientTip("createrole.unvalid.invitationCode");
            }
        }

        CfgServerEntity loginServer = Cfg.I.get(param.getServerId(), CfgServerEntity.class);
        // 合服后仍然保持原始区服入口，可以在原始区服创建角色，这里使用原始区服Id
        // 验证在原始区服是否存在账号
        Optional<InsRoleInfoEntity> role = this.roleInfo.getUidAtLoginServer(loginServer.getId(), param.getUserName());
        if (role.isPresent()) {
            throw new ExceptionForClientTip("createrole.has.role");
        }

        // 昵称必须在合服区里保持唯一，这里必须使用合服ID
        Optional<Long> uidOp = this.serverUserService.getUidByNickName(loginServer.getMergeSid(), param.getNickname());
        if (uidOp.isPresent()) {
            throw new ExceptionForClientTip("createrole.nickname.is.exist");
        }
        GameUser user = this.serverUserService.newGameUser(param, optional.get().getId(), account.getRegDate());
        long uid = user.getId();
        this.gameUserService.setActiveSid(uid, user.getServerId());
        doAfterCreatRole(user);
        // 重新获取一次user对象，保证doAfterCreatRole后user的数据是最新的
        user = this.gameUserService.getGameUser(uid);

        // 返回角色信息给客户端
        LoginPlayer player = LoginPlayer.fromGameUser(user, loginServer, IpUtil.getIpAddr(this.request), param.getDeviceId(), param.getOaid(), param.getPushToken());
        player.setOpenId(account.getOpenId());
        LoginInfo loginInfo = new LoginInfo(user, IpUtil.getIpAddr(this.request), param.getPushToken());
        // 登录时检测没有数据,如果没有则初始化数据
        System.out.println("checkAndInitUserData");
        repairInitDataService.repair(user, null);
        RDGameUser rdGameUser = this.userLoginService.getGameUserInfo(loginInfo, player);
        LoginEventPublisher.pubLoginEvent(player);
        AuthToken authToken = singleTokenService.generateToken(uid, player);
        rdGameUser.setToken(authToken.getToken());
        rdGameUser.setTokenExpiredTime(authToken.getExpiredDate().getTime());
        return rdGameUser;
    }

    /**
     * 创建角色后要进行的操作
     *
     * @param user 玩家对象
     */
    private void doAfterCreatRole(GameUser user) {
        int country = user.getRoleInfo().getCountry();
        ResEventPublisher.pubEleAddEvent(user.getId(), country, 3, WayEnum.NONE, new RDCommon());
        ResEventPublisher.pubEleAddEvent(user.getId(), TypeEnum.Earth.getValue(), 2, WayEnum.NONE, new RDCommon());
    }

    /**
     * 获取生成的校验过后的随机昵称
     *
     * @param sid
     * @param account
     * @return
     * @throws InterruptedException
     */
    @RequestMapping("gu!randomName")
    public RdRegister getRandomName(int sid, String account) {
        List<String> res = new ArrayList<String>();
        long start = System.currentTimeMillis();
        int need = 10;
        CfgServerEntity loginServer = ServerTool.getServer(sid);
        List<String> nicknames = null;
        List<String> build = null;
        do {
            build = RandomNameUtil.getRandomName(need - res.size());
            nicknames = this.serverUserService.checkServerNickname(loginServer.getMergeSid(), build);
            for (String str : nicknames) {
                build.remove(str);
            }
            res.addAll(build);
        } while (res.size() < 5);
        long end = System.currentTimeMillis();
        log.debug("生成并检验昵称，耗时：" + (end - start) + ";有效昵称数量：" + res.size());
        return RdRegister.putRadomNames(res);
    }
}
