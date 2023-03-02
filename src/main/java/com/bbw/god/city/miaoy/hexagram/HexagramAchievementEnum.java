package com.bbw.god.city.miaoy.hexagram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 获取卦象成就
 * @author liuwenbin
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum HexagramAchievementEnum {
    QWT(1,16010,"乾为天卦",1),
    KWD(2,16020,"坤为地卦",1),
    SDB(3,16030,"水地比卦",1),
    HTDY(4,16040,"火天大有卦",1),
    HLSH(5,16050,"火雷噬嗑卦",1),
    SLY(6,16060,"山雷颐卦",1),
    FLY(7,16070,"风雷益卦",1),
    ZTG(8,16080,"泽天夬卦",1),
    DFS(9,16090,"地风升卦",1),
    SFJ(10,16100,"水风井卦",1),
    ZHG(11,16110,"泽火革卦",1),
    FSJ(12,16120,"风山渐卦",1),
    LHF(13,16130,"雷火丰卦",1),
    DWZ(14,16140,"兑为泽卦",1),
    SZJ(15,16150,"水泽节卦",1),
    TFG(16,16160,"天风姤卦",1),
    STX(17,16170,"水天需卦",2),
    DSS(18,16180,"地水师卦",2),
    TZL(19,16190,"天泽履卦",2),
    THTR(20,16200,"天火同人卦",2),
    DZL(21,16210,"地泽临卦",2),
    FDG(22,16220,"风地观卦",2),
    SHB(23,16230,"山火贲卦",2),
    STDC(24,16240,"山天大畜卦",2),
    LWH(25,16250,"离为火卦",2),
    ZSX(26,16260,"泽山咸卦",2),
    LFH(27,16270,"雷风恒卦",2),
    LTDZ(28,16280,"雷天大壮卦",2),
    HDJ(29,16290,"火地晋卦",2),
    LSJ(30,16300,"雷水解卦",2),
    ZDC(31,16310,"泽地萃卦",2),
    ZSK(32,16320,"泽水困卦",2),
    ZWL(33,16330,"震为雷卦",2),
    XWF(34,16340,"巽为风卦",2),
    LSXG(35,16350,"雷山小过卦",2),
    SHJJ(36,16360,"水火即济卦",2),
    DTT(37,16370,"地天泰卦",3),
    TDF(38,16380,"天地否卦",3),
    DSQ(39,16390,"地山谦卦",3),
    LDY(40,16400,"雷地豫卦",3),
    ZLS(41,16410,"泽雷随卦",3),
    SFG(42,16420,"山风蛊卦",3),
    DLF(43,16430,"地雷复卦",3),
    SSM(44,16440,"山水蒙卦",4),
    TSS(45,16450,"天水讼卦",4),
    SDBG(46,16460,"山地剥卦",4),
    ZFDG(47,16470,"泽风大过卦",4),
    DHMY(48,16480,"地火明夷卦",4),
    HFD(49,16490,"火风鼎卦",4),
    GWS(50,16500,"艮为山卦",4),
    HSWJ(51,16510,"火水未济卦",4),
    SLT(52,16520,"水雷屯卦",5),
    FTXC(53,16530,"风天小畜卦",5),
    TLWW(54,16540,"天雷无妄卦",5),
    KWS(55,16550,"坎为水卦",5),
    TSD(56,16560,"天山遁卦",5),
    FHJR(57,16570,"风火家人卦",5),
    FZK(58,16580,"火泽睽卦",5),
    SSJ(59,16590,"水山蹇卦",5),
    SZS(60,16600,"山泽损卦",5),
    LZGM(61,16610,"雷泽归妹卦",5),
    HSL(62,16620,"火山旅卦",5),
    FSH(63,16630,"风水涣卦",5),
    FZZF(64,16640,"风泽中孚卦",5);

    private int hexagramId; //卦象ID
    private int achievementId; //成就ID
    private String hexagramName; //卦象名称
    private int hexagramLevel; //卦象级别 HexagramLevelEnum // 1 上上，2 中上，3 中中，4 中下，5 下下

    public static HexagramAchievementEnum fromValue(int hexagramId) {
        for (HexagramAchievementEnum item : values()) {
            if (item.getHexagramId() == hexagramId) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取等级卦象ID列表
     * @param hexagramLevel
     * @return
     */
    public static List<Integer> hexagramIdsByLevel(int hexagramLevel){
        List<Integer> hexagramIds = new ArrayList<>();
        for (HexagramAchievementEnum item : values()) {
            if (item.getHexagramLevel() == hexagramLevel) {
                hexagramIds.add(item.getHexagramId());
            }
        }
        return hexagramIds;
    }

    /**
     * 某个等级的卦象数量
     * @param userHexagramIds
     * @param hexagramLevel
     * @return
     */
    public static int countByIdsAndLevel(List<Integer> userHexagramIds,int hexagramLevel){
        return (int) Arrays.stream(values())
                .filter(h -> h.getHexagramLevel() == hexagramLevel)
                .filter(h -> userHexagramIds.contains(h.getHexagramId())).count();
    }
}
