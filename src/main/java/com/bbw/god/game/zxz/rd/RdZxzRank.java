package com.bbw.god.game.zxz.rd;

import com.bbw.common.DateUtil;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.entity.ZxzInfo;
import com.bbw.god.game.zxz.rank.ZxzRanker;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 返回榜单
 * @author: hzf
 * @create: 2022-09-26 23:54
 **/
@Data
public class RdZxzRank extends RDSuccess {

    private List<RdRank> rdRank;
    /** 榜单 可选时间 */
    private List<String> rankTime;
    /** 当前榜单的时间 */
    private String currentTime;


    /**
     * 获取每周诛仙阵信息
     * @param zxzInfos
     * @return 返回每周开始时间-结束时间的集合
     */
    public static List<String> gainRankTime(List<ZxzInfo> zxzInfos){
        //获取开始维护时间
        Date beginMaintainDate = DateUtil.fromDateTimeString(ZxzTool.getCfg().getBeginMaintainDate());
        //显示保留的时间
        zxzInfos.sort(Comparator.comparing(ZxzInfo::getGenerateTime).reversed());
        //根据开始时间降序

        zxzInfos = zxzInfos.stream().filter(zxzInfo -> zxzInfo.getGenerateTime() > beginMaintainDate.getTime()).limit(ZxzTool.getCfg().getRetainWeekNum()).collect(Collectors.toList());
        List<String> times = new ArrayList<>();
        for (ZxzInfo zxzInfo : zxzInfos) {
            times.add(zxzInfo.gainSplitTime());
        }
        return times;
    }

    public static  RdZxzRank getInstance(List<RdRank> rdRank, List<String> rankTime, String currentTime){
        RdZxzRank rd = new RdZxzRank();
        rd.setRdRank(rdRank);
        rd.setRankTime(rankTime);
        rd.setCurrentTime(currentTime);
        return rd;
    }

    @Data
    public static class  RdRank{
        /** 玩家ID */
        private Long uid;
        /** 头像 */
        private int head;
        /** 角色昵称 */
        private String nickname;
        /** 排名 */
        private int rank;
        /** 区域等级 */
        private Integer regionLevel;

        public static RdRank getInstance(GameUser gameUser, ZxzRanker ranker){
            RdRank rdRank = new RdRank();
            rdRank.setHead(gameUser.getRoleInfo().getHead());
            rdRank.setNickname(gameUser.getRoleInfo().getNickname());
            rdRank.setRank(ranker.getRank());
            rdRank.setUid(ranker.getUid());
            rdRank.setRegionLevel(ranker.getRegionLevel());
            return rdRank;
        }
    }

}
