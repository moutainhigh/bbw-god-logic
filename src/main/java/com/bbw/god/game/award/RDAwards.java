package com.bbw.god.game.award;

import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suchaobin
 * @description 返给客户端的奖励的数据结构
 * @date 2020/9/17 15:29
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RDAwards extends RDCommon {
    private static final long serialVersionUID = -3621824377988236742L;
    private List<RDAward> awards;

    public static RDAwards getInstance(List<Award> awards) {
        List<RDAward> awardList = new ArrayList<>();
        for (Award award : awards) {
            RDAward rdAward = RDAward.getInstance(award);
            awardList.add(rdAward);
        }
        return new RDAwards(awardList);
    }
}
