package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.RDAward;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 物品信息
 *
 * @author fzj
 * @date 2022/5/31 15:35
 */
@Data
public class GoodsInfo implements Serializable {
    private static final long serialVersionUID = -7020464926324448451L;
    /** id */
    private Integer id;
    /** 数量 */
    private Integer num;

    private GoodsInfo() {
    }

    public static GoodsInfo getInstance(int id, int num) {
        GoodsInfo goods = new GoodsInfo();
        goods.setId(id);
        goods.setNum(num);
        return goods;
    }

    public GoodsInfo(int id, int num) {
        this.id = id;
        this.num = num;
    }

    public static List<Award> getAwards(List<GoodsInfo> goods, AwardEnum awardEnum) {
        List<Award> awardList = new ArrayList<>();
        for (GoodsInfo goodsInfo : goods) {
            awardList.add(new Award(goodsInfo.getId(), awardEnum, goodsInfo.getNum()));
        }
        return awardList;
    }

    public static List<GoodsInfo> getGoods(String goods) {
        List<GoodsInfo> goodsInfoList = new ArrayList<>();
        String[] goodsInfos = goods.split(",");
        for (String goodsInfo : goodsInfos) {
            int goodId = Integer.parseInt(goodsInfo.split("_")[0]);
            int num = Integer.parseInt(goodsInfo.split("_")[1]);
            GoodsInfo g = new GoodsInfo();
            g.setId(goodId);
            g.setNum(num);
            goodsInfoList.add(g);
        }
        return goodsInfoList;
    }
}
