package com.bbw.god.activity.game;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 游戏活动实例，如签到等，提前生成
 *
 * @author suhq
 * @date 2018年10月12日 下午4:06:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GameActivity extends GameData implements IActivity {
    private static final String UNLIMITED_DATE = "2099-12-31 23:59:59";
    private Integer serverGroup = 0;
    /** 活动父类型 */
    private Integer parentType;
    /** 活动类型 */
    private Integer type;
    private Date begin;
    private Date end;
    /** 节日活动主题标识 */
    private String sign;

    public static GameActivity fromActivity(CfgActivityEntity activity) {
        Date beginDate = DateUtil.now();
        Date endDate = DateUtil.fromDateTimeString(UNLIMITED_DATE);
        return fromActivity(activity, beginDate, endDate);
    }

    public static GameActivity fromActivity(CfgActivityEntity activity, Date beginDate, Date endDate) {
        GameActivity ga = new GameActivity();
        ga.setId(ID.INSTANCE.nextId());
        ga.setParentType(activity.getParentType());
        ga.setType(activity.getType());
        ga.setBegin(beginDate);
        ga.setEnd(endDate);
//        System.out.println(ga.toString());
        return ga;
    }

    @Override
    public Boolean ifTimeValid() {
        return DateUtil.isBetweenIn(DateUtil.now(), this.begin, this.end);
    }

    public Boolean ifBefore(Date beginBefore, Date endBefore) {
        return this.begin.before(beginBefore) && this.end.before(endBefore);
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.ACTIVITY;
    }

    @Override
    public Long gainId() {
        return this.id;
    }

    @Override
    public Integer gainSId() {
        return 0;
    }

    @Override
    public Integer gainParentType() {
        return this.parentType;
    }

    @Override
    public Integer gainType() {
        return this.type;
    }

    @Override
    public Date gainBegin() {
        return this.begin;
    }

    @Override
    public Date gainEnd() {
        return this.end;
    }

    @Override
    public String gainSign() {
        return this.sign;
    }

    public String toDesString() {
        return this.id + ",区服组：" + serverGroup + "," + ActivityEnum.fromValue(this.type).getName() + this.type + "," + DateUtil.toDateTimeString(this.begin) + "," + DateUtil.toDateTimeString(this.end);
    }
}
