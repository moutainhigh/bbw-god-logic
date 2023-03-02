package com.bbw.god.city.nvwm.nightmare.nuwamarket;

import com.bbw.common.DateUtil;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.marketenum.BargainProductEnum;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 女娲集市还价信息
 *
 * @author fzj
 * @date 2022/5/25 17:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GameNvWaMarketBargain extends GameData implements Serializable {
    private static final long serialVersionUID = 3352313347452828250L;
    /** 发起玩家 */
    private long sponsor;
    /** 摊位编号 */
    private Integer boothNo;
    /** 讨价商品编号 */
    private Long productId;
    /** 商品 */
    private GoodsInfo product;
    /** 出价 */
    private List<GoodsInfo> price;
    /** 状态 */
    private Integer status = BargainProductEnum.UNDECIDED.getValue();
    /** 过期时间 */
    private Date expireTime;
    /** 是否处理 */
    private boolean isDealWith = false;
    /** 留言 */
    private String message;

    public static GameNvWaMarketBargain getInstance(long bargainId, long sponsor, int boothNo, long bargainProductNo, GoodsInfo product, Date expireTime, List<GoodsInfo> price, String message) {
        GameNvWaMarketBargain nvWaMarketBargain = new GameNvWaMarketBargain();
        nvWaMarketBargain.setId(bargainId);
        nvWaMarketBargain.setSponsor(sponsor);
        nvWaMarketBargain.setBoothNo(boothNo);
        nvWaMarketBargain.setProductId(bargainProductNo);
        nvWaMarketBargain.setProduct(product);
        nvWaMarketBargain.setExpireTime(expireTime);
        nvWaMarketBargain.setPrice(price);
        nvWaMarketBargain.setMessage(message);
        return nvWaMarketBargain;
    }

    /**
     * 更新状态
     */
    public void updateBargainStatus(int status) {
        BargainProductEnum value = BargainProductEnum.fromValue(status);
        this.setStatus(value.getValue());
        this.setDealWith(true);
    }

    /**
     * 更新过期状态
     */
    public void updateBargainStatus() {
        Date expireTime = this.expireTime;
        if (DateUtil.now().after(expireTime)) {
            this.setStatus(BargainProductEnum.EXPIRED.getValue());
        }
    }

    /**
     * 检查是否过期
     *
     * @return
     */
    public boolean isExpired(){
        return DateUtil.now().after(this.expireTime);
    }


    @Override
    public GameDataType gainDataType() {
        return GameDataType.NV_WA_MARKET_BARGAIN;
    }
}
