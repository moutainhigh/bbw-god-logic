package com.bbw.god.city.miaoy.hexagram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/***
 * 玩家的特殊BUFF枚举
 * @author liuwenbin
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum HexagramBuffEnum implements Serializable {
    /**
     * 乾为天卦-接下来1次进入客栈，必定遇到一张5星卡牌
     */
    HEXAGRAM_1(1),
    /**
     * 水地比卦-接下来10回合可免费获得七香车效果
     */
    HEXAGRAM_3(3),
    /**
     * 水风井卦-获得神仙-仙长，且产出为四倍。
     */
    HEXAGRAM_10(10),
    /**
     * 风山渐卦-接下来10个路口可任意选择方向
     */
    HEXAGRAM_12(12),
    /**
     * 水泽节卦-接下来10次随机事件中，遇到负面事件不会受到影响
     */
    HEXAGRAM_15(15),
    /**
     * 地泽临卦-接下来10回合投骰子不消耗任何体力
     */
    HEXAGRAM_21(21),
    /**
     * 风地观卦-接下来1次城池交易必定15%折扣和溢价
     */
    HEXAGRAM_22(22),
    /**
     * 离为火卦-接下来1次进入城池领取收益翻倍%折扣和溢价
     */
    HEXAGRAM_25(25),
    /**
     * 泽雷随卦-下回合在原地停留一次
     */
    HEXAGRAM_41(41),
    /**
     * 地雷复卦-随机传送到野怪点，且必为精英野怪
     */
    HEXAGRAM_43(43),
    /**
     * 泽风大过卦-接下来3场战斗，招财技能无法生效。
     */
    HEXAGRAM_47(47),
    /**
     * 地火明夷卦-获得【随机恶神】(显示神仙名称），且不能使用请神符
     */
    HEXAGRAM_48(48),
    /**
     * 风天小畜卦-接下来5次遇到村庄不会触发村庄事件
     */
    HEXAGRAM_53(53),
    /**
     * 天雷无妄卦-随机传送到木区，且接下来2个回合原地不动
     */
    HEXAGRAM_54(54),
    /**
     * 天山遁卦-15回合内无法通过随机摇骰子离开所属区域
     */
    HEXAGRAM_56(56),
    /**
     * 风火家人卦-接下来1次进入客栈，所有卡牌为5星，且只能用2个捆仙绳购买
     */
    HEXAGRAM_57(57),
    /**
     * 水山蹇卦-随机传送到水区，且接下来2个回合原地不动
     */
    HEXAGRAM_59(59),
    /**
     * 山泽损卦-随机传送到土区，且接下来2个回合原地不动
     */
    HEXAGRAM_60(60),
    /**
     * 雷泽归妹卦-接下来2次进入城池无法领取收益
     */
    HEXAGRAM_61(61),
    /**
     * 风水涣卦-接下来5次遇到元宝时无法获得奖励
     */
    HEXAGRAM_63(63),
    /**
     * 风泽中孚卦-接下来10回合固定走1步
     */
    HEXAGRAM_64(64);
    private int id;

    public static boolean isStand(int id){
        List<HexagramBuffEnum> list= Arrays.asList(HEXAGRAM_41,HEXAGRAM_54,HEXAGRAM_59,HEXAGRAM_60);
        return list.stream().filter(p->p.getId()==id).findFirst().isPresent();
    }
}
