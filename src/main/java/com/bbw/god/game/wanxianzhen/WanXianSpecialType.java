package com.bbw.god.game.wanxianzhen;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lwb
 * @date 2020/6/2 9:01
 */
@Getter
@AllArgsConstructor
public enum WanXianSpecialType {
    JIN(9010,"金系赛（仅能使用金系神将参赛）"),
    MU_YE(9020,"牧野赛（参赛队伍必须拥有20张神将）"),
    MU(9030,"木系赛（仅能使用木系神将参赛）"),
    XIAO_BING(9040,"小兵赛（仅能使用1星神将和2星神将参赛，玩家等级调为40级）"),
    SHUI(9050,"水系赛（仅能使用水系神将参赛）"),
    ZHONG_JIAN(9060,"中坚赛（仅能使用3星神将和4星神将参赛，玩家等级调为80级）"),
    HUO(9070,"火系赛（仅能使用火系神将参赛）"),
    XIN_SHOU(9080,"（战斗时神将调整为0阶10级，玩家等级调为40级）"),
    PING_MIN(9090,"平民赛（无法使用五张，将参与比赛）"),
    TU(9100,"土系赛（仅能使用土系神将参赛）"),
    BEI_SHUI(9110,"背水赛，召唤师血量调整为18w，拥有复活、招魂、封神技能的卡牌不能上场。"),
    MAGIC(9120,"法师赛 召唤师体力上限为18w，所有对卡牌生效的法术伤害翻倍。"),
    JIN_LUAN_DOU(9130,"金乱斗，仅能使用金系神将参赛，至少上阵15名神将，不能上阵姬昌。"),
    MU_LUAN_DOU(9140,"木乱斗 仅能使用木系神将参赛，至少上阵15名神将，不能上姜桓楚。"),
    SHUI_LUAN_DOU(9150,"水乱斗 仅能使用水系神将参赛，至少上阵15名神将，不能上崇侯虎。"),
    HUO_LUAN_DOU(9160,"火乱斗 仅能使用火系神将参赛，至少上阵15名神将，不能上鄂崇禹。"),
    TU_LUAN_DOU(9170,"土乱斗 仅能使用土系神将参赛，至少上阵15名神将，不能上纣王。"),
    ALL_LUAN_DOU(9180,"大乱斗 需要上阵五种属性的神将，每种属性各4张。"),
    GONG_CHENG(9190,"攻城赛 召唤师血量为80万，且神疗、治愈技能无效，战斗最多进行10回合。"),
    SHEN_XIAN(9200,"神仙战 随机抽取20张卡牌，如该玩家未获得，降为0阶10级。"),
    ;
    private int val;
    private String memo;

    public static WanXianSpecialType fromVal(int val){
        for (WanXianSpecialType type:values()){
            if (type.getVal()==val){
                return type;
            }
        }
        return null;
    }
}
