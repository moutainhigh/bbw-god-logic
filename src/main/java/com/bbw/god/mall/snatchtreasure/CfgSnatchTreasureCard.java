package com.bbw.god.mall.snatchtreasure;

import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.random.box.BoxGood;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 夺宝卡牌配置类
 * @date 2020/8/21 9:47
 **/
@Data
public class CfgSnatchTreasureCard implements CfgInterface, Serializable {
    private static final long serialVersionUID = 9207497730181278091L;
    private String key;
    private String desc;// 箱子、礼包描述
    private List<BoxGood> goods;// 物品集

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
