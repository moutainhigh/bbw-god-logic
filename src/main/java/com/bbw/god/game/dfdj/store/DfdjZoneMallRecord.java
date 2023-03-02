package com.bbw.god.game.dfdj.store;

import com.bbw.common.ID;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.dfdj.DfdjDateService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 战区购买记录,新赛季第一次访问预生成赛季所有记录
 *
 * @author suhq
 * @date 2021-01-08 16:08
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DfdjZoneMallRecord extends GameData implements Serializable {
    private static DfdjDateService dfdjDateService = SpringContextUtil.getBean(DfdjDateService.class);
    private static final long serialVersionUID = 1L;
    private Integer serverGroup;//区服组
    private int zoneType;// 战区ID
    private int mallId;//商品ID
    private int goodId;//商品ID
    private Integer num;// 购买数量
    private Date expireDate;// 过期时间

    public static DfdjZoneMallRecord instance(int serverGroup, int zoneType, int mallId, int goodId) {
        DfdjZoneMallRecord umr = new DfdjZoneMallRecord();
        umr.setId(ID.INSTANCE.nextId());
        umr.setServerGroup(serverGroup);
        umr.setZoneType(zoneType);
        umr.setMallId(mallId);
        umr.setGoodId(goodId);
        umr.setNum(0);
        umr.setExpireDate(dfdjDateService.getDfdjBuyResetDate());
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
        return GameDataType.DFDJ_ZONE_MALL_RECORD;
    }
}
