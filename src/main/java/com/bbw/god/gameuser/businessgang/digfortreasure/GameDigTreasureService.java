package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.CoderException;
import com.bbw.god.game.data.GameDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全区服组挖宝
 *
 * @author: huanghb
 * @date: 2022/12/28 15:51
 */
@Slf4j
@Service
public class GameDigTreasureService {
    @Autowired
    private GameDataService gameDataService;
    /** 买量服id */
    private int BUY_NUM_GROUP_Id = 16;
    /** 神魔专服id */
    private int GODS_AND_DEMONS_GROUP_Id = 17;

    /**
     * 添加单个区服组挖宝信息信息
     *
     * @param gameDigTreasure
     */
    public void addGameDigTreasure(GameDigTreasure gameDigTreasure) {
        gameDataService.addGameData(gameDigTreasure);
    }

    /**
     * 批量添加全区服组挖宝信息
     *
     * @param gameDigTreasures
     */
    public void addGameDigTreasures(List<GameDigTreasure> gameDigTreasures) {
        gameDataService.addGameDatas(gameDigTreasures);
    }

    /**
     * 获得指定区服组挖宝信息
     *
     * @param serverGroup 区服组id
     * @return
     */
    public GameDigTreasure getGameDigTreasure(int serverGroup) {
        if (GODS_AND_DEMONS_GROUP_Id == serverGroup) {
            serverGroup = BUY_NUM_GROUP_Id;
        }
        int finalServerGroup = serverGroup;
        List<GameDigTreasure> digTreasures = getGameDigTreasuresByDate(DateUtil.now());
        if (ListUtil.isEmpty(digTreasures)) {
            throw CoderException.high(String.format("没有配置区服组id=%s的挖宝信息", serverGroup));
        }
        GameDigTreasure gameDigTreasure = digTreasures.stream().filter(tmp -> tmp.isMatchServerGroup(finalServerGroup)).findFirst().orElse(null);
        if (null == gameDigTreasure) {
            throw CoderException.high(String.format("没有配置区服组id=%s的挖宝信息", serverGroup));
        }
        return gameDigTreasure;
    }

    /**
     * 获得所有区服组挖宝信息
     *
     * @return
     */
    public List<GameDigTreasure> getGameDigTreasures() {
        return gameDataService.getGameDatas(GameDigTreasure.class);
    }

    /**
     * 获得指定时间全区服组挖宝信息
     *
     * @return
     */
    public List<GameDigTreasure> getGameDigTreasuresByDate(Date date) {
        List<GameDigTreasure> gameDigTreasures = getGameDigTreasures();
        return gameDigTreasures.stream().filter(tmp -> tmp.isMatchWeek(date)).collect(Collectors.toList());
    }
}
