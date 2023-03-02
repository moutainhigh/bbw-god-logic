package com.bbw.god.activityrank;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
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
public class GameRankAwardRecord extends GameData {
    private String activityName;// 榜单名称
    private Long uid;// 玩家uid
    private int rank;// 排名
    private double score = 0.0;// 积分
    private Date sendTime = DateUtil.now();// 奖励发送时间
    private Long awardMailId = -1L;// 奖励邮件的ID

    public static GameRankAwardRecord newInstance(long uid, String activityName, int rank, long awardMailId) {
        GameRankAwardRecord record = new GameRankAwardRecord();
        record.setId(ID.INSTANCE.nextId());
        record.setUid(uid);
        record.setAwardMailId(awardMailId);
        record.setActivityName(activityName);
        record.setRank(rank);
        return record;
    }

    public static GameRankAwardRecord newInstance(RankAwardRecord r) {
        GameRankAwardRecord record = new GameRankAwardRecord();
        record.setId(ID.INSTANCE.nextId());
        record.setUid(r.getUid());
        record.setAwardMailId(r.getAwardMailId());
        record.setActivityName(r.getActivityName());
        record.setRank(r.getRank());
        return record;
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.ACTIVITY_RANK_AWARD_RECORD;
    }

}
