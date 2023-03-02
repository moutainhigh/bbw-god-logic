package com.bbw.god.mall.processor;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.city.UserCityService;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMall;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.mall.*;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 神秘商品
 *
 * @author suhq
 * @date 2018年12月6日 上午10:58:36
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MysteriousMallProcessor extends AbstractMallProcessor {
    @Autowired
    private MallService mallService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserCityService userCityService;

    MysteriousMallProcessor() {
        this.mallType = MallEnum.SM;
    }

    @Override
    public RDMallList getGoods(long guId) {
        GameUser gu = gameUserService.getGameUser(guId);
        List<UserMallRecord> records = getUserMallRecords(guId);
        // 每日第一次点击自动刷新神秘，不算近刷新次数
        if (records.size() == 0) {
            records = generateMysteriousTreasures(gu);
        } else {
            boolean isOldData = records.stream().anyMatch(tmp -> MallTool.getMall(tmp.getBaseId()) == null);
            if (isOldData) {
                records = generateMysteriousTreasures(gu);
            }
        }
        // 已更新次数
        int refreshTimes = 0;
        UserMallRefreshRecord umrRecord = getTodayRefreshRecord(guId);
        if (umrRecord != null) {
            refreshTimes = umrRecord.getMysteriousRefreshTimes();
        }
        // 排序列表
        sortUserMallRecords(records);
        // 设置返回给客户端的数据
        return setRdByRecords(guId, records, refreshTimes);
    }

    /**
     * 刷新神秘物品
     *
     * @param guId
     * @return
     */
    public RDMallList refreshMysterious(long guId) {

        CfgMall config = MallTool.getMallConfig();
        int needGold = config.getMyteriousRefreshGold();
        GameUser gu = gameUserService.getGameUser(guId);
        // 是否元宝足够
        ResChecker.checkGold(gu, needGold);

        Integer limit = config.getMysteriousRefreshLimit();
        UserMallRefreshRecord umrRecord = getTodayRefreshRecord(guId);
        if (umrRecord != null) {
            // 检查刷新次数
            if (umrRecord.getMysteriousRefreshTimes() >= limit) {
                throw new ExceptionForClientTip("mall.unable.refreshMysterious", limit.toString());
            }
            umrRecord.addMysteriousRefreshTimes();
            gameUserService.updateItem(umrRecord);
        } else {
            umrRecord = UserMallRefreshRecord.instance(guId);
            gameUserService.addItem(guId, umrRecord);
        }
        int refreshTimes = umrRecord.getMysteriousRefreshTimes();
        List<UserMallRecord> records = generateMysteriousTreasures(gu);
        // 排序列表
        sortUserMallRecords(records);
        RDMallList rd = setRdByRecords(guId, records, refreshTimes);
        ResEventPublisher.pubGoldDeductEvent(guId, needGold, WayEnum.MALL_REFRESH_MYSTERIOUS, rd);
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int proId = mall.getGoodsId();
        Integer num = mall.getNum() * buyNum;
        if (proId == TreasureEnum.TQD.getValue()) {
            ResEventPublisher.pubCopperAddEvent(guId, 50000 * num, WayEnum.OPEN_TongQD, rd);
        } else if (proId == TreasureEnum.YSD.getValue()) {
            ResEventPublisher.pubEleAddEvent(guId, 10 * num, WayEnum.OPEN_YuanSD, rd);
        } else if (mall.getItem() == AwardEnum.TQ.getValue()) {
            ResEventPublisher.pubCopperAddEvent(guId, num, WayEnum.MALL_BUY, rd);
        } else if (mall.getItem() == AwardEnum.TL.getValue()) {
            ResEventPublisher.pubDiceAddEvent(guId, num, WayEnum.MALL_BUY, rd);
        } else {
            TreasureEventPublisher.pubTAddEvent(guId, proId, num, WayEnum.MALL_BUY, rd);
        }
    }

    /**
     * 今日购买纪录
     *
     * @param guId
     * @return
     */
    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        // 读取今天类型为神秘的UserMallRecord
        // TODO:可能有性能问题
        List<UserMallRecord> smRecords = mallService.getUserMallRecord(guId, MallEnum.SM);
        smRecords = smRecords.stream().filter(record -> DateUtil.isToday(record.getDateTime())).sorted(Comparator.comparingLong(UserMallRecord::getId).reversed()).limit(MallTool.getMallConfig().getMyteriousNum())
                .collect(Collectors.toList());
        Collections.reverse(smRecords);
        return smRecords;
    }

    /**
     * 随机神秘道具
     *
     * @param gu
     * @return
     */
    private List<UserMallRecord> generateMysteriousTreasures(GameUser gu) {
        // 获取随机商品
        List<UserMallRecord> records = getRandomUserMallRecords(gu);
        // 缓存
        mallService.addRecords(records);
        return records;
    }

    private List<UserMallRecord> getRandomUserMallRecords(GameUser gu) {
        List<CfgMall.MysteriousMallProb> mysteriousMallProbs = getMysteriousMallProbs(gu);
        Collections.shuffle(mysteriousMallProbs);
        //商品按三部分分组
        Map<Integer, List<CfgMall.MysteriousMallProb>> probsGroup = mysteriousMallProbs.stream().collect(Collectors.groupingBy(CfgMall.MysteriousMallProb::getPart));
        // 获取神秘商品分组part
        List<CfgMall.MallPartNum> partNums = MallTool.getMallConfig().getMallPartNums();

        // 添加list<String>（排序需要） 结构 UserMallRecord的id拼接',' 拼接 serial商品排序信息
        List<String> sortRules = new ArrayList<>();
        //根据随机结果构造UserMallRecord集合
        List<UserMallRecord> results = new ArrayList<>();
        for (Integer part : probsGroup.keySet()) {
            // 获取part分组 对应商品数量num
            CfgMall.MallPartNum prodPartNum = partNums.stream().filter(prodPart ->
                    prodPart.getPart().equals(part)).findFirst().orElse(null);
            // 商品分组数量
            assert prodPartNum != null;
            int partNum = prodPartNum.getNum();

            //按part获取商品列表
            List<CfgMall.MysteriousMallProb> tobeRamPro = probsGroup.get(part);
            // 获取固定商品 probability为0则为固定商品
            List<CfgMall.MysteriousMallProb> intrinsicProbList = tobeRamPro.stream().filter(mallProb ->
                    mallProb.getProbability().equals(0)).collect(Collectors.toList());
            // 获取固有商品
            List<UserMallRecord> intMallRecords = intrinsicProbList.stream().map(tmp -> {
                CfgMallEntity mall = MallTool.getMall(tmp.getId());
                UserMallRecord userMallRecord = UserMallRecord.instance(gu.getId(), mall.getId(), mall.getType(), 0);
                return userMallRecord;
            }).collect(Collectors.toList());

            // 过滤固有商品 得到 可随机商品列表
            List<CfgMall.MysteriousMallProb> probs = tobeRamPro.stream().filter(mallProb ->
                    !mallProb.getProbability().equals(0)).collect(Collectors.toList());
            // 获取商品随机数量 = 商品分组数量 - 固定商品数量（固有商品列表.size()）
            int ramNum = partNum - intMallRecords.size();
            // 按照概率 随机获取商品prob
            List<CfgMall.MysteriousMallProb> resProbList = getIndexsByProbs(probs, MallTool.getMallConfig().getRamSeed(), ramNum);

            // 添加固有商品
            results.addAll(intMallRecords);
            // 添加随机商品 并随机打折
            for (CfgMall.MysteriousMallProb prob : resProbList) {
                //获取cfgMallEntity
                CfgMallEntity cfgMall = MallTool.getMall(prob.getId());
                // 生成UserMallRecord
                UserMallRecord userMallRecord = UserMallRecord.instance(gu.getId(), cfgMall.getId(), cfgMall.getType(), 0);
                // 判断商品是否存在打折
                if (prob.getIsDiscount()) {
                    // 按照概率 获取打折类型
                    CfgMall.DiscountType discountType = getDiscountType(prob, MallTool.getMallConfig().getRamSeed());
                    if (discountType != null) {
                        // 设置为打折后折扣值
                        userMallRecord.setDiscount(discountType.getDiscount());
                        results.add(userMallRecord);
                        continue;
                    }
                }
                // 无折扣
                results.add(userMallRecord);
            }
        }
        // 排序并响应
        return results;
    }

    /**
     * 排序神秘商品记录
     *
     * @param toBeSortList 待排序列表
     * @return
     */
    public void sortUserMallRecords(List<UserMallRecord> toBeSortList) {
        toBeSortList.sort((o1, o2) -> {
            Integer serial1 = MallTool.getMall(o1.getBaseId()).getSerial();
            Integer serial2 = MallTool.getMall(o2.getBaseId()).getSerial();
            if (serial1 == serial2) {
                return 0;
            }
            return serial1 > serial2 ? 1 : -1;
        });
    }

    /**
     * 根据商品prob权重 计算概率区间
     *
     * @param probs 商品prob列表
     * @return
     */
    private Map<Integer, Map<String, Integer>> getMallWeightsProb(List<CfgMall.MysteriousMallProb> probs) {
        // 权重区间map，key为 prob：id， value为 权重值对应概率值区间[min,max]
        Map<Integer, Map<String, Integer>> weightsMap = new HashMap<>(16);
        // 概率区间 上下限
        AtomicInteger min = new AtomicInteger();
        AtomicInteger max = new AtomicInteger();
        probs.forEach(mallProb -> {
            // 根据权重 probability 设置最小、最大值 区间[min,max]
            min.set(max.get() + 1);
            max.set(min.get() + mallProb.getProbability() - 1);
            // 添加区间
            Map<String, Integer> intervalMap = new HashMap<>();
            intervalMap.put("min", min.get());
            intervalMap.put("max", max.get());
            weightsMap.put(mallProb.getId(), intervalMap);
        });
        return weightsMap;
    }

    /**
     * 根据概率生成索引
     *
     * @param probs 概率
     * @param seed  概率和
     * @return 权重区间map，key为 样品标识id：probId， value为 权重值对应概率值区间[min,max]
     */
    private List<CfgMall.MysteriousMallProb> getIndexsByProbs(List<CfgMall.MysteriousMallProb> probs, int seed, int num) {

        // 抽中（随机到）的商品列表
        List<CfgMall.MysteriousMallProb> resProbList = new ArrayList<>();
        // 抽中（随机到）的商品数
        AtomicInteger ramNum = new AtomicInteger();
        // 判断随机商品数ramNum 已达到所需的要求num
        while (ramNum.get() < num) {
            // 获取随机万分率值
            int randomBySeed = PowerRandom.getRandomBySeed(seed);
            // 获取权重区间
            Map<Integer, Map<String, Integer>> weightsProb = getMallWeightsProb(probs);
            weightsProb.forEach((integer, stringIntegerMap) -> {
                // 比较随机万分率值是否在 权重区间下--true-抽中（随机到），false-未抽中
                if (stringIntegerMap.get("min") <= randomBySeed && randomBySeed <= stringIntegerMap.get("max")) {
                    // 保存抽中商品prob
                    CfgMall.MysteriousMallProb mysteriousMallProb = probs.stream().filter(mallProb ->
                            mallProb.getId().equals(integer)).findFirst().orElse(null);
                    resProbList.add(mysteriousMallProb);
                    // 抽中数值 加 1
                    ramNum.getAndIncrement();
                }
            });

        }
        return resProbList;
    }
    /**
     * 根据折扣prob权重 计算概率区间
     *
     * @param probs 折扣类型列表
     * @return 权重区间map，key为 样品标识id：typeId， value为 权重值对应概率值区间[min,max]
     */
    private Map<Integer, Map<String, Integer>> getDiscountWeightsProb(List<CfgMall.DiscountType> probs) {
        // 权重区间map，key为 折扣类型id：typeId， value为 权重值对应概率值区间[min,max]
        Map<Integer, Map<String, Integer>> weightsMap = new HashMap<>(16);
        // 概率区间 上下限
        AtomicInteger min = new AtomicInteger();
        AtomicInteger max = new AtomicInteger();
        probs.forEach(mallProb -> {
            // 根据权重 probability 设置最小、最大值 区间[min,max]
            min.set(max.get() + 1);
            max.set(min.get() + mallProb.getProbability() - 1);
            // 添加区间
            Map<String, Integer> intervalMap = new HashMap<>();
            intervalMap.put("min", min.get());
            intervalMap.put("max", max.get());
            weightsMap.put(mallProb.getTypeId(), intervalMap);
        });
        return weightsMap;
    }
    /**
     * 获取折扣类型
     *
     * @param mallProb 概率商品信息
     * @param seed 随机种子
     * @return discountType
     */
    private CfgMall.DiscountType getDiscountType(CfgMall.MysteriousMallProb mallProb,int seed) {
        // 获取商城折扣类型
        List<CfgMall.DiscountType> discountTypes = MallTool.getMallConfig().getDiscounts();
        // 获取商城折扣组合详情
        List<CfgMall.DiscountGroup> typeArrDetails = MallTool.getMallConfig().getDiscountGroups();

        // 获取折扣类型组合详情
        CfgMall.DiscountGroup discountTypeArrDetail = typeArrDetails.stream().filter(typeArrDetail ->
                typeArrDetail.getDiscountGroupId().equals(mallProb.getDiscountGroupId())).findFirst().orElse(null);
        List<Integer> typeArr = discountTypeArrDetail.getTypes();

        // 获取商品折扣组 对应折扣类型
        List<CfgMall.DiscountType> ramDiscountList = new ArrayList<>();
        discountTypes.forEach(discountType -> {
            // 筛选符合折扣组的折扣类型
            List<Integer> collect = typeArr.stream().filter(typeId ->
                    typeId.equals(discountType.getTypeId())).collect(Collectors.toList());
            // 存在 则添加该折扣类型
            if (collect.size() > 0){
                ramDiscountList.add(discountType);
            }
        });
        // 生成折扣概率权重区间
        Map<Integer, Map<String, Integer>> discountWeightsProb = getDiscountWeightsProb(ramDiscountList);
        // 随机万分率值
        int randomBySeed = PowerRandom.getRandomBySeed(seed);
        // 抽中的折扣id
        AtomicInteger typeId = new AtomicInteger();
        discountWeightsProb.forEach((integer, stringIntegerMap) -> {
            // 比较随机万分率值是否在 权重区间下--true-抽中（随机到），false-未抽中
            if (stringIntegerMap.get("min") <= randomBySeed && randomBySeed <= stringIntegerMap.get("max")) {
                // 设置抽中的折扣id
                typeId.set(integer);
            }
        });
        // 无折扣 返回 null
        return ramDiscountList.stream().filter(type ->
                type.getTypeId().equals(typeId.get())).findFirst().orElse(null);
    }

    /**
     * 获取配置神秘概率配置文件
     *
     * @param gu
     * @return
     */
    private List<CfgMall.MysteriousMallProb> getMysteriousMallProbs(GameUser gu) {
        CfgMall config = MallTool.getMallConfig();
        return CloneUtil.cloneList(config.getFsdlMysteriousMallProbs());
    }

    /**
     * 根据集合中的概率获取
     *
     * @param mallList
     * @return
     */
    private CfgMallEntity getRandomByProbability(List<CfgMallEntity> mallList) {
        int totalProbability = mallList.stream().mapToInt(CfgMallEntity::getProbability).sum();
        int random = PowerRandom.getRandomBySeed(totalProbability);
        int sum = 0;
        for (CfgMallEntity entity : mallList) {
            sum += entity.getProbability();
            if (random <= sum) {
                return entity;
            }
        }
        return null;
    }

    /**
     * 神秘道具的概率处理
     *
     * @param mall
     * @return
     */
    private boolean isToRemake(CfgMallEntity mall) {
        switch (mall.getGoodsId()) {
            case 850:
                return PowerRandom.getRandomBySeed(MallTool.getMallConfig().getLsRate5()) > 1;
            case 840:
                return PowerRandom.getRandomBySeed(MallTool.getMallConfig().getLsRate4()) > 1;
            case 830:
                return PowerRandom.getRandomBySeed(MallTool.getMallConfig().getLsRate3()) > 1;
            case 10010:
                return PowerRandom.getRandomBySeed(MallTool.getMallConfig().getJxqRate()) > 1;
            default:
                return false;
        }
    }

    /**
     * 获取今日刷新纪录
     *
     * @param guId
     * @return
     */
    private UserMallRefreshRecord getTodayRefreshRecord(long guId) {
        return gameUserService.getMultiItems(guId, UserMallRefreshRecord.class).stream().filter(UserMallRefreshRecord::ifToday)
                .findFirst().orElse(null);
    }

    private RDMysteriousMallList setRdByRecords(Long guId, List<UserMallRecord> records, int refreshTimes) {
        RDMysteriousMallList rd = new RDMysteriousMallList();
        int sId = gameUserService.getActiveSid(guId);
        boolean isDiscount = activityService.isActive(sId, ActivityEnum.MALL_DISCOUNT);
        List<RDMallInfo> mallMysteriousTreasures = records.stream().map(record -> {
            CfgMallEntity mall = MallTool.getMall(record.getBaseId());
            int remainTimes = mall.getLimit() - record.getNum();
            RDMallInfo rdMallInfo = RDMallInfo.fromMall(mall, mall.getPrice(), remainTimes);
            rdMallInfo.setRecordId(record.getId());
            // 设置原价
            rdMallInfo.setOriginalPrice(mall.getPrice());
            // 设置售价（有折扣）
            rdMallInfo.setPrice(mall.getPrice() * record.getDiscount() / 100);
            // 设置折扣值
            rdMallInfo.setDiscount(record.getDiscount());
            return rdMallInfo;
        }).collect(Collectors.toList());

        CfgMall config = MallTool.getMallConfig();
        rd.setMallGoods(mallMysteriousTreasures);
        rd.setRefreshGold(config.getMyteriousRefreshGold());
        rd.setRefreshTime(DateUtil.getTimeToNextDay());
        rd.setRefreshTimes(refreshTimes);
        rd.setLimitRefreshTime(config.getMysteriousRefreshLimit());
        return rd;
    }
}