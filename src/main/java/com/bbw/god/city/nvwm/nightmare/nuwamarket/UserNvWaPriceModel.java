package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 女娲集市价格模板
 *
 * @author fzj
 * @date 2022/6/6 8:57
 */
@Data
public class UserNvWaPriceModel extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 模板信息 */
    private List<ModelInfo> modelInfos = new ArrayList<>();


    @Data
    public static class ModelInfo {
        /** 编号 */
        private Integer id;
        /** 价格 */
        private List<GoodsInfo> price = new ArrayList<>();
    }

    public static UserNvWaPriceModel getInstance(long uid) {
        UserNvWaPriceModel userNvWaPriceModel = new UserNvWaPriceModel();
        userNvWaPriceModel.setId(ID.INSTANCE.nextId());
        userNvWaPriceModel.setGameUserId(uid);
        return userNvWaPriceModel;
    }

    /**
     * 更新模板
     *
     * @param priceModel
     */
    public final void updatePriceModel(String priceModel) {
        String[] priceWays = priceModel.split(";");
        List<ModelInfo> modelInfos = new ArrayList<>();
        for (int i = 0; i < priceWays.length; i++) {
            String priceInfo = priceWays[i];
            String[] price = priceInfo.split(",");
            ModelInfo modelInfo = new ModelInfo();
            modelInfo.setId(i);
            for (String priceNums : price) {
                int priceId = Integer.parseInt(priceNums.split("_")[0]);
                int priceNum = Integer.parseInt(priceNums.split("_")[1]);
                GoodsInfo award = new GoodsInfo(priceId, priceNum);
                modelInfo.getPrice().add(award);
            }
            modelInfos.add(modelInfo);
        }
        this.modelInfos = modelInfos;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_NV_WA_PRICE_MODEL;
    }
}
