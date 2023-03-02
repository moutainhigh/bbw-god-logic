package com.bbw.god.gameuser.leadercard.fashion;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 时装监听器
 *
 * @author suhq
 * @date 2021-03-26 17:25
 **/
@Component
public class UserLeaderFashionListener {
    @Autowired
    private UserLeaderFashionService userLeaderFashionService;

    /** 时装 */
    private static final List<Integer> LEADER_FASHIONS = Arrays.asList(
            TreasureEnum.FASHION_ShiJNH.getValue(),
            TreasureEnum.FASHION_JinFSS.getValue(),
            TreasureEnum.FASHION_FENGQNS.getValue(),
            TreasureEnum.FASHION_CHENGFPL.getValue(),
            TreasureEnum.FASHION_LVYSZ.getValue()
    );

    /** 获取途径 */
    private static final List<WayEnum> WAYS = Arrays.asList(
            WayEnum.ACHIEVEMENT,
            WayEnum.WING_RUSTLE_BOX,
            WayEnum.FLOWER_TO_GOD,
            WayEnum.Mail,
            WayEnum.WORLD_CUP_ACTIVITIE_GUESS_SHOP
    );

    @EventListener
    public void activeFashion(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        //成就投放
        if (!WAYS.contains(ep.getWay())) {
            return;
        }
        //是否奖励时装
        Optional<EVTreasure> optional = ep.getAddTreasures().stream()
                .filter(tmp -> LEADER_FASHIONS.contains(tmp.getId()))
                .findFirst();
        if (!optional.isPresent()) {
            return;
        }
        Integer fashionId = optional.get().getId();
        Long guId = ep.getGuId();
        //不重复的时装
        UserLeaderFashion fashion = userLeaderFashionService.getFashion(guId, fashionId);
        if (null != fashion) {
            return;
        }
        fashion = UserLeaderFashion.getInstance(guId, fashionId);
        userLeaderFashionService.addUserLeaderFashion(fashion);
    }

}
