package com.bbw.god.mall.skillscroll;

import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 返回给客户端的可指定合成的卷轴id集合
 * @date 2021/2/3 10:25
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RDDesignateInfoList extends RDCommon {
    private List<RDDesignateInfo> infos = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RDDesignateInfo {
        private Integer chapter;
        private List<Award> awards;
    }
}
