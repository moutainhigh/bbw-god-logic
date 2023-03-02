package com.bbw.god.gameuser.yaozu;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 触发妖族的临时缓存
 *
 * @author fzj
 * @date 2021/9/9 17:02
 */
@Data
public class ArriveYaoZuCache extends RDCityInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 妖族id */
    private Integer yaoZuId;
    /** 属性 10-金 20木 30水 40火 50土*/
    private Integer type;
    /** 护符id */
    private List<Integer> runes = new ArrayList<>();
    /** 妖族卡组 */
    private List<CfgYaoZuEntity.CardParam> cardParams;
    /** 进度 0表示未攻打镜像 1表示已经打赢镜像 2表示已经打败本体 */
    private Integer progress = 0;
    /** 镜像卡组 */
    private List<Integer> mirroringCards = new ArrayList<>();
    /** 本体卡组 */
    private List<Integer> ontologyCards = new ArrayList<>();
    /** 初始化战斗信息 */
    private CombatPVEParam fightParam = null;

    public static ArriveYaoZuCache getInstance(UserYaoZuInfo yaoZu) {
        ArriveYaoZuCache rd = new ArriveYaoZuCache();
        CfgYaoZuEntity cfg = YaoZuTool.getYaoZu(yaoZu.getBaseId());
        rd.setYaoZuId(yaoZu.getBaseId());
        rd.setType(cfg.getType());
        rd.setRunes(cfg.getRunes());
        rd.setProgress(0);
        rd.setCardParams(cfg.getYaoZuCards());
        rd.setMirroringCards(yaoZu.getMirroringCards());
        rd.setOntologyCards(yaoZu.getOntologyCards());
        return rd;
    }
}
