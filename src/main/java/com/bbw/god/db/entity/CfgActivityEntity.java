package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-11-27 21:57:48
 */
@Data
@TableName("cfg_activity")
public class CfgActivityEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    @TableId
    private Integer id; //
    private Integer parentType;// 参见ActivityParentEnum
    private Integer scope;// 活动范围 10游戏活动实例 20区服活动实例
    private Integer type; // 活动类型
    private Integer serial; // 排序
    private Integer series;// 系列
    private String name; //
    private String detail; //
    private Integer needValue;// 领取奖励需要达到的数值条件
    private String awards;// 如果某活动时玩家指定奖励的，值为"-"
    private Boolean status; //

    @Override
    public int getSortId() {
        return this.getId();
    }

    /**
     * 是否月卡
     *
     * @return
     */
    public boolean isYueKa() {
        return ActivityEnum.RECHARGE_CARD.getValue() == type && this.series != null && this.series == 30;
    }

    /**
     * 会否季卡
     *
     * @return
     */
    public boolean isJiKa() {
        return ActivityEnum.RECHARGE_CARD.getValue() == type && this.series != null && this.series == 90;
    }

    /**
     * 是否是首位攻下主城的活动
     *
     * @return
     */
    public boolean isFirstCC5() {
        return this.type == ActivityEnum.GongCLD.getValue() && this.series == null;
    }

    /**
     * 是否是3倍返利充值要求活动
     *
     * @return
     */
    public boolean isMultipleRebateRechargeNeed() {
        return this.type == ActivityEnum.MULTIPLE_REBATE.getValue() && this.series == null;
    }

    /**
     * 是否是多日累充
     *
     * @return
     */
    public boolean isMultiDayRecharge() {
        return Arrays.asList(ActivityEnum.MULTI_DAY_ACC_R.getValue(), ActivityEnum.MULTI_DAY_ACC_R2.getValue(),
                ActivityEnum.MULTI_DAY_ACC_R3.getValue()).contains(this.type);
    }
}
