package com.bbw.god.mall;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallPeroidEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.rechargeactivities.processor.RechargeStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商城记录
 *
 * @author suhq 2018年9月30日 上午10:55:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserMallRecord extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 折扣默认值100 */
    private static final int DISCOUNT_DEFAULT = 100;
    /** 物品类型 */
    private Integer type;
    /** 购买数量 */
    private Integer num;
    /** discount(折扣值),例:80,即为打8折 */
    private Integer discount;
    /** 记录生成时间 */
    private Date dateTime;
    /** 已经选择的奖励 */
    private List<Award> pickedAwards = null;
    /** 状态 */
    private Integer status = null;

    public static UserMallRecord instance(Long guId, int mallId, int goodType, int num) {
        UserMallRecord umr = new UserMallRecord();
        umr.setId(ID.INSTANCE.nextId());
        umr.setGameUserId(guId);
        umr.setBaseId(mallId);
        umr.setType(goodType);
        umr.setDiscount(DISCOUNT_DEFAULT);
        umr.setNum(num);
        umr.setDateTime(DateUtil.now());
        return umr;
    }

    public static UserMallRecord instanceZLRecord(Long guId, int mallId) {
        UserMallRecord umr = new UserMallRecord();
        umr.setId(ID.INSTANCE.nextId());
        umr.setGameUserId(guId);
        umr.setBaseId(mallId);
        umr.setType(MallEnum.ZLLB.getValue());
        umr.setDiscount(DISCOUNT_DEFAULT);
        umr.setNum(0);
        umr.setDateTime(DateUtil.addSeconds(DateUtil.now(), DateUtil.SECOND_ONE_DAY));
        return umr;
    }

    public static UserMallRecord instanceTimeLimitRecord(Long guId, int mallType, int mallId, int seconds) {
        UserMallRecord umr = new UserMallRecord();
        umr.setId(ID.INSTANCE.nextId());
        umr.setGameUserId(guId);
        umr.setBaseId(mallId);
        umr.setType(mallType);
        umr.setDiscount(DISCOUNT_DEFAULT);
        umr.setNum(0);
        umr.setStatus(RechargeStatusEnum.CAN_BUY.getStatus());
        umr.setDateTime(DateUtil.addSeconds(DateUtil.now(), seconds));
        return umr;
    }

    public void addNum(int num) {
        if (status != null && status == RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()) {
            return;
        }
        this.num += num;
    }

    public void add() {
        this.num += 1;
    }

    /**
     * 该记录是否仍然有效
     *
     * @return
     */
    public boolean ifValid() {
        CfgMallEntity mall = MallTool.getMall(getBaseId());
        if (mall == null) {
            return false;
        }
        if (mall.getId() == 180004) {
            String  february17thString = "2023-02-17 00:00:00";
            Date february17thDate = DateUtil.fromDateTimeString(february17thString);
            //礼包的结束时间
            Date endDateTime = DateUtil.addDays(this.getDateTime(), 7);
            //当前礼包的是七天过期
            boolean timeOutWeek = mall.getPeroid() == MallPeroidEnum.TIME_OUT_WEEK.getValue();
            //12-06号在这个特惠礼包领取的范围内
            boolean exisfebruary17 = this.getDateTime().before(february17thDate) && endDateTime.after(february17thDate);
            //当前时间小于礼包领取的结束时间
            boolean lessEndDate = new Date().before(endDateTime);

            if (timeOutWeek && exisfebruary17 && lessEndDate){
                return true;
            }
        }
        if (mall.getPeroid() == MallPeroidEnum.PER_DAY.getValue() && !DateUtil.isToday(this.dateTime)) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.PER_WEEK.getValue() && !DateUtil.isThisWeek(this.dateTime)) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.PER_MONTH.getValue() && !DateUtil.isThisMonth(this.dateTime)) {
            return false;
        }
        Date now = DateUtil.now();
        //限时过期
        if (mall.getType() == MallEnum.ROLE_TIME_LIMIT_BAG.getValue() && now.after(this.dateTime)) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_WEEK.getValue() && DateUtil.getDaysBetween(this.getDateTime(), DateUtil.now()) > 6) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_EIGHT.getValue() && DateUtil.getDaysBetween(this.getDateTime(), DateUtil.now()) > 7) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_NINE.getValue() && DateUtil.getDaysBetween(this.getDateTime(), DateUtil.now()) > 8) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_TEN.getValue() && DateUtil.getDaysBetween(this.getDateTime(), DateUtil.now()) > 9) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_ELEVEN.getValue() && DateUtil.getDaysBetween(this.getDateTime(), DateUtil.now()) > 10) {
            return false;
        }
        return true;
    }

    /**
     * 是否在指定的时间内为有效记录
     *
     * @param date
     * @return
     */
    public boolean ifValid(Date date) {
        int days = DateUtil.getDaysBetween(this.getDateTime(), date);
        if (days < 0) {
            //比指定的时间晚生成
            return false;
        }
        CfgMallEntity mall = MallTool.getMall(getBaseId());
        if (mall == null) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.PER_DAY.getValue() && 0 != days) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.PER_WEEK.getValue() && !DateUtil.isEqualWeek(this.dateTime, date)) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.PER_MONTH.getValue() && DateUtil.getMonthsBetween(this.dateTime, date) != 0) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_WEEK.getValue() && DateUtil.getDaysBetween(this.getDateTime(), date) > 6) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_EIGHT.getValue() && DateUtil.getDaysBetween(this.getDateTime(), date) > 7) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_NINE.getValue() && DateUtil.getDaysBetween(this.getDateTime(), date) > 8) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_TEN.getValue() && DateUtil.getDaysBetween(this.getDateTime(), date) > 9) {
            return false;
        }
        if (mall.getPeroid() == MallPeroidEnum.TIME_OUT_ELEVEN.getValue() && DateUtil.getDaysBetween(this.getDateTime(), date) > 10) {
            return false;
        }
        return true;
    }

    /**
     * 是否超过购买次数
     *
     * @return
     */
    public boolean ifOutOfLimit() {
        CfgMallEntity mall = MallTool.getMall(getBaseId());
        return this.num >= mall.getLimit();
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.MALL_RECORD;
    }
}