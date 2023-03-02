package com.bbw.god.gameuser.guide.v1;

import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.guide.INewerGuideLoginService;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.guide.UserNewerGuide;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.god.GodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author suhq
 * @description 新手引导登录服务类
 * @date 2019-12-27 03:42
 **/
@Service
public class NewerGuideLoginService implements INewerGuideLoginService {
    @Autowired
    private GameUserService userService;
    @Autowired
    private GodService godService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 进入游戏后的新手引导修正
     *
     * @param gu
     */
    @Override
    public void handleNewerGuideAsLogin(GameUser gu) {
        if (!this.newerGuideService.isPassNewerGuide(gu.getId())) {
            UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(gu.getId());
            NewerGuideEnum guideEnum = NewerGuideEnum.fromValue(userNewerGuide.getNewerGuide());
            // 位置修正
            if (null == guideEnum) {
                UserCity tuShan = userCityService.getUserCity(gu.getId(), 2046);
                guideEnum = null == tuShan ? NewerGuideEnum.START : NewerGuideEnum.CARD_LEVEL_UP;
                newerGuideService.updateNewerGuide(gu.getId(), guideEnum, new RDCommon());
                gu.moveTo(guideEnum.getPos(), guideEnum.getDir());
            }
            gu.moveTo(guideEnum.getPos(), guideEnum.getDir());
            switch (guideEnum) {
                case START:
                    // 移到起点
                    gu.moveTo(NewerGuideEnum.START.getPos(), NewerGuideEnum.START.getDir());
                    // 确保删除所有卡牌
                    List<UserCard> userCards = userCardService.getUserCards(gu.getId());
                    userCardService.delUserCards(gu.getId(), userCards);
                    break;
                case XIANRENDONG:
                    // 玩家已完成仙人洞，保证有一个七香车
                    sendTreasure(gu, TreasureEnum.QXC.getValue());
                    // 确保清除神仙
                    Optional<UserGod> userGod = this.godService.getAttachGod(gu);
                    if (userGod.isPresent()) {
                        this.userService.deleteItem(userGod.get());
                    }
                    // 确保清除多余的卡牌
                    List<UserCard> uCards = userCardService.getUserCards(gu.getId());
                    if (uCards.size() > 1) {
                        uCards = uCards.subList(1, uCards.size());
                        CardEventPublisher.pubCardDelEvent(gu.getId(), uCards);
                    }
                    break;
                case ATTACK:
                    List<UserCity> uCities = userCityService.getUserCities(gu.getId());
                    if (uCities.size() > 0) {
                        // 注意！！！同步事件处理的新手引导转态
                        UserCity userCity = uCities.get(0);
                        userCity.setQz(1);
                        userNewerGuide.updateNewerGuide(NewerGuideEnum.QIANZHUANG.getStep());
                        ResEventPublisher.pubExpAddEvent(gu.getId(), 300, WayEnum.KC_UPDATE, new RDCommon());
                        this.userService.updateItem(userCity);
                        this.userService.updateItem(userNewerGuide);
                    }
                    break;
            }
        } else {
            // 已经通过的修改新手引导的status值
            UserNewerGuide userNewerGuide = newerGuideService.getUserNewerGuide(gu.getId());
            if (NewerGuideEnum.CARD_LEVEL_UP.getStep().intValue() != userNewerGuide.getNewerGuide()) {
                userNewerGuide.setNewerGuide(NewerGuideEnum.CARD_LEVEL_UP.getStep());
                this.userService.updateItem(userNewerGuide);
            }

        }
    }

    /**
     * 如果没有对应法宝，则发一个
     *
     * @param gu
     * @param treasureId
     */
    private void sendTreasure(GameUser gu, Integer treasureId) {
        int ownNum = userTreasureService.getTreasureNum(gu.getId(), treasureId);
        if (ownNum == 0) {
            TreasureEventPublisher.pubTAddEvent(gu.getId(), treasureId, 1, WayEnum.NONE, new RDCommon());
        }
    }
}
