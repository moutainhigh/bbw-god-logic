package com.bbw.god.city.nvwm.nightmare.nuwamarket.rd;

import com.bbw.common.DateUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.GameNvWaBooth;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.GoodsInfo;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 返回客户端女娲集市数据
 *
 * @author fzj
 * @date 2022/5/9 9:02
 */
@Data
public class RDNvWaMarketInfos extends RDSuccess {
    private static GameUserService gameUserService = SpringContextUtil.getBean(GameUserService.class);

    /** 摊位信息 */
    private List<RDNvWaMarketInfo> rdNvWaMarketInfos;
    /** 总摊位数 */
    private Integer totalBoothNum;

    @Data
    public static class RDNvWaMarketInfo {
        /** 摊位编号 */
        private Integer boothNum;
        /** 摊位状态 */
        private Integer status;
        /** 标语 */
        private String message = "";
        /** 商品Id */
        private List<Integer> treasureId;
        /** 剩余时间 */
        private Long remainTime;
        /** 是否自己摊位 */
        private boolean isUserBooth = false;
        /** 性别 1男 2女 */
        private Integer sex;
    }

    public static RDNvWaMarketInfo getInstance(GameNvWaBooth gameNuWaMarket) {
        RDNvWaMarketInfo rd = new RDNvWaMarketInfo();
        rd.setBoothNum(gameNuWaMarket.getBoothNo());
        rd.setStatus(gameNuWaMarket.getBoothStatus());
        rd.setMessage(gameNuWaMarket.getSlogan());
        List<GoodsInfo> goodsInfos = gameNuWaMarket.getProductInfos().stream().filter(p -> p.getGoods().getNum() > 0)
                .map(GameNvWaBooth.ProductInfo::getGoods).collect(Collectors.toList());
        List<Integer> treasureIds = goodsInfos.stream().map(GoodsInfo::getId).collect(Collectors.toList());
        rd.setTreasureId(treasureIds);
        Date leaseEndTime = gameNuWaMarket.getLeaseEndTime();
        rd.setRemainTime(DateUtil.millisecondsInterval(leaseEndTime, DateUtil.now()));
        GameUser.RoleInfo roleInfo = gameUserService.getGameUser(gameNuWaMarket.getUid()).getRoleInfo();
        rd.setSex(roleInfo.getSex());
        return rd;
    }
}
