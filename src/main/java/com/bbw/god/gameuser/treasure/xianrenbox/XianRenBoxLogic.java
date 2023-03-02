package com.bbw.god.gameuser.treasure.xianrenbox;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 仙人遗落的袋子
 *
 * @author lwb
 * @date 2020/8/12 16:37
 */
@Service
public class XianRenBoxLogic {
    @Autowired
    private GameUserService gameUserService;

    /**
     * 根据法宝存储的ID获取 对应的仙人袋子的玩家记录
     *
     * @param treasureDataId
     * @return
     */
    public UserXianRenBox getXianRenBox(long uid, long treasureDataId) {
        List<UserXianRenBox> boxes = gameUserService.getMultiItems(uid, UserXianRenBox.class);
        List<UserXianRenBox> list = boxes.stream().filter(p -> p.getTreasureDataId() == treasureDataId).collect(Collectors.toList());
        if (list.size() == 1) {
            //有一个 则返回当前的
            return list.get(0);
        } else if (list.size() > 1) {
            //有多个默认优先返回未开启的那个,都开启则返回第一个
            int today = DateUtil.getTodayInt();
            Optional<UserXianRenBox> first = list.stream().filter(p -> p.getLastOpenDate() != today).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
            return list.get(0);
        }
        //没有对应的记录 需要重新初始化一个补充
        UserXianRenBox userXianRenBox = UserXianRenBox.instance(uid, treasureDataId);
        userXianRenBox.setAwardsList(initAwards());
        gameUserService.addItem(uid, userXianRenBox);
        return userXianRenBox;
    }

    /**
     * 初始化奖励顺序
     *
     * @return
     */
    public List<Integer> initAwards() {
        CfgXianRenBox cfgXianRenBox = Cfg.I.getUniqueConfig(CfgXianRenBox.class);
        List<Integer> ids = cfgXianRenBox.getAwardsPool().stream().map(CfgXianRenBox.BoxAward::getId).collect(Collectors.toList());
        PowerRandom.shuffle(ids);
        for (CfgXianRenBox.BoxSetting setting : cfgXianRenBox.getSetting()) {
            int index = ids.indexOf(setting.getId());
            if (setting.valid(index)) {
                continue;
            }
            int newIndex = PowerRandom.getRandomBetween(setting.getMinIndex(), setting.getMaxIndex()) - 1;
            Collections.swap(ids, index, newIndex);
        }
        return ids;
    }

    /**
     * 获取具体的奖励
     *
     * @param awardIndexId
     * @return
     */
    public List<Award> getAward(int awardIndexId) {
        CfgXianRenBox cfgXianRenBox = Cfg.I.getUniqueConfig(CfgXianRenBox.class);
        Optional<CfgXianRenBox.BoxAward> optional = cfgXianRenBox.getAwardsPool().stream().filter(p -> p.getId() == awardIndexId).findFirst();
        if (!optional.isPresent()) {
            throw new ExceptionForClientTip("xianrenbox.open.error");
        }
        CfgXianRenBox.BoxAward boxAward = optional.get();
        List<Award> awards = ListUtil.copyList(boxAward.getAwards(), Award.class);
        return awards;
    }
}
