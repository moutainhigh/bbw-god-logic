package com.bbw.god.login.review;

import com.bbw.common.DateUtil;
import com.bbw.common.IpUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.service.CfgChannelService;
import com.bbw.god.db.service.InsRoleInfoService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.guide.GuideEventPublisher;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.guide.UserNewerGuide;
import com.bbw.god.gameuser.guide.v1.NewerGuideEnum;
import com.bbw.god.login.LoginInfo;
import com.bbw.god.login.LoginVO;
import com.bbw.god.login.repairdata.RepairInitDataService;
import com.bbw.god.login.strategy.LoginResult;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.RoleVO;
import com.bbw.god.server.ServerUserService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 审核渠道初始服务
 *
 * @author suhq
 * @date 2020-09-28 10:11
 **/
@ConfigurationProperties("bbw-god")
@Service
public class ChannelReviewService {
    @Setter
    private List<ChannelReviewInfo> checkingIosChannels = new ArrayList<>();
    @Autowired
    private InsRoleInfoService roleInfo;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private RepairInitDataService repairInitDataService;
    @Autowired
    private CfgChannelService cfgChannelService;


    public void doAfterLoginAsIosChecking(CfgChannelEntity channel, LoginVO loginVO, LoginResult loginResult, CfgServerEntity loginServer, HttpServletRequest request) {
        if (!isIosChecking(channel.getPlatCode(), loginVO.getClientVersion())) {
            return;
        }
        // 在本区服是否已经创建角色
        Optional<InsRoleInfoEntity> role = roleInfo.getUidAtLoginServer(loginServer.getId(), loginResult.getAccountName());
        // 没有角色初始化一个角色
        if (!role.isPresent()) {
            RoleVO roleVO = new RoleVO();
            roleVO.setServerId(loginVO.getServerId());
            roleVO.setUserName(loginVO.getEmail());
            roleVO.setNickname(serverUserService.getRandomNickName());
            roleVO.setChannelCode(channel.getPlatCode());
            roleVO.setProperty("10");
            roleVO.setIp("127.0.0.1");
            GameUser user = serverUserService.newGameUser(roleVO, channel.getId(), DateUtil.getTodayInt());
            ChannelReviewInfo reviewInfo = getReviewInfo(channel, loginVO.getClientVersion());
            user.moveTo(reviewInfo.getInitPos(), reviewInfo.getInitDir());
            //发放卡牌
            List<CfgCardEntity> cards = CardTool.getRandomNotSpecialCards(user.getRoleInfo().getCountry(), 1, 4);
            int cardId = Arrays.asList(118, 220, 318, 420, 520).get(user.getRoleInfo().getCountry() / 10 - 1);
            cards.add(CardTool.getCardById(cardId));
            cardId = Arrays.asList(111, 214, 313, 410, 514).get(user.getRoleInfo().getCountry() / 10 - 1);
            cards.add(CardTool.getCardById(cardId));
            List<Integer> cardIds = cards.stream().map(CfgCardEntity::getId).collect(Collectors.toList());
            CardEventPublisher.pubCardAddEvent(user.getId(), cardIds, WayEnum.NONE, "", new RDCommon());
            LoginInfo loginInfo = new LoginInfo(user, IpUtil.getIpAddr(request), loginVO.getPushToken());
            repairInitDataService.repair(user, DateUtil.now());
            //通过新手引导
            UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(user.getId());
            if (userNewerGuide == null) {
                userNewerGuide = UserNewerGuide.getInstance(user.getId(), NewerGuideEnum.CARD_LEVEL_UP.getStep(), true);
                this.gameUserService.addItem(user.getId(), userNewerGuide);
            } else {
                newerGuideService.updateNewerGuide(user.getId(), NewerGuideEnum.CARD_LEVEL_UP, new RDCommon());
            }
            GuideEventPublisher.pubPassNewerGuideEvent(user.getId(), new RDCommon());
        }
    }

    public boolean isIosChecking(String channelCode, String clientVersion) {
        if (StrUtil.isBlank(channelCode) || StrUtil.isBlank(clientVersion)) {
            return false;
        }
        Optional<CfgChannelEntity> channel = cfgChannelService.getByPlatCode(channelCode);
        return getReviewInfo(channel.get(), clientVersion) != null;
    }

    public ChannelReviewInfo getReviewInfo(CfgChannelEntity channel, String clientVersion) {
        if (channel == null) {
            return null;
        }
        if (clientVersion == null) {
            clientVersion = "0";
        }
        String channelVersion = channel.getPlatCode() + "v" + clientVersion;
        ChannelReviewInfo reviewInfo = checkingIosChannels.stream().filter(tmp -> tmp.getChannelVersion().equals(channelVersion)).findFirst().orElse(null);
        return reviewInfo;
    }
}
