package com.bbw.god.gameuser.guide.v3;

import com.bbw.common.ListUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.special.CfgSpecialEntity;
import com.bbw.god.game.config.special.SpecialEnum;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.guide.INewerGuideLoginService;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.guide.UserNewerGuide;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.special.UserSpecial;
import com.bbw.god.gameuser.special.UserSpecialService;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialEventPublisher;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.god.GodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author suhq
 * @description 新手引导登录服务类
 * @date 2019-12-27 03:42
 **/
//@Service
public class NewerGuideLoginService implements INewerGuideLoginService {
    @Autowired
    private GameUserService userService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private GodService godService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserSpecialService userSpecialService;
    @Autowired
    private UserCardGroupService userCardGroupService;

    /**
     * 进入游戏后的新手引导修正
     *
     * @param gu
     */
    @Override
    public void handleNewerGuideAsLogin(GameUser gu) {
       /* // 已经通过的修改新手引导的status值
        if (this.newerGuideService.isPassNewerGuide(gu.getId())) {
            UserNewerGuide userNewerGuide = newerGuideService.getUserNewerGuide(gu.getId());
            userNewerGuide.setNewerGuide(NewerGuideEnum.YE_GUAI.getStep());
            this.userService.updateItem(userNewerGuide);
        }
        UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(gu.getId());
        NewerGuideEnum guideEnum = NewerGuideEnum.fromValue(userNewerGuide.getNewerGuide());
        // userNewerGuide不存在，且涂山已经攻打下来了，直接跳过新手引导。没打下来的去起点开始
        if (null == guideEnum) {
            UserCity tuShan = userCityService.getUserCity(gu.getId(), 2046);
            guideEnum = null == tuShan ? NewerGuideEnum.START : NewerGuideEnum.YE_GUAI;
            newerGuideService.updateNewerGuide(gu.getId(), guideEnum, new RDCommon());
            gu.moveTo(guideEnum.getPos(), guideEnum.getDir());
        }
        switch (guideEnum) {
            case START:
                // 确保清除神仙
                Optional<UserGod> userGod = this.godService.getAttachGod(gu);
                userGod.ifPresent(god -> this.userService.deleteItem(god));
                // 删除所有卡牌
                List<UserCard> uCards = userCardService.getUserCards(gu.getId());
                CardEventPublisher.pubCardDelEvent(gu.getId(), uCards);
                gu.moveTo(NewerGuideEnum.START.getPos(), NewerGuideEnum.START.getDir());
                // 检查元素，不够就送
                checkEle(gu);
                // 清空默认卡组
                UserCardGroup cardGroup = userCardGroupService.getUsingGroup(gu.getId(), CardGroupWay.Normal_Fight);
                if (null != cardGroup) {
                    cardGroup.setCards(new ArrayList<>());
                    userService.updateItem(cardGroup);
                }
                break;
            case BIAN_ZHU_1:
                gu.moveTo(guideEnum.getPos(), guideEnum.getDir());
                break;
            case ATTACK_1:
            case JIAOYI:
                int cityId = guideEnum == NewerGuideEnum.ATTACK_1 ? 2046 : 1934;
                UserCity userCity = userCityService.getUserCity(gu.getId(), cityId);
                if (guideEnum == NewerGuideEnum.ATTACK_1) {
                    if (0 == userCity.getJxz()) {
                        userCity.setJxz(1);
                        this.userService.updateItem(userCity);
                        ResEventPublisher.pubExpAddEvent(gu.getId(), 300, WayEnum.JXZ_UPDATE, new RDCommon());
                    }
                    newerGuideService.updateNewerGuide(gu.getId(), NewerGuideEnum.JXZ, new RDCommon());
                    newerGuideService.sendTreasureToNum(gu.getId(), TreasureEnum.JU_XIAN_LING.getValue(), 10, WayEnum.JXZ_AWARD, new RDCommon());
                } else {
                    if (0 == userCity.getKc()) {
                        userCity.setKc(1);
                        this.userService.updateItem(userCity);
                        ResEventPublisher.pubExpAddEvent(gu.getId(), 300, WayEnum.KC_UPDATE, new RDCommon());
                    }
                    newerGuideService.updateNewerGuide(gu.getId(), NewerGuideEnum.KC, new RDCommon());
                    // 送一个金元素
                    ResEventPublisher.pubEleAddEvent(gu.getId(), TypeEnum.Gold.getValue(), 1, WayEnum.KC_AWARD, new RDCommon());
                }
                break;
            case YOU_SHANG_GUAN:
            case ATTACK_2:
                // 攻完城的状态，要判断身上特产，防止只卖了特产就重登的玩家
                if (guideEnum == NewerGuideEnum.ATTACK_2) {
                    List<UserSpecial> ownSpecials = userSpecialService.getOwnSpecials(gu.getId());
                    // 身上没特产，说明已经卖过特产了，直接帮他买 亚麻布
                    if (ListUtil.isEmpty(ownSpecials)) {
                        CfgSpecialEntity special = SpecialTool.getSpecialById(SpecialEnum.YM.getValue());
                        ResEventPublisher.pubCopperDeductEvent(gu.getId(), special.getPrice().longValue(), WayEnum.NONE, new RDCommon());
                        List<EVSpecialAdd> specialAdds = Collections.singletonList(new EVSpecialAdd(special.getId(), 100));
                        SpecialEventPublisher.pubSpecialAddEvent(gu.getId(), specialAdds, WayEnum.NONE, new RDCommon());
                        newerGuideService.updateNewerGuide(gu.getId(), NewerGuideEnum.JIAOYI, new RDCommon());
                    }
                }
                gu.moveTo(NewerGuideEnum.YOU_SHANG_GUAN.getPos(), NewerGuideEnum.YOU_SHANG_GUAN.getDir());
                break;
            case FD:
                // 玩家已到达福地，保证有一个七香车
                newerGuideService.sendTreasureToNum(gu.getId(), TreasureEnum.QXC.getValue(), 1, WayEnum.FD, new RDCommon());
                // 如果有诛仙剑，说明已经踩过百宝箱了，扣除百宝箱获得的资源，避免玩家刷奖励
                int num = userTreasureService.getTreasureNum(gu.getId(), TreasureEnum.ZXJ.getValue());
                if (num > 0) {
                    ResEventPublisher.pubCopperDeductEvent(gu.getId(), 30000L, WayEnum.NONE, new RDCommon());
                    ResEventPublisher.pubDiceDeductEvent(gu.getId(), 30, WayEnum.NONE, new RDCommon());
                    ResEventPublisher.pubEleDeductEvent(gu.getId(), TypeEnum.Earth.getValue(), 2, WayEnum.NONE, new RDCommon());
                    TreasureEventPublisher.pubTDeductEvent(gu.getId(), TreasureEnum.WNLS1.getValue(), 1, WayEnum.NONE, new RDCommon());
                    TreasureEventPublisher.pubTDeductEvent(gu.getId(), TreasureEnum.ZXJ.getValue(), 1, WayEnum.NONE, new RDCommon());
                }
                gu.moveTo(guideEnum.getPos(), guideEnum.getDir());
                break;
            case CARD_LEVEL_UP_3:
                // 防止野怪打一半退出
                gu.moveTo(NewerGuideEnum.CARD_LEVEL_UP_3.getPos(), NewerGuideEnum.CARD_LEVEL_UP_3.getDir());
                break;
            default:
                break;
        }*/
    }

    /**
     * 检查元素，不够就送，保证旧版本的新手引导没做完的玩家可以正常过
     *
     * @param gu
     */
    private void checkEle(GameUser gu) {
        Integer earthEle = gu.getEarthEle();
        int country = gu.getRoleInfo().getCountry();
        TypeEnum typeEnum = TypeEnum.fromValue(country);
        switch (typeEnum) {
            case Gold:
            case Wood:
            case Water:
            case Fire:
                sendEleToNum(gu.getId(), earthEle, TypeEnum.Earth.getValue(), 2);
                int ele = gu.getEleCount(typeEnum);
                sendEleToNum(gu.getId(), ele, typeEnum.getValue(), 3);
                break;
            case Earth:
                sendEleToNum(gu.getId(), earthEle, TypeEnum.Earth.getValue(), 5);
                break;
            default:
                break;
        }
    }

    /**
     * 如果元素数量没到num，则送元素到num数量
     *
     * @param uid
     * @param ele
     * @param type
     * @param num
     */
    private void sendEleToNum(long uid, Integer ele, int type, int num) {
        if (ele < num) {
            ResEventPublisher.pubEleAddEvent(uid, type, num - ele, WayEnum.NONE, new RDCommon());
        }
    }
}
