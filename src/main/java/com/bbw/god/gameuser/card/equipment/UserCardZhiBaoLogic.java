package com.bbw.god.gameuser.card.equipment;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.equipment.data.UserCardXianJue;
import com.bbw.god.gameuser.card.equipment.data.UserCardZhiBao;
import com.bbw.god.gameuser.card.equipment.rd.RdCardZhiBao;
import com.bbw.god.gameuser.card.equipment.rd.RdWearingCondition;
import com.bbw.god.gameuser.kunls.CfgKunLSTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 卡牌至宝逻辑
 *
 * @author: huanghb
 * @date: 2022/9/15 14:16
 */
@Service
public class UserCardZhiBaoLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardZhiBaoService userCardZhiBaoService;
    @Autowired
    private UserCardXianJueService userCardXianJueService;
    @Autowired
    private UserCardService userCardService;

    /**
     * 灵宝穿戴
     *
     * @param cardId
     * @param zhiBaoId     至宝id
     * @param zhiBaoDataId 至宝数据id
     * @return
     */
    public RdCardZhiBao take(long uid, Integer cardId, Integer zhiBaoId, long zhiBaoDataId) {

        List<Integer> allZhiBaoType = CfgKunLSTool.getAllZhiBaoType();
        if (!allZhiBaoType.contains(zhiBaoId)) {
            throw new ExceptionForClientTip("zhiBao.is.error.type");
        }
        //检查装备是否存在
        UserCardZhiBao newUserCardZhiBao = userCardZhiBaoService.getUserCardZhiBao(uid, zhiBaoDataId);
        if (null == newUserCardZhiBao) {
            throw new ExceptionForClientTip("zhiBao.is.not.exist");
        }
        if (0 != newUserCardZhiBao.ifPutOn()) {
            throw new ExceptionForClientTip("zhiBao.is.putedOn");
        }
        Integer xianJueType = getXianJueType(zhiBaoId);
        //仙诀激活检测
        UserCardXianJue userCardXianJue = userCardXianJueService.getUserCardXianJue(uid, cardId, xianJueType);
        if (null == userCardXianJue) {
            throw ExceptionForClientTip.fromi18nKey("xianJue.is.not.active");
        }
        //品质检测
        Integer zhiBaoQuality = zhiBaoId % 100;
        Integer qualityLimit = userCardXianJue.getQuality();
        if (zhiBaoQuality > qualityLimit) {
            throw new ExceptionForClientTip("zhiBao.quality.limited");
        }
        UserCard userCard = userCardService.getUserCard(uid, cardId);
        if (!userCard.gainCard().getType().equals(newUserCardZhiBao.getProperty())) {
            throw new ExceptionForClientTip("zhiBao.type.limited");

        }
        //至宝类型
        int zhiBaoType = zhiBaoId / 100;
        UserCardZhiBao oldUserCardZhiBao = userCardZhiBaoService.getUserCardZhiBaoByZhiBaoType(uid, cardId, zhiBaoType);
        //是否已经穿戴
        if (null != oldUserCardZhiBao) {
            oldUserCardZhiBao.takeDown();
            userCardZhiBaoService.cacheZhiBao(oldUserCardZhiBao);
        }
        //穿戴
        newUserCardZhiBao.putOn(cardId);
        userCardZhiBaoService.cacheZhiBao(newUserCardZhiBao);
        return RdCardZhiBao.instance(newUserCardZhiBao);

    }

    /**
     * 获得至宝类型
     *
     * @param zhiBaoId
     * @return
     */
    private Integer getXianJueType(Integer zhiBaoId) {
        return zhiBaoId / 100;
    }

    /**
     * 灵宝取下
     *
     * @param uid
     * @param zhiBaoDataId
     * @return
     */
    public RdWearingCondition takeOff(long uid, long zhiBaoDataId) {
        //检查至宝是否存在
        UserCardZhiBao userCardZhiBao = userCardZhiBaoService.getUserCardZhiBao(uid, zhiBaoDataId);
        if (null == userCardZhiBao) {
            throw new ExceptionForClientTip("zhiBao.is.not.exist");
        }
        //至宝是否穿戴
        if (0 == userCardZhiBao.ifPutOn()) {
            throw new ExceptionForClientTip("zhiBao.is.not.putedOn");
        }
        userCardZhiBao.takeDown();
        userCardZhiBaoService.cacheZhiBao(userCardZhiBao);
        return RdWearingCondition.instance(userCardZhiBao.ifPutOn());
    }
}
