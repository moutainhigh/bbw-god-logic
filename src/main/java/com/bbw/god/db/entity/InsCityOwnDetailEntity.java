package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import lombok.Data;

import java.io.Serializable;

/**
 * 玩家城池占有明细
 *
 * @author suhq
 * @date 2020-04-15 10:29
 **/
@Data
@TableName("ins_city_own_detail")
public class InsCityOwnDetailEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.INPUT)
    private Long id;
    private Integer serverGroup;
    private Integer sid;
    private Long uid; //玩家ID
    private Integer guLv;// 玩家等级
    private Integer pay;// 累计充值
    private Integer cityId;// 城池ID
    private Integer cityCountry;//城池所属区域
    private String cityName;//城池名称
    private Integer cityLv;//城池级别
    private Integer cityLvNum;//城池等级序号
    private Integer cityNum;//城池序号
    private Integer roleLifeMinutes;//自角色创建到现在的时间(分钟)
    private String roleLife;//自角色创建到现在的时间
    private Long ownTime;//占有时间

    public InsCityOwnDetailEntity() {
        this.id = ID.getNextDetailId();
        this.ownTime = DateUtil.toDateTimeLong();
    }

}
