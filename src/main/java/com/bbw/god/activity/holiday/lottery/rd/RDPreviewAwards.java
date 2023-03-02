package com.bbw.god.activity.holiday.lottery.rd;

import com.bbw.god.game.award.Award;
import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 预览奖励
 * @date 2020/8/27 16:22
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class RDPreviewAwards extends RDCommon {
    private static final long serialVersionUID = -705363628454655280L;
    private List<Award> awards;
}
