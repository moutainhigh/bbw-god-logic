package com.bbw.god.gameuser.guide.v2;

import com.bbw.common.ListUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
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
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.god.GodService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author suhq
 * @description: 新手引导登录服务类
 * @date 2019-12-27 03:42
 **/
//@Service
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
    @Autowired
    private UserSpecialService userSpecialService;

    /**
     * 进入游戏后的新手引导修正
     *
     * @param gu
     */
    @Override
    public void handleNewerGuideAsLogin(GameUser gu) {
        if (this.newerGuideService.isPassNewerGuide(gu.getId())) {
            return;
        }
        UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(gu.getId());
        NewerGuideEnum preGuideEnum = NewerGuideEnum.fromValue(userNewerGuide.getNewerGuide());
        switch (preGuideEnum) {
            case START:
                sendTreasure(gu, TreasureEnum.DFZ.getValue());
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
            case YEGUAI:
                List<UserCard> cards = userCardService.getUserCards(gu.getId());
                List<Integer> cardIds = Arrays.asList(115, 518);
                // 没领宝箱直接退游戏，卡牌补发
                if (cards.size() < 6) {
                    for (Integer cardId : cardIds) {
                        boolean match = cards.stream().anyMatch(c -> c.getBaseId().intValue() == cardId.intValue());
                        if (!match) {
                            CfgCardEntity cardEntity = CardTool.getCardById(cardId);
                            UserCard userCard = UserCard.fromCfgCard(gu.getId(), cardEntity, WayEnum.FIGHT_YG);
                            userCardService.addUserCard(gu.getId(), userCard);
                        }
                    }
                }
                break;
            case ATTACK:
                UserCity userCity = userCityService.getUserCities(gu.getId()).stream().findFirst().get();
                if (userCity.getLdf() == 0) {
                    preGuideEnum = NewerGuideEnum.YEGUAI;
                }
                break;
            case LDF_LEVEL_UP:
                List<UserCard> cardList = userCardService.getUserCards(gu.getId());
                Long experienceSum = cardList.stream().mapToLong(UserCard::getExperience).sum();
                if (experienceSum.intValue() == 0) {
                    preGuideEnum = NewerGuideEnum.YEGUAI;
                }
                break;
            case CARD_EXP:
                sendTreasure(gu, TreasureEnum.DFZ.getValue());
                break;
            case QKT_USE:
                sendTreasure(gu, TreasureEnum.DFZ.getValue());
                break;
            case DFZ_USE:
                List<UserCard> userCardList = userCardService.getUserCards(gu.getId());
                boolean present = userCardList.stream().anyMatch(uc -> uc.getGetWay().equals(WayEnum.JXZ_AWARD.getValue()));
                if (!present) {
                    sendTreasure(gu, TreasureEnum.DFZ.getValue());
                }
                break;
            case JXZ_BUY:
                Integer tcp = userCityService.getUserCities(gu.getId()).stream().findFirst().get().getTcp();
                if (tcp == 1) {
                    sendTreasure(gu, TreasureEnum.DFZ.getValue());
                }
                break;
            case TCP_LEVEL_UP:
                List<UserSpecial> specialList = userSpecialService.getSpecials(gu.getId());
                if (ListUtil.isEmpty(specialList)) {
                    sendTreasure(gu, TreasureEnum.DFZ.getValue());
                }
                break;
            default:
                break;
        }
        gu.moveTo(preGuideEnum.getPos(), preGuideEnum.getDir());
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
