package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.wanxianzhen.RDWanXian;
import com.bbw.god.game.wanxianzhen.WanXianTool;
import lombok.Data;

/**
 * @author lwb
 * @date 2020/5/26 15:31
 */
@Data
@TableName("wanxian_fight_detail")
public class WanXianFightDetailEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long p1;
    private Long p2;
    private Integer winner;
    private Integer winType;
    private Integer wxType=0;
    private String vidKey;
    private Integer season;
    private Integer round;
    private Integer weekday= DateUtil.getToDayWeekDay();

    public static WanXianFightDetailEntity instance(Combat combat, RDWanXian.RDFightLog log){
        WanXianFightDetailEntity entity=new WanXianFightDetailEntity();
        entity.setP1(log.getP1().getUid());
        entity.setP2(log.getP2().getUid());
        entity.setWinner(log.getWinner());
        entity.setVidKey(log.getVidKey());
        entity.setWinType(combat.getCombatResultType().getVal());
        entity.setSeason(WanXianTool.getThisSeason());
        entity.setRound(combat.getRound());
        return entity;
    }
}
