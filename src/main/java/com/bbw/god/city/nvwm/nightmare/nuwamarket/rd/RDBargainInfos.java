package com.bbw.god.city.nvwm.nightmare.nuwamarket.rd;

import com.bbw.common.DateUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.GameNvWaBooth;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.GameNvWaMarketBargain;
import com.bbw.god.city.nvwm.nightmare.nuwamarket.GoodsInfo;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 讨价还价信息
 *
 * @author fzj
 * @date 2022/6/8 15:09
 */
@Data
public class RDBargainInfos extends RDCommon {
    private static GameUserService gameUserService = SpringContextUtil.getBean(GameUserService.class);

    /** 讨价 */
    private List<RDBargain> bargain = new ArrayList<>();
    /** 还价 */
    private List<RDBargain> counterOffer = new ArrayList<>();

    @Data
    public static class RDBargain {
        /** 要价id */
        private Long bargainId;
        /** 发起玩家 */
        private String sponsor;
        /** 摊位编号 */
        private Integer boothNo;
        /** 头像 */
        private Integer head;
        /** 讨价商品 */
        private List<RDAward> product;
        /** 出价 */
        private List<RDAward> price;
        /** 状态 */
        private Integer status;
        /** 过期时间 */
        private Long expireTime;
        /** 留言 */
        private String message;
    }

    public static RDBargain getInstance(GameNvWaMarketBargain bargain, GoodsInfo product, GameNvWaBooth booth) {
        RDBargain rd = new RDBargain();
        long userId = bargain.getSponsor();
        if (null != booth) {
            userId = booth.getUid();
        }
        GameUser gameUser = gameUserService.getGameUser(userId);
        GameUser.RoleInfo roleInfo = gameUser.getRoleInfo();
        String sponsor = ServerTool.getServerShortName(gameUser.getServerId()) + "·" + roleInfo.getNickname();
        rd.setSponsor(sponsor);
        rd.setBargainId(bargain.getId());
        rd.setBoothNo(bargain.getBoothNo());
        rd.setHead(roleInfo.getHead());
        rd.setExpireTime(DateUtil.millisecondsInterval(bargain.getExpireTime(), DateUtil.now()));
        rd.setMessage(bargain.getMessage());
        rd.setPrice(getRDAwards(bargain.getPrice()));
        rd.setMessage(bargain.getMessage());
        rd.setStatus(bargain.getStatus());
        rd.setProduct(getRDAwards(product));
        return rd;
    }


    private static List<RDAward> getRDAwards(List<GoodsInfo> goods) {
        List<Award> awards = GoodsInfo.getAwards(goods, AwardEnum.FB);
        return RDAward.getInstances(awards);
    }

    private static List<RDAward> getRDAwards(GoodsInfo goods) {
        List<Award> awardList = new ArrayList<>();
        awardList.add(new Award(goods.getId(), AwardEnum.FB, goods.getNum()));
        return RDAward.getInstances(awardList);
    }
}
