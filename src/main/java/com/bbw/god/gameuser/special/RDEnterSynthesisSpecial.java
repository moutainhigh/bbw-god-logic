package com.bbw.god.gameuser.special;

import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 进入合成特产界面
 * @date 2020/11/12 15:13
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RDEnterSynthesisSpecial extends RDCommon {
    private List<SynthesisSpecialInfo> infos;
    private Long remainTime;

    @Data
    @AllArgsConstructor
    public static class SynthesisSpecialInfo {
        // 合成特产的id
        private Integer specialId;
        // 合成特产所需要的材料id集合
        private List<Integer> materialIds;
        // 高价区域
        private Integer highPriceCountry;
    }
}
