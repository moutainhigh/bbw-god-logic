package com.bbw.god.rd;

import com.bbw.god.city.RDCityInfo;
import com.bbw.god.city.yed.RDArriveYeD;
import com.bbw.god.gameuser.businessgang.digfortreasure.RDDigTreasureInfo;
import com.bbw.god.gameuser.businessgang.luckybeast.RDLuckyBeastInfo;
import com.bbw.god.gameuser.yaozu.rd.RDArriveYaoZu;
import com.bbw.god.server.god.RDAttachGod;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 在地图上行进用，包括摇骰子、选择方向、使用七香车、山河社稷图、风火轮等
 *
 * @author suhq
 * @date 2019年3月12日 上午10:09:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDAdvance extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Integer> randoms = null;// 摇骰子的结果
    private Integer mbxRemainForcross = null;
    private Integer direction = null;// 1，2，3，4 表示上左右下
    private List<Integer> poss = null;
    private List<Integer> dirs = null;

    private Long boundCopper = null;// 经过界碑获得的铜钱

    private Integer godRemainStep = null;// 神仙剩余步数
    private RDAttachGod attachGod = null;// 附体神仙
    private Integer godExt = null;//神仙额外参数
    private RDCityInfo cityInfo = null;// 城池到达信息
    private RDArriveYeD arriveYeD = null;// 城池到达信息
    private Integer stand = 0;//原地不动
    /** 妖族到达信息 */
    private RDArriveYaoZu arriveYaoZu;
    /**
     * 不给糖就捣乱活动 触发事件类型 0表示开宝箱 1为南瓜幽灵战斗
     * 双旦节活动 触发事件类型 0直接获得奖励 1为特殊村庄任务
     * 虎震威年活动 触发事件类型 0直接获得奖励
     */
    private Integer activityEvenType;
    private RDDigTreasureInfo arriveDigTreasure;
    private RDLuckyBeastInfo arriveLuckyBeast;
}
