package com.bbw.god.gameuser.nightmarenvwam.pinchpeople;

import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 返回客户端捏土造人信息
 *
 * @author fzj
 * @date 2022/5/4 17:12
 */
@Data
public class RDPinchPeopleInfo extends RDSuccess {
    /** 泥人进度值 */
    private Integer progressToPinchPeople;
    /** 每日捏人评分 */
    private List<Integer> dayPinchPeopleScore;
    /** 累计评分 */
    private Integer pinchPeopleTotalScore;
}
