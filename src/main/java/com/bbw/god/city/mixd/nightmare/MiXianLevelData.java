package com.bbw.god.city.mixd.nightmare;

import com.bbw.common.ListUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 说明：
 * 迷仙洞关卡数据
 *
 * @author lwb
 * date 2021-05-26
 */
@Data
public class MiXianLevelData implements Serializable {
    private static final long serialVersionUID = 5214285726518613155L;
    /**
     * 关卡数据 5 X 10格子
     * 0,0为屏幕左下角第一个格子
     */
    private List<PosData> posDatas = new ArrayList<>(50);
    private Integer pos;//玩家位置
    private Integer level = 1;//当前层

    public static MiXianLevelData getInstance() {
        MiXianLevelData miXianLevelData = new MiXianLevelData();
        return miXianLevelData;
    }

    /**
     * 添加格子数据
     *
     * @param list
     */
    public void joinPosData(List<NightmareMiXianPosEnum> list, boolean showAll) {
        for (int i = 0; i < list.size(); i++) {
            NightmareMiXianPosEnum posEnum = list.get(i);
            if (NightmareMiXianPosEnum.PLAYER.equals(posEnum)) {
                posEnum = NightmareMiXianPosEnum.EMPTY;
                posDatas.add(PosData.getInstance(i + 1, posEnum, true));
            } else {
                posDatas.add(PosData.getInstance(i + 1, posEnum, showAll));
            }
        }
    }


    /**
     * 获得格子数据
     *
     * @param pos
     */
    public PosData getPosData(int pos) {
        return posDatas.stream().filter(p -> p.ifThisPos(pos)).findFirst().orElse(null);
    }

    /**
     * 显示某个格子
     *
     * @param pos
     */
    public void showPos(int pos) {
        PosData posData = getPosData(pos);
        if (posData != null) {
            posData.setShow(true);
        }
    }

    /**
     * 将指定格子 改变为 显示的空白格
     *
     * @param pos
     */
    public void takePosToEmpty(int pos) {
        Optional<PosData> optional = posDatas.stream().filter(p -> p.ifThisPos(pos)).findFirst();
        if (optional.isPresent()) {
            PosData posData = optional.get();
            posData.setShow(true);
            posData.setTye(NightmareMiXianPosEnum.EMPTY.getType());
        }
    }

    /**
     * 统计当前类型的 格子数量
     *
     * @param type
     * @return
     */
    public int settlePosNumByType(NightmareMiXianPosEnum type) {
        Long num = posDatas.stream().filter(p -> p.getTye() == type.getType()).count();
        return num.intValue();
    }

    /**
     * 计算巡使剩余数量
     *
     * @return
     */
    public int settleXunShiNum() {
        Long num = posDatas.stream().filter(p -> p.getTye() == NightmareMiXianPosEnum.XUN_SHI_XD.getType() || p.getTye() == NightmareMiXianPosEnum.XUN_SHI_LEADER.getType()).count();
        return num.intValue();
    }

    /**
     * 获取宝库的奖励数量
     *
     * @return
     */
    public int gainTreasureHourseAwardNum() {
        int awardNum = 0;
        if (ListUtil.isEmpty(posDatas)) {
            return awardNum;
        }
        for (PosData posData : posDatas) {
            if (posData.getTye() != 0 && posData.getTye() != NightmareMiXianPosEnum.TREASURE_HOUSE_GATE.getType()) {
                awardNum++;
            }
        }
        return awardNum;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PosData implements Serializable {
        private static final long serialVersionUID = 5574571992193907075L;
        private Integer pos;
        private Integer tye;
        private boolean show = false;

        public boolean ifThisPos(int pos) {
            return this.pos == pos;
        }

        public static PosData getInstance(int pos, NightmareMiXianPosEnum type) {
            return new PosData(pos, type.getType(), false);
        }

        public static PosData getInstance(int pos, NightmareMiXianPosEnum type, boolean show) {
            return new PosData(pos, type.getType(), show);
        }

        public boolean ifXunShiPos() {
            if (NightmareMiXianPosEnum.XUN_SHI_LEADER.getType() == tye) {
                return true;
            }
            if (NightmareMiXianPosEnum.XUN_SHI_XD.getType() == tye) {
                return true;
            }
            if (NightmareMiXianPosEnum.XUN_SHI_ZHU_LONG.getType() == tye) {
                return true;
            }
            if (NightmareMiXianPosEnum.XUN_SHI_JIANG_HUAN.getType() == tye) {
                return true;
            }
            return false;
        }

        /**
         * 判断是否有元宝，铜钱，元素
         *
         * @return
         */
        public boolean ifGoldCopperElePos() {
            if (NightmareMiXianPosEnum.GOLD.getType() == tye) {
                return true;
            }
            if (NightmareMiXianPosEnum.COPPER.getType() == tye) {
                return true;
            }
            if (NightmareMiXianPosEnum.ELE.getType() == tye) {
                return true;
            }
            return false;
        }
    }
}
