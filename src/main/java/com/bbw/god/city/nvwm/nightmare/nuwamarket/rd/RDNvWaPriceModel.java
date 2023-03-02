package com.bbw.god.city.nvwm.nightmare.nuwamarket.rd;

import com.bbw.god.city.nvwm.nightmare.nuwamarket.GoodsInfo;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.UserNvWaPriceModel;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 女娲集市模板信息
 *
 * @author fzj
 * @date 2022/6/6 9:01
 */
@Data
public class RDNvWaPriceModel extends RDSuccess {
    /** 价格模板信息 */
    private List<RDPriceModelInfo> priceModelInfos;

    @Data
    public static class RDPriceModelInfo {
        /** 编号 */
        private Integer modelId;
        /** 价格 */
        private List<RDAward> price;
    }

    public static RDNvWaPriceModel getInstance(UserNvWaPriceModel userNvWaPriceModel) {
        RDNvWaPriceModel rd = new RDNvWaPriceModel();
        List<UserNvWaPriceModel.ModelInfo> modelInfos = userNvWaPriceModel.getModelInfos();
        List<RDPriceModelInfo> rdPriceModelInfos = new ArrayList<>();
        for (UserNvWaPriceModel.ModelInfo modelInfo : modelInfos) {
            RDPriceModelInfo rdPriceModelInfo = new RDPriceModelInfo();
            rdPriceModelInfo.setModelId(modelInfo.getId());
            rdPriceModelInfo.setPrice(getRDAwards(modelInfo.getPrice()));
            rdPriceModelInfos.add(rdPriceModelInfo);
        }
        rd.setPriceModelInfos(rdPriceModelInfos);
        return rd;
    }

    private static List<RDAward> getRDAwards(List<GoodsInfo> goods) {
        List<Award> awards = GoodsInfo.getAwards(goods, AwardEnum.FB);
        return RDAward.getInstances(awards);
    }
}
