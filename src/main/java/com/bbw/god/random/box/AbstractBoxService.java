package com.bbw.god.random.box;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 基于配置的开宝箱、礼包服务
 *
 * @author: suhq
 * @date: 2022/2/14 10:50 上午
 */
@Slf4j
public abstract class AbstractBoxService {
    // 概率和
    private static final int PROP_SUM = 10000;
    @Autowired
    private AwardService awardService;

    /**
     * 获取奖励。如果是随机的，则返回随机后的奖励
     *
     * @param uid
     * @param boxId
     * @return
     */
    public List<Award> getAward(long uid, int boxId) {
        List<BoxGood> targetGoods = getBoxGoods(boxId);
        return toAwards(uid, targetGoods);
    }

    /**
     * 获取奖励。如果是随机的，则返回随机后的奖励
     *
     * @param uid
     * @param boxKey
     * @return
     */
    public List<Award> getAward(long uid, String boxKey) {
        List<BoxGood> targetGoods = getBoxGoods(boxKey);
        return toAwards(uid, targetGoods);
    }

    /**
     * 开箱子、礼包
     *
     * @param boxId
     * @param way
     * @param rd
     */
    public void open(long guId, int boxId, WayEnum way, RDCommon rd) {
        List<Award> awards = getAward(guId, boxId);
        // 发放奖励
        this.awardService.fetchAward(guId, awards, way, "", rd);
    }

    /**
     * 开多次礼包
     *
     * @param guId
     * @param boxId
     * @param num
     * @param way
     * @param rd
     */
    public void open(long guId, int boxId, int num, WayEnum way, RDCommon rd) {
        List<Award> awards = getAward(guId, boxId);
        awards.forEach(award -> award.setNum(award.getNum() * num));
        // 发放奖励
        this.awardService.fetchAward(guId, awards, way, "", rd);
    }


    /**
     * 更具策略ID获得箱子奖励
     *
     * @param boxId
     * @return
     */
    public List<BoxGood> getBoxGoods(int boxId) {
        return getBoxGoods(String.valueOf(boxId));
    }

    /**
     * 根据策略key获得箱子奖励
     *
     * @param boxkey
     * @return
     */
    public List<BoxGood> getBoxGoods(String boxkey) {
        BoxGoods srcBox = Cfg.I.get(boxkey, BoxGoods.class);
        if (srcBox == null) {
            return new ArrayList<>();
        }
        // 对元数据进行克隆，避免修改元数据
        BoxGoods box = CloneUtil.clone(srcBox);
        // System.out.println(box.toString());
        final List<BoxGood> goods = box.getGoods();
        List<BoxGood> targetGoods = new ArrayList<>();
        // goods.stream().forEach(good -> System.out.println("原始数据：" +
        // good.toString()));
        // 如果是随机箱子，则随机categoryNum个
        if (box.getIsRandom()) {
            // 概率集合
            List<Integer> props = goods.stream().map(BoxGood::getProp).collect(Collectors.toList());
            // 随机索引
            List<Integer> indexs = Stream.generate(() -> PowerRandom.getIndexByProbs(props, PROP_SUM)).distinct()
                    .limit(box.getCategoryNum()).collect(Collectors.toList());
            // 随机道具
            targetGoods = indexs.stream().map(index -> goods.get(index)).collect(Collectors.toList());
        } else {
            // 非随机，全物品
            targetGoods.addAll(goods);
        }
        return targetGoods;
    }

    /**
     * 根据箱子id获取宝箱配置的所有奖励
     *
     * @param boxId
     * @return
     */
    public List<Award> getBoxAllGoods(long uid, int boxId) {
        BoxGoods srcBox = Cfg.I.get(String.valueOf(boxId), BoxGoods.class);
        if (srcBox == null) {
            return new ArrayList<>();
        }
        // 对元数据进行克隆，避免修改元数据
        BoxGoods box = CloneUtil.clone(srcBox);
        return toAwards(uid, box.getGoods());
    }

    /**
     * 发放多个奖励
     *
     * @param uid
     * @param awards
     */
    public void sendBoxAwards(long uid, List<Award> awards, WayEnum way, RDCommon rd) {
        List<Award> awardList = new ArrayList<>();
        Map<Integer, List<Award>> awardsByItem = awards.stream().collect(Collectors.groupingBy(Award::getItem));
        for (Map.Entry<Integer, List<Award>> awardByItemList : awardsByItem.entrySet()){
            AwardEnum awardEnum = AwardEnum.fromValue(awardByItemList.getKey());
            if (null == awardEnum){
                continue;
            }
            Map<Integer, Integer> boxAwards = awardByItemList.getValue().stream()
                    .collect(Collectors.groupingBy(Award::getAwardId, Collectors.summingInt(Award::getNum)));
            awardList.addAll(Award.getAwards(boxAwards, awardEnum.getValue()));
        }
        awardService.fetchAward(uid, awardList, way, "", rd);
    }

    /**
     * 将物品转换为奖励
     *
     * @param goods
     * @return
     */
    public List<Award> toAwards(long uid, List<BoxGood> goods) {
        List<Award> awards = new ArrayList<>();
        if (ListUtil.isEmpty(goods)) {
            return awards;
        }
        for (BoxGood good : goods) {
            String goodInfo = good.getGood();
            AwardEnum awardType = AwardEnum.fromValue(good.getItem());
            switch (awardType) {
                case YB:
                case TQ:
                case TL:
                    awards.add(new Award(awardType, good.getNum()));
                    break;
                case KP:
                    List<Award> cardAwards = toCardAwards(uid, good);
                    if (ListUtil.isNotEmpty(cardAwards)) {
                        awards.addAll(cardAwards);
                    }
                    break;
                case YS:
                    int eleType = 0;
                    if (!goodInfo.endsWith("随机")) {
                        eleType = TypeEnum.fromName(goodInfo).getValue();
                    }
                    awards.add(new Award(eleType, awardType, good.getNum()));
                    break;
                case FB:
                    int treasureId = 0;
                    if (goodInfo.endsWith("万能灵石")) {
                        int star = PowerRandom.getIndexByProbs(good.getStarProps(), PROP_SUM) + 1;
                        treasureId = 800 + star * 10;
                    } else if (goodInfo.endsWith("法宝")) {
                        int star = PowerRandom.getIndexByProbs(good.getStarProps(), PROP_SUM) + 1;
                        treasureId = TreasureTool.getRandomOldTreasure(star).getId();
                    } else {
                        treasureId = TreasureTool.getTreasureByName(goodInfo).getId();
                    }
                    awards.add(new Award(treasureId, awardType, good.getNum()));
                    break;
                default:
                    break;
            }
        }
        return awards;
    }

    /**
     * 将卡牌物品转换成卡牌奖励。如果是基于策略的卡牌，则直接返回执行策略后的卡牌
     *
     * @param cardGood
     * @return
     */
    public abstract List<Award> toCardAwards(long uid, BoxGood cardGood);


    /**
     * 是否是基于配置的宝箱或者礼包
     *
     * @param boxId
     * @return
     */
    public boolean isBox(int boxId) {
        BoxGoods srcBox = Cfg.I.get(String.valueOf(boxId), BoxGoods.class);
        return srcBox != null;
    }
}
