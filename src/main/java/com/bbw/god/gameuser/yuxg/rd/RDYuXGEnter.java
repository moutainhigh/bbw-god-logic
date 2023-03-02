package com.bbw.god.gameuser.yuxg.rd;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 进入玉虚宫
 *
 * @author: suhq
 * @date: 2021/10/19 3:48 下午
 */
@Data
public class RDYuXGEnter extends RDSuccess {
    /** 法坛总等级 */
    private int faTanTotalLevel;
    /** 熔炼值 */
    private int meltValue;
    /** 当前符坛 */
    private int curfuTan;
    /** 祈福概率加成值 */
    private double addition;
    /** 是否使用神水 0-未使用、1-使用 */
    private int activeShenShui;
    /**
     * 祈福设置
     * ①　直到所选材料消耗完毕
     * ②　祈符到玄阶符首时停止
     * ③　祈符到地阶符首时停止
     * ④　祈符到天阶符首时停止
     */
    private int praySetting;

    /**
     * 许愿清单
     */
    private List<RdWishingDetailed.RdDetailed> wishingDetailed;
    /** 当前符坛的许愿清单*/
    private RdWishingDetailed curWishingDetailed;

 }