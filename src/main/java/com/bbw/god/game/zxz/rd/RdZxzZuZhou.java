package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.zxz.entity.ZxzEntry;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回诛仙阵 诅咒效果
 * @author: 殇璃
 * @create: 2022-12-26 14:53
 **/
@Data
public class RdZxzZuZhou extends RDSuccess {

    private List<RdRegionZuZhou> regionZuZhous;

    public static RdRegionZuZhou getInstance(List<ZxzEntry> entries,Integer regionId){
        RdRegionZuZhou rdRegionZuZhou = new RdRegionZuZhou();
        rdRegionZuZhou.setRegionId(regionId);

        List<RdZuZhou> rdZuZhous = new ArrayList<>();
        for (ZxzEntry entry : entries) {
            RdZuZhou rdZuZhou = RdZuZhou.getInstance(entry);
            rdZuZhous.add(rdZuZhou);
        }
        rdRegionZuZhou.setZuZhous(rdZuZhous);
        return rdRegionZuZhou;
    }

    @Data
    public static class RdRegionZuZhou{
        private Integer regionId;
        private List<RdZuZhou> zuZhous;
    }


    @Data
    public static class RdZuZhou{
        private Integer entryId;
        private Integer entryLv;

        public static RdZuZhou getInstance(ZxzEntry entry){
            RdZuZhou rdZuZhou = new RdZuZhou();
            rdZuZhou.setEntryId(entry.getEntryId());
            rdZuZhou.setEntryLv(entry.getEntryLv());
            return rdZuZhou;
        }

    }
}
