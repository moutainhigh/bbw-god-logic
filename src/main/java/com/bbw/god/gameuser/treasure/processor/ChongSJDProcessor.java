package com.bbw.god.gameuser.treasure.processor;

import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 重塑金丹
 * 更换性别后，头像、分身卡牌立绘、地图模型需更改为对应性别的素材。
 *
 * @author: suhq
 * @date: 2021/8/17 3:46 下午
 */
@Service
public class ChongSJDProcessor extends TreasureUseProcessor {
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private GameUserService gameUserService;

    public ChongSJDProcessor() {
        this.treasureEnum = TreasureEnum.CHONG_SJD;
        this.isAutoBuy = false;
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        Long uid = gu.getId();
        //设置性别
        int newSex = (gu.getRoleInfo().getSex() + 1) % 2;
        newSex = newSex == 0 ? 2 : 1;
        gu.getRoleInfo().setSex(newSex);
        //设置头像
        int head = newSex == 1 ? 1 : 51;
        gu.getRoleInfo().setHead(head);
        gu.updateRoleInfo();
        //更新主句卡性别
        Optional<UserLeaderCard> userLeaderCardOp = leaderCardService.getUserLeaderCardOp(uid);
        if (!userLeaderCardOp.isPresent()) {
            return;
        }
        UserLeaderCard leaderCard = userLeaderCardOp.get();
        leaderCard.setSex(newSex);
        gameUserService.updateItem(leaderCard);
    }

}
