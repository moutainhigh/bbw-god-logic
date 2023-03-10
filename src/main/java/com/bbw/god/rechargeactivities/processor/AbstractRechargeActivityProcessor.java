package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.detail.async.MallDetailAsyncHandler;
import com.bbw.god.detail.async.MallDetailEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.CfgDailyShake;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatistic;
import com.bbw.god.gameuser.statistic.behavior.recharge.RechargeStatisticService;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.pay.ProductService;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import com.bbw.god.rechargeactivities.processor.dailyshake.DailyShakeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2020/7/1 16:30
 */
@Slf4j
public abstract class AbstractRechargeActivityProcessor {
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected MallService mallService;
    @Autowired
    protected ProductService productService;
    @Autowired
    protected AwardService awardService;
    @Autowired
    protected RechargeStatisticService rechargeStatisticService;
    @Autowired
    private MailService mailService;
    @Autowired
    private DailyShakeService dailyShakeService;
    @Autowired
    private MallDetailAsyncHandler mallDetailAsyncHandler;
    //??????????????????ID=???????????????ID
    private static final int GROUP_GIFT_PACK_ID = 180004;

    /**
     * ?????????????????????
     *
     * @return
     */
    public abstract RechargeActivityEnum getParent();

    /**
     * ???????????????????????????
     * ????????????????????????????????????
     * @param parent
     * @return
     */
    public boolean isMatchByParent(RechargeActivityEnum parent){
        return getParent().equals(parent);
    }

    /**
     *
     * ??????????????????
     * @param itemEnum
     * @return
     */
    public boolean isMatch(RechargeActivityItemEnum itemEnum){
        return getCurrentEnum().equals(itemEnum);
    }
    /**
     * ??????????????????????????????
     * @return
     */
    public abstract RechargeActivityItemEnum getCurrentEnum();

    /**
     * ????????????
     * @param uid
     * @return
     */
    public boolean isShow(long uid) {
        Integer level = gameUserService.getGameUser(uid).getLevel();
        if (level >= getShowNeedLevel()) {
            return true;
        }
        RechargeStatistic statistic = rechargeStatisticService.fromRedis(uid, DateUtil.getTodayInt());
        Integer total = statistic.getTotal();
        if (total >= getShowNeedRecharge()) {
            return true;
        }
        return false;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    protected int getShowNeedLevel() {
        return 0;
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    protected int getShowNeedRecharge() {
        return 0;
    }

    /**
     * ??????????????????
     * @param uid
     * @return
     */
    public abstract RDRechargeActivity listAwards(long uid);

    /**
     * ??????????????????????????????
     * @param uid
     * @return
     */
    public int getCanGainAwardNum(long uid){
        return 0;
    }

    /**
     *
     * ????????????
     * @param uid
     * @param realId
     * @return
     */
    public RDRechargeActivity gainAwards(long uid,int realId){
        throw new ExceptionForClientTip("rechargeActivity.cant.award");
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param uid
     * @param realId
     * @return
     */
    public RDRechargeActivity buyAwards(long uid,int realId){
        throw new ExceptionForClientTip("rechargeActivity.cant.award");
    }

    /**
     * ????????????
     *
     * @param fMalls
     * @param uid
     * @param rd
     */
    protected void buyAwards(List<CfgMallEntity> fMalls, int mallId, long uid, RDRechargeActivity rd, WayEnum wayEnum, MallEnum mallEnum) {
        Optional<CfgMallEntity> optional = fMalls.stream().filter(p -> p.getId() == mallId).findFirst();
        if (!optional.isPresent()) {
            //?????????
            throw new ExceptionForClientTip("rechargeActivity.cant.use.way");
        }
        CfgMallEntity cfgMallEntity = optional.get();
        //????????????
        int buyWay = cfgMallEntity.getUnit();
        if (buyWay != ConsumeType.GOLD.getValue() && buyWay != ConsumeType.DIAMOND.getValue()) {
            //????????????????????????????????????,?????????
            throw new ExceptionForClientTip("rechargeActivity.cant.use.way");
        }
        UserMallRecord mallRecord = mallService.getUserMallRecord(uid, cfgMallEntity.getId());
        if (mallRecord == null) {
            mallRecord = UserMallRecord.instance(uid, cfgMallEntity.getId(), mallEnum.getValue(), 0);
            mallService.addRecord(mallRecord);
        }
        //????????????????????????????????????
        CfgDailyShake.Welfare welfare = dailyShakeService.getWelfare(uid);
        boolean isWelfare = null != welfare && welfare.getMallIds().contains(mallId);
        //??????????????????
        buyLimiyCheck(mallRecord, cfgMallEntity, isWelfare);
        //????????????
        List<Award> awards = getAwards(uid, cfgMallEntity, mallRecord, isWelfare, welfare);

        //????????????
        int price = getPrice(uid, cfgMallEntity, isWelfare, welfare);
        //????????????
        if (buyWay == ConsumeType.GOLD.getValue()) {
            //????????????
            ResChecker.checkGold(gameUserService.getGameUser(uid), price);
            ResEventPublisher.pubGoldDeductEvent(uid, cfgMallEntity.getPrice(), wayEnum, rd);
        }
        //????????????
        if (buyWay == ConsumeType.DIAMOND.getValue()) {
            ResChecker.checkDiamond(gameUserService.getGameUser(uid), price);
            //????????????
            ResEventPublisher.pubDiamondDeductEvent(uid, price, wayEnum, rd);
            mallDetailAsyncHandler.log(new MallDetailEventParam(uid, cfgMallEntity, 1, price, gameUserService.getGameUser(uid).getGold()));
        }
        awardService.fetchAward(uid, awards, wayEnum, "", rd);
        //??????????????????
        addBuyNum(mallRecord, awards, isWelfare);
        gameUserService.updateItem(mallRecord);
    }

    /**
     * ??????????????????
     *
     * @param userMallRecord
     * @param cfgMallEntity
     * @param isWelfare
     */
    protected void buyLimiyCheck(UserMallRecord userMallRecord, CfgMallEntity cfgMallEntity, boolean isWelfare) {
        if (isWelfare) {
            return;
        }
        if (cfgMallEntity.getLimit() == 0) {
            return;
        }
        if (userMallRecord.getNum() >= cfgMallEntity.getLimit()) {
            //??????
            throw new ExceptionForClientTip("rechargeActivity.awarded");
        }

    }

    /**
     * ????????????
     *
     * @param uid
     * @param cfgMallEntity
     * @param isWelfare
     * @param welfare
     * @return
     */
    protected int getPrice(long uid, CfgMallEntity cfgMallEntity, boolean isWelfare, CfgDailyShake.Welfare welfare) {
        return cfgMallEntity.getPrice();
    }

    /**
     * ???????????????
     *
     * @param uid
     * @param cfgMallEntity
     * @param userMallRecord
     * @param isWelfare
     * @param welfare
     * @return
     */
    protected List<Award> getAwards(long uid, CfgMallEntity cfgMallEntity, UserMallRecord userMallRecord, boolean isWelfare, CfgDailyShake.Welfare welfare) {
        return productService.getProductAward(getProductGoodsId(cfgMallEntity.getGoodsId())).getAwardList();
    }

    /**
     * ??????????????????
     *
     * @param userMallRecord
     * @param awards
     * @param isWelfare
     */
    protected void addBuyNum(UserMallRecord userMallRecord, List<Award> awards, boolean isWelfare) {
        //???????????????????????????
        if (isWelfare) {
            return;
        }
        //????????????
        userMallRecord.add();
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param guId
     * @param fMalls
     * @param mallEnum
     * @param isExtraDiscount
     * @return
     */
    protected List<RDRechargeActivity.GiftPackInfo> toRdGoodsInfoList(long guId, List<CfgMallEntity> fMalls, MallEnum mallEnum, boolean isExtraDiscount) {
        List<RDRechargeActivity.GiftPackInfo> list = new ArrayList<>(16);
        List<UserMallRecord> userMallRecords = mallService.getUserMallRecord(guId, mallEnum);
        checkTimeoutRecharge(guId, userMallRecords);
        if (ListUtil.isNotEmpty(userMallRecords)) {
            userMallRecords = userMallRecords.stream().filter(p -> p.ifValid()).collect(Collectors.toList());
        }
        for (CfgMallEntity mall : fMalls) {
            RDRechargeActivity.GiftPackInfo goodsInfo = RDRechargeActivity.GiftPackInfo.instance(mall, mall.getPrice(isExtraDiscount));
            // ?????????????????????
            if (mall.getLimit() > 0 && ListUtil.isNotEmpty(userMallRecords)) {
                // ??????????????????
                Optional<UserMallRecord> optional = userMallRecords.stream().filter(p -> p.getBaseId().equals(mall.getId())).findFirst();
                if (optional.isPresent()) {
                    UserMallRecord um = optional.get();
                    goodsInfo.setRemainTimes(mall.getLimit() - um.getNum());
                    if (um.getStatus() != null && um.getStatus() == RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()) {
                        goodsInfo.setStatus(um.getStatus());
                    }
                }
            }
            goodsInfo.setAwards(ListUtil.copyList(productService.getProductAward(goodsInfo.getRechargeId()).getAwardList(),Award.class));
            if (goodsInfo.getRemainTimes()<=0){
                goodsInfo.setStatus(-1);
            }
            list.add(goodsInfo);
        }
        list=list.stream().sorted(Comparator.comparing(RDRechargeActivity.GiftPackInfo::getStatus).reversed()).collect(Collectors.toList());
        return list;
    }

    /**
     * ?????????????????????
     * @param uid
     * @param mallEntity
     * @param wayEnum
     * @return
     */
    protected RDRechargeActivity gainFreeMallAwards(long uid, CfgMallEntity mallEntity,WayEnum wayEnum) {
        RDRechargeActivity rd=new RDRechargeActivity();
        if (!canGainFreeAwards(uid,mallEntity)){
            //??????????????????
            throw new ExceptionForClientTip("rechargeActivity.awarded");
        }
        UserMallRecord record = mallService.getUserMallRecord(uid,mallEntity.getId());
        if (record.getStatus()!=null && record.getStatus()!=RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()){
            throw new ExceptionForClientTip("rechargeActivity.awarded");
        }
        List<Award> awards=new ArrayList<>();
        if (record==null){
            record=UserMallRecord.instance(uid,mallEntity.getId(),mallEntity.getType(),0);
        }else if (ListUtil.isNotEmpty(record.getPickedAwards())){
            awards.addAll(record.getPickedAwards());
        }
        awards.addAll(productService.getProductAward(getProductGoodsId(mallEntity.getGoodsId())).getAwardList());
        awardService.fetchAward(uid,awards, wayEnum,"??????"+wayEnum.getName()+"???",rd);
        record.setStatus(RechargeStatusEnum.DONE.getStatus());
        record.addNum(1);
        mallService.addRecord(record);
        return rd;
    }

    /**
     *
     * ??????????????????????????????
     * @param uid
     * @param mallEntity
     * @return
     */
    protected boolean canGainFreeAwards(long uid, CfgMallEntity mallEntity){
        UserMallRecord umr=mallService.getUserMallRecord(uid,mallEntity.getId());
        return umr==null && umr.getNum()<mallEntity.getLimit();
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    public RDRechargeActivity pickAwards(long uid, Integer mallId, String awardIds) {
        return new RDRechargeActivity();
    }

    /**
     * ????????????????????????????????????ID?????????????????????goodsId+99000000
     * @param mallGoodsId
     * @return
     */
    protected int getProductGoodsId(int mallGoodsId){
        return mallGoodsId+99000000;
    }

    public boolean updateRechargeStatus(long uid,int mallId){
        return false;
    }


    public void checkTimeoutRecharge(long uid,List<UserMallRecord> userMallRecords){
        try{
            List<UserMallRecord> records = userMallRecords.stream().filter(p -> !p.ifValid() && p.getStatus() != null && p.getStatus() == RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()).collect(Collectors.toList());
            String title= LM.I.getMsgByUid(uid,"mail.timeout.recharge.award.title");
            String content=LM.I.getMsgByUid(uid,"mail.timeout.recharge.award.content");
            if (ListUtil.isNotEmpty(records)){
                //????????????????????????
                for (UserMallRecord record : records) {
                    if (record.getBaseId()==GROUP_GIFT_PACK_ID){
                        continue;
                    }
                    CfgMallEntity mallEntity= MallTool.getMall(record.getBaseId());
                    List<Award> awards=new ArrayList<>();
                    awards.addAll(productService.getProductAward(getProductGoodsId(mallEntity.getGoodsId())).getAwardList());
                    if (ListUtil.isNotEmpty(record.getPickedAwards())){
                        awards.addAll(record.getPickedAwards());
                    }
                    mailService.sendAwardMail(title,content,uid,awards);
                    record.addNum(1);
                    record.setStatus(RechargeStatusEnum.DONE.getStatus());
                    gameUserService.addItem(uid, record);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    /**
     * ????????????
     * @param uid
     * @return
     */
    public RDRechargeActivity gainAllAvailableAwards(long uid){
        return new RDRechargeActivity();
    }

    /**
     * ??????
     * @param uid
     * @param id
     * @return
     */
    public RDRechargeActivity refreshItem(long uid,int id){
        return new RDRechargeActivity();
    }

}
