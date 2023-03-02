package com.bbw.god.mall.processor;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.FavorableBagEnum;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.random.box.BoxService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 特惠礼包
 *
 * @author suhq
 * @date 2018年12月6日 上午10:58:36
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FavorableMallProcessor extends AbstractMallProcessor {
    // 不显示的商品
    private final List<Integer> excludes = Arrays.asList();

    @Autowired
    private MallService mallService;
    @Autowired
    private BoxService boxService;
    @Autowired
    private HolidayLimitTimeMallProcessor holidayLimitTimeMallProcessor;
    @Autowired
    private HolidayLimitTimeMall51Processor holidayLimitTimeMall51Processor;

    FavorableMallProcessor() {
        this.mallType = MallEnum.THLB;
    }

    @Override
    public RDMallList getGoods(long guId) {
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getFavorableMalls();
        fMalls = fMalls.stream().filter(m -> !this.excludes.contains(m.getGoodsId())).collect(Collectors.toList());
        RDMallList rd = new RDMallList();
        toRdMallList(guId, fMalls, false, rd);
        //添加节日限时礼包
        RDMallList rdMallList = holidayLimitTimeMallProcessor.getGoods(guId);
        if (ListUtil.isNotEmpty(rdMallList.getMallGoods())) {
            rd.getMallGoods().addAll(rdMallList.getMallGoods());
        }
        //添加节日限时礼包-parentType51
        RDMallList limitTimeMall51 = holidayLimitTimeMall51Processor.getGoods(guId);
        if (ListUtil.isNotEmpty(limitTimeMall51.getMallGoods())) {
            rd.getMallGoods().addAll(limitTimeMall51.getMallGoods());
        }
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int goodId = mall.getGoodsId();
        FavorableBagEnum bag = FavorableBagEnum.fromValue(goodId);
        WayEnum way = WayEnum.fromName(bag.getName());
        // 可大量购买的礼包(非随机礼包)
        if (buyNum > 5 || bag == FavorableBagEnum.SSDLB) {
            boxService.open(guId, goodId, buyNum, way, rd);
            return;
        }
        for (int i = 0; i < buyNum; i++) {
            this.boxService.open(guId, goodId, way, rd);
        }
    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        // 读取类型为特惠礼包的UserMallRecord
        // TODO:可能会有性能问题
        List<UserMallRecord> favorableRecords = this.mallService.getUserMallRecord(guId, this.mallType);
        List<UserMallRecord> validRecords = favorableRecords.stream().filter(umr -> umr.ifValid())
                .collect(Collectors.toList());
        return validRecords;
    }
}
