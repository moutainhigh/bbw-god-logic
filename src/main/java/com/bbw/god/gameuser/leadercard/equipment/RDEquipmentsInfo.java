package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.common.ListUtil;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 装备星图升星
 *
 * @author suhq
 * @date 2021-03-26 17:57
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDEquipmentsInfo extends RDSuccess {
    private List<RDEquipmentInfo> equipments = new ArrayList<>();

    public void addEquipmentInfo(List<UserLeaderEquipment> userLeaderEquipments) {
        if (ListUtil.isNotEmpty(userLeaderEquipments)) {
            for (UserLeaderEquipment ule : userLeaderEquipments) {
                RDEquipmentInfo info = new RDEquipmentInfo(ule.getEquipmentId());
                info.setLevel(ule.getLevel());
                info.setQuality(ule.getQuality());
                info.setStarMapProgress(ule.getStarMapProgress());
                equipments.add(info);
            }
        }
    }

    @Data
    public static class RDEquipmentInfo {
        private Integer equipmentId;
        private Integer level = 0;
        private Integer quality = 10;
        /** 星图进度 */
        private Integer starMapProgress = 0;

        RDEquipmentInfo(int equipmentId) {
            this.equipmentId = equipmentId;
        }
    }
}
