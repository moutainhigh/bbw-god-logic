package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽卡明细
 *
 * @author suhq
 * @date 2020-05-30 14:02
 **/
@Data
@TableName("ins_card_draw_detail")
public class InsCardDrawDetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private Long id;
    private Integer serverGroup;
    private Integer sid;
    private Long uid; //玩家ID
    private Integer guLv;// 玩家等级
    private Integer way;// 卡池类型
    private String wayName;// 卡池类型名称
    private Integer drawNum;//抽卡次数
    private String result;//结果
    private String newCards;//新卡
    private Integer newCardsNum;//新卡数
    private Integer maxStar;//抽到的最大星级
    private Integer ownGold;//购买时拥有的元宝数
    private Date drawTime;//抽卡时间
    private Integer rechargeAmount;//购买时累计充值金额
    private Date lastRechargeTime;//最近充值时间
    private Integer roleLifeMinutes;//自角色创建到现在的时间(分钟)
    private String roleLife;//自角色创建到现在的时间

    public InsCardDrawDetailEntity() {
        this.id = ID.getNextDetailId();
        this.drawTime = DateUtil.now();
    }

}
