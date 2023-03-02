package com.bbw.god.game.sxdh.store;

import com.bbw.common.ID;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.sxdh.SxdhDateService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 战区购买记录,新赛季第一次访问预生成赛季所有记录
 *
 * @author suhq
 * @date 2020-04-24 09:34
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SxdhZoneMallRecord extends GameData implements Serializable {
    private static SxdhDateService sxdhDateService = SpringContextUtil.getBean(SxdhDateService.class);
    private static final long serialVersionUID = 1L;
    private Integer serverGroup;//区服组
    private int zoneType;// 战区ID
    private int mallId;//商品ID
    private int goodId;//商品ID
    private Integer num;// 购买数量
    private Date expireDate;// 过期时间

    /**
     * 动态goodId的商品（秘传）不可用该方法
     * @param serverGroup
     * @param zoneType
     * @param mallId
     * @return
     */
    public static SxdhZoneMallRecord instance(int serverGroup, int zoneType, int mallId) {
        CfgMallEntity mallEntity = MallTool.getMall(mallId);
        return instance(serverGroup,zoneType,mallId,mallEntity.getGoodsId());
    }

    public static SxdhZoneMallRecord instance(int serverGroup, int zoneType, int mallId, int goodId) {
        SxdhZoneMallRecord umr = new SxdhZoneMallRecord();
        umr.setId(ID.INSTANCE.nextId());
        umr.setServerGroup(serverGroup);
        umr.setZoneType(zoneType);
        umr.setMallId(mallId);
        umr.setGoodId(goodId);
        umr.setNum(0);
        umr.setExpireDate(sxdhDateService.getSxdhBuyResetDate());
        return umr;
    }

    public void addNum(int num) {
        this.num += num;
    }

    /**
     * 是否超过购买次数
     *
     * @return
     */
    public boolean ifOutOfLimit() {
        CfgMallEntity mall = MallTool.getMall(mallId);
        return this.num >= mall.getLimit();
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.SXDH_ZONE_MALL_RECORD;
    }
}
