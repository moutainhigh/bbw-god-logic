package com.bbw.god.detail.async;

import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsCardDrawDetailEntity;
import com.bbw.god.db.service.InsCardDrawDetailService;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserLoginInfo;
import com.bbw.god.gameuser.pay.UserReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽卡明细
 *
 * @author: suhq
 * @date: 2021/12/16 11:52 上午
 */
@Slf4j
@Async
@Component
public class CardDrawDetailAsyncHandler {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private InsCardDrawDetailService insCardDrawDetailService;

    /**
     * 记录明细
     *
     * @param ep
     */
    public void log(CardDrawDetailEventParam ep) {
        try {
            addDetail(ep);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void addDetail(CardDrawDetailEventParam ep) {
        long guId = ep.getUid();
        InsCardDrawDetailEntity detailEntity = new InsCardDrawDetailEntity();
        //区服信息
        CfgServerEntity server = gameUserService.getOriServer(guId);
        detailEntity.setServerGroup(server.getGroupId());
        detailEntity.setSid(server.getId());
        //玩家信息
        GameUser gameUser = gameUserService.getGameUser(guId);
        detailEntity.setUid(guId);
        detailEntity.setGuLv(gameUser.getLevel());
        //充值金额
        int rechargeAmount = 0;
        List<UserReceipt> userReceipts = gameUserService.getMultiItems(guId, UserReceipt.class);
        if (ListUtil.isNotEmpty(userReceipts)) {
            rechargeAmount = userReceipts.stream().mapToInt(UserReceipt::getPrice).sum();
            detailEntity.setLastRechargeTime(userReceipts.get(userReceipts.size() - 1).getDeliveryTime());
        }
        detailEntity.setRechargeAmount(rechargeAmount);
        //购买信息
        String results = "";
        String newCardsInfo = "-";
        int maxStar = 0;
        int newCardNum = 0;
        if (ListUtil.isNotEmpty(ep.getAddCards())) {
            List<CfgCardEntity> cards = ep.getAddCards().stream().map(tmp -> CardTool.getCardById(tmp.getCardId())).collect(Collectors.toList());
            List<CfgCardEntity> newCards = ep.getAddCards().stream().filter(tmp -> tmp.isNew()).map(tmp -> CardTool.getCardById(tmp.getCardId())).distinct().collect(Collectors.toList());
            maxStar = cards.stream().max(Comparator.comparing(CfgCardEntity::getStar)).get().getStar();
            newCardNum = newCards.size();
            results = cards.stream().map(CfgCardEntity::getName).collect(Collectors.joining(","));
            newCardsInfo = newCards.stream().map(CfgCardEntity::getName).collect(Collectors.joining(","));
            newCardsInfo = StrUtil.isBlank(newCardsInfo) ? "-" : newCardsInfo;
        }
        if (ListUtil.isNotEmpty(ep.getTreasures())) {
            String treasureInfos = ep.getTreasures().stream().map(tmp -> TreasureTool.getTreasureById(tmp.getId()).getName() + "*" + tmp.getNum()).collect(Collectors.joining(","));
            if (StrUtil.isNotBlank(treasureInfos)) {
                if (StrUtil.isNotBlank(results)) {
                    results += ",";
                }
                results += treasureInfos;
            }
        }
        detailEntity.setDrawNum(ep.getDrawTimes());

        detailEntity.setResult(results);
        detailEntity.setNewCards(newCardsInfo);
        detailEntity.setNewCardsNum(newCardNum);
        detailEntity.setMaxStar(maxStar);
        detailEntity.setWay(ep.getWay().getValue());
        detailEntity.setWayName(ep.getWay().getName());

        //自角色创建时间
        setTimeSinceRoleCreate(guId, detailEntity);

        insCardDrawDetailService.insert(detailEntity);
    }

    /**
     * 自角色创建时间
     *
     * @param uid
     * @param detailEntity
     * @return
     */
    private String setTimeSinceRoleCreate(long uid, InsCardDrawDetailEntity detailEntity) {
        UserLoginInfo loginInfo = gameUserService.getSingleItem(uid, UserLoginInfo.class);
        Long seconds = (System.currentTimeMillis() - loginInfo.getEnrollTime().getTime()) / 1000;
        int minutes = seconds.intValue() / 60;
        if (seconds % 60 > 0) {
            minutes++;
        }
        String timeStr = "";
        int date = minutes / (24 * 60);
        int hour = (minutes % (24 * 60)) / 60;
        int minute = minutes % 60;
        timeStr = date + "天" + hour + "时" + minute + "分";
        detailEntity.setRoleLifeMinutes(minutes);
        detailEntity.setRoleLife(timeStr);
        return timeStr;
    }

}
