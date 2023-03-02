package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * @author lwb
 * @date 2020/5/26 15:31
 */
@Data
@TableName("wanxian_champion_prediction_fight_log")
public class WanXianCPFightLogEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long uid;
    private Integer lv;
    private String vidkey;
    private Date clickDate;

    public static WanXianCPFightLogEntity instance(long uid, int lv, String vidkey){
        WanXianCPFightLogEntity entity=new WanXianCPFightLogEntity();
        entity.setVidkey(vidkey);
        entity.setLv(lv);
        entity.setUid(uid);
        entity.setClickDate(new Date());
        return entity;
    }
}
