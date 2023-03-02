package com.bbw.god.gameuser.yaozu.rd;

import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 返回客户端妖族信息
 *
 * @author fzj
 * @date 2021/9/9 11:56
 */
@Slf4j
@Data
public class RDYaoZuInfo extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -2194371713246181712L;
    /** 妖族id */
    private Integer yaoZuId = null;
    /** 位置 */
    private Integer position = null;
    /** 属性 10-金 20木 30水 40火 50土*/
    private Integer type = null;
    /** 护符id */
    private List<Integer> runes = null;
    /** 妖族卡组 */
    private List<RDFightsInfo.RDFightCard> yaoZuCards = null;
    /** 妖族总数量 */
    private Integer yaoZuAllNum = null;
    /** 妖族未打败数量 */
    private Integer yaoZuRemainingNum = null;
    /** 妖族buff */
    private List<Integer> buffs = null;



}
