package com.bbw.god.server.god;

import com.bbw.cache.ServerCacheService;
import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.city.CfgGodRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.server.PrepareServerDataService;
import com.bbw.god.server.ServerDataID;
import com.bbw.god.server.ServerDataType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 每日区服神仙配置服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-11 20:39
 */
@Slf4j
@Service
public class ServerGodDayConfigService extends PrepareServerDataService<ServerGod> {
    @Value("${day.gods.num:7}")
    private int godNum;// 每日生成的神仙数量
    @Autowired
    private ServerCacheService serverCacheService;

    @Override
    public void generateByDate(int sid, int dateInt) {
        List<CfgGodRoadEntity> godRoads = RoadTool.getGodRoads();
        // 洗牌
        Collections.shuffle(godRoads);
        List<GodEnum> godType = Arrays.asList(GodEnum.values());
        Collections.shuffle(godType);
        // 最多不超过神仙总数
        int totalGodNum = GodEnum.values().length;
        int maxGod = this.godNum > totalGodNum ? totalGodNum : this.godNum + 1;
        List<GodEnum> candidate = godType.subList(0, maxGod);
        Optional<GodEnum> bbx = candidate.stream().filter(god -> god == GodEnum.BBX).findFirst();
        List<GodEnum> result = new ArrayList<>(maxGod);
        if (!bbx.isPresent()) {// 一定要有百宝箱
            result.addAll(candidate.subList(1, candidate.size() - 1));
            result.add(GodEnum.BBX);
        } else {
            result.addAll(candidate);
        }
        // 生成所有神仙
        List<ServerGod> gods = new ArrayList<>();
        int index = 0;
        for (GodEnum god : result) {
            ServerGod serverGod = ServerGod.instance(sid, dateInt, god.getValue(), godRoads.get(index++).getId());
            Long id = ServerDataID.generateConfigID(sid, DateUtil.fromDateInt(dateInt), ServerDataType.GOD, getGodSeq(god));
            serverGod.setId(id);
            gods.add(serverGod);
        }
        this.serverData.addServerData(gods);
    }

    private int getGodSeq(GodEnum god) {
        for (int i = 0; i < GodEnum.values().length; i++) {
            if (GodEnum.values()[i] == god) {
                return i;
            }
        }
        return 99;
    }

    /**
     * 获取今日在地图上浮动的神仙
     *
     * @param sid
     * @return
     */
    public List<ServerGod> getTodayGods(int sid) {
        return getGods(sid, DateUtil.now());
    }

    /**
     * 获取某一天的神仙
     *
     * @param sid
     * @param date
     * @return
     */
    public List<ServerGod> getGods(int sid, Date date) {
        String loopKey = this.getLoopKeyByDate(date);
        List<ServerGod> gods = this.serverCacheService.getServerDatas(sid, ServerGod.class, loopKey);
        return gods;
    }

    @Override
    public boolean check(CfgServerEntity server, Date date) {
        String loopKey = this.getLoopKeyByDate(date);
        List<ServerGod> gods = this.serverData.getServerDatas(server.getMergeSid(), ServerGod.class, loopKey);
        if (gods.size() > 0 && !gods.stream().anyMatch(tmp -> tmp.getGodId() == 520)) {
            serverData.deleteServerDatas(server.getMergeSid(), ServerGod.class, loopKey);
            gods = new ArrayList<>();
        }
        if (gods.isEmpty()) {
            log.warn("错误!!![" + server.getMergeSid() + "][" + server.getName() + "]区服没有[" + DateUtil.toDateString(date) + "]的神仙数据!");
            generateByDate(server.getMergeSid(), DateUtil.toDateInt(date));
            log.warn("生成[" + server.getMergeSid() + "][" + server.getName() + "]区服[" + DateUtil.toDateString(date) + "]的神仙数据!");
            return false;
        }
        return true;
    }
}
