package com.bbw.god.activityrank;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 奖励发送记录
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-15 22:01
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class RankAwardRecord extends ServerData {
    private String activityName;// 榜单名称
    private Long uid;// 玩家uid
    private int rank;// 排名
    private double score = 0.0;// 积分
    private Date sendTime = DateUtil.now();// 奖励发送时间
    private Long awardMailId = -1L;// 奖励邮件的ID

    public static RankAwardRecord newInstance(String name, Long uid, int sId, Long mailId, int rank) {
        RankAwardRecord record = new RankAwardRecord();
        record.setId(ID.INSTANCE.nextId());
        record.setUid(uid);
        record.setSid(sId);
        record.setAwardMailId(mailId);
        record.setActivityName(name);
        record.setRank(rank);
        return record;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.ACTIVITY_RANK_AWARD_RECORD;
    }

}
