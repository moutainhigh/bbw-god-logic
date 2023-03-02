package com.bbw.god.db.pool;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.IActivityRank;
import com.bbw.god.db.entity.InsUserDetailEntity;
import com.bbw.god.db.service.InsUserDetailService;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gm.service.ActivityRankRegenerateService.ActivityRanker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * 区服操作类
 * 使用一下语句获取bean
 *   DetailDataDAO pdd = SpringContextUtil.getBean(DetailDataDAO.class, serverId);
 * </pre>
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-21 22:05
 */
@Service
@Scope("prototype")
@Lazy
public class DetailDataDAO {
    @Value("${bbw-god.db-batchSize:1000}")
    private int batchSize = 1000;
    private int sid;

    @Autowired
    private InsUserDetailService service;
    @Autowired
    private JdbcTemplate jdbc;

    @SuppressWarnings("unused")
    private DetailDataDAO() {

    }

    public DetailDataDAO(int sid) {
        this.sid = sid;
    }

    public int getSid() {
        return sid;
    }

    public boolean dbInsertInsUserDetailEntity(InsUserDetailEntity entity) {
        return service.insert(entity);
    }

    public boolean dbBatchInsertInsUserDetailEntity(List<InsUserDetailEntity> entities) {
        return service.insertOrUpdateBatch(entities);
    }

    public List<ActivityRanker> dbGetRankersFromDetail(IActivityRank ar) {
        String sql = "";
        String partSql = " FROM %s where opdate>=%s and optime>=%s and opdate<=%s and optime<=%s ";
        ActivityRankEnum type = ActivityRankEnum.fromValue(ar.gainType());
        switch (type) {
            case FUHAO_RANK:
                sql = "SELECT uid,SUM(value_change) as value" + partSql + "AND award_type = 20 AND way != 3113 && way != 3810 AND value_change > 0 group by uid";
                break;
            case ELE_CONSUME_RANK:
                sql = "SELECT uid,-SUM(value_change) as value" + partSql + "AND award_type = 50 AND value_change < 0 group by uid";
                break;
            case WIN_BOX_RANK:
                sql = "SELECT uid,SUM(value_change) as value" + partSql + "AND award_type = 60 and award_id=512 group by uid";
                break;
            default:
                break;
        }

        int beginDateInt = DateUtil.toDateInt(ar.gainBegin());
        int beginTimeInt = DateUtil.toHMSInt(ar.gainBegin());
        int endDateInt = DateUtil.toDateInt(ar.gainEnd());
        int endTimeInt = DateUtil.toHMSInt(ar.gainEnd());
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String table = "ins_user_detail_" + i;

            String runSql = String.format(sql, table, beginDateInt, beginTimeInt, endDateInt, endTimeInt);
            // System.out.println(runSql);
            List<Map<String, Object>> values = jdbc.queryForList(runSql);
            if (ListUtil.isNotEmpty(values)) {
                results.addAll(values);
            }
        }
        if (ListUtil.isNotEmpty(results)) {
            return results.stream().map(r -> new ActivityRanker(((BigInteger) r.get("uid")).longValue(), ((BigDecimal) r.get("value")).intValue())).collect(Collectors.toList());
        }
        return new ArrayList<>();

    }

    /**
     * 累计奖励获取
     *
     * @param uid     玩家ID
     * @param award   奖励类型
     * @param awardId 奖励ID
     * @param from    进行累计的开始时间 eg：20221101
     * @param to      进行累计的结束时间 eg：20221110
     * @return
     */
    public Long dbTotalAwarded(long uid, AwardEnum award, int awardId, int from, int to) {
        String table = "ins_user_detail_" + uid % 10;
        String sql = "SELECT SUM(value_change) as value " +
                "FROM  " + table + " " +
                "where uid = %s AND opdate >= %s AND opdate <= %s and award_type= %s and award_id = %s AND value_change > 0";

        String runSql = String.format(sql, uid, from, to, award.getValue(), awardId);
        Map<String, Object> values = jdbc.queryForMap(runSql);
        if (null == values || values.isEmpty()) {
            return 0L;
        }
        BigDecimal value = (BigDecimal) values.get("value");
        if (null == value) {
            return 0L;
        }
        return value.longValue();
    }
}
