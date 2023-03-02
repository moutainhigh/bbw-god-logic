package com.bbw.god.gameuser.nightmarenvwam.pinchpeople;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.util.List;

/**
 * 返回客户端的泥人信息
 *
 * @author fzj
 * @date 2022/5/4 16:37
 */
@Data
public class RDPinchPeople extends RDCommon {
    /** 捏人分数 */
    private Integer pinchPeopleScore;
    /** 每日分数 */
    private List<Integer> dayScoreList;
}
