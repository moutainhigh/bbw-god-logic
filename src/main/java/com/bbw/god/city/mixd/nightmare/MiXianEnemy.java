package com.bbw.god.city.mixd.nightmare;

import com.bbw.god.game.combat.data.param.CCardParam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-05-27
 */
@Data
public class MiXianEnemy implements Serializable {
    private Long enemyId;
    private Long uid=-1L;
    private Integer saveTimes=0;
    private Integer head=101;
    private Integer headIcon;
    private String nickname;
    private Integer level;
    private Integer type;
    private Integer pos;
    private Integer blood=-1;
    private Integer buff=-1;
    private List<CCardParam> cardParams;

    public static MiXianEnemy getInstance(MiXianLevelData.PosData posData,int mxdLevel){
        MiXianEnemy enemy=new MiXianEnemy();
        enemy.setEnemyId(NightmareMiXianTool.buildMxdAiId(mxdLevel, posData.getPos(), posData.getTye()));
        enemy.setType(posData.getTye());
        enemy.setPos(posData.getPos());
        return enemy;
    }
    public static MiXianEnemy getInstance(long enemyId,MiXianLevelData.PosData posData){
        MiXianEnemy enemy=new MiXianEnemy();
        enemy.setEnemyId(enemyId);
        enemy.setType(posData.getTye());
        enemy.setPos(posData.getPos());
        return enemy;
    }

}
