package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.god.game.wanxianzhen.RDWanXian;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import lombok.Data;

/**
 * @author lwb
 * @date 2020/5/26 15:31
 */
@Data
@TableName("wanxian_match")
public class WanXianMatchEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long p1;
    private Long p2;
    private Integer wxType;
    private String vidKey;
    private Integer season;
    private Integer weekday;
    private Integer gid;

    public static WanXianMatchEntity instance(RDWanXian.RDFightLog log,int weekday,int gid,int wxtype){
        WanXianMatchEntity entity=new WanXianMatchEntity();
        if (log.isChangePos()){
            entity.setP1(log.getP2().getUid());
            entity.setP2(log.getP1().getUid());
        }else {
            entity.setP1(log.getP1().getUid());
            entity.setP2(log.getP2().getUid());
        }
        entity.setSeason(WanXianTool.getThisSeason());
        entity.setWeekday(weekday);
        entity.setGid(gid);
        entity.setWxType(wxtype);
        return entity;
    }
}
