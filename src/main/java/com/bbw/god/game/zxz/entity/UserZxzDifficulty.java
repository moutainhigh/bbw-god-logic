package com.bbw.god.game.zxz.entity;

import com.bbw.common.DateUtil;
import com.bbw.god.game.zxz.cfg.CfgZxzLevel;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.award.ZxzAwardTool;
import com.bbw.god.game.zxz.enums.ZxzDifficultyEnum;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 玩家区域数据
 *
 * @author: hzf
 * @create: 2022-09-17 10:10
 **/
@Data
public class UserZxzDifficulty implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 难度类型 */
    private Integer difficulty;
    /** 通关评分 */
    private Integer clearanceScore;
    /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
    private Integer status;
    /** 通关次数 */
    private Integer clearanceNum;
    /** 进入的区域 */
    private Integer enterRegion;
    /** 全通奖励 */
    private Integer allPassedAwarded;
    /** 最近一次刷新时间 */
    private Date lastRefreshDate = new Date() ;

    /**
     * 构建实例
     *
     * @param zxzDifficulty 难度枚举
     * @return
     */
    public static UserZxzDifficulty getInstance(ZxzDifficultyEnum zxzDifficulty) {
        UserZxzDifficulty userZxzDifficulty = new UserZxzDifficulty();
        userZxzDifficulty.setDifficulty(zxzDifficulty.getDifficulty());
        userZxzDifficulty.setClearanceScore(0);
        userZxzDifficulty.setClearanceNum(0);
        userZxzDifficulty.setAllPassedAwarded(0);
        if (zxzDifficulty.getDifficulty() == ZxzDifficultyEnum.DIFFICULTY_10.getDifficulty()) {
            userZxzDifficulty.setStatus(ZxzStatusEnum.ABLE_ATTACK.getStatus());
        } else {
            userZxzDifficulty.setStatus(ZxzStatusEnum.NOT_OPEN.getStatus());
        }
        userZxzDifficulty.setLastRefreshDate(DateUtil.now());
        return userZxzDifficulty;
    }

    /**
     * 获取基准等级 通关评分/当前难度的区域数量
     * @return
     */
    public Integer gainReferenceLv(){
        CfgZxzLevel zxzLevel = ZxzTool.getZxzLevel(difficulty);
        //区域数量
        int regionNum = zxzLevel.getRegions().size();
        int referenceLv = clearanceScore / regionNum;
        return  referenceLv == 0 ? 1:referenceLv;
    }
    /**
     * 获取基准等级和
     * @return
     */
    public Integer gainReferenceLvs(){
        CfgZxzLevel zxzLevel = ZxzTool.getZxzLevel(difficulty);
        int regionNum = zxzLevel.getRegions().size();
        int referenceLvs = gainReferenceLv() * regionNum;
        return referenceLvs;
    }

    /**
     * 判断全通奖励是否可领取
     * @return
     */
    public boolean ifAllPassedAwarded(){
        return allPassedAwarded == 1;
    }

    /**
     * 领取全通奖励
     */
    public void receiveAllPassedAwarded(){
        allPassedAwarded = 1;
    }

    /**
     * 获取通关次数,+1:通关次数记录的从0开始
     * @return
     */
    public Integer gainClearanceNum() {
        return null == clearanceNum || 0 == clearanceNum ? 1 : clearanceNum;
    }

    /**
     * 添加通关次数
     */
    public void  addClearanceNum(){
        Integer clearanceNumLimit = ZxzAwardTool.getCfg().getClearanceNumLimit();
        if (clearanceNum >= clearanceNumLimit) {
           clearanceNum = clearanceNumLimit;
           return;
        }
        clearanceNum++;
    }

    /**
     * 获取状态
     * @return
     */
    public Integer gainStatus() {
        return null == status ? 0 : status;
    }

    /**
     * 获取评分
     * @return
     */
    public Integer gainClearanceScore() {
        return null == clearanceScore ? 0 : clearanceScore;
    }

    /**
     * 结算
     *
     * @param score 通关评分
     */
    public void settlement(Integer score) {
        //计算通关评分
        clearanceScore = score;
        //修改通关状态
        status = ZxzStatusEnum.PASSED.getStatus();
        //添加通关次数
        addClearanceNum();
    }

    /**
     * 刷新
     */
    public void autoRefresh() {
        this.status = ZxzStatusEnum.ABLE_ATTACK.getStatus();
        this.allPassedAwarded = 0;
        this.enterRegion = null;

        this.lastRefreshDate = DateUtil.now();
    }

    /**
     * 重置通关评分
     */
    public void resetClearanceScore(){
        if (gainClearanceNum() > 0) {
            this.clearanceScore = 0;
        }
    }

    /**
     * 判断难度是否通关
     *
     * @return
     */
    public boolean ifClearanceScore() {
        if (gainClearanceScore() > 0) {
            return true;
        }
        return false;
    }

}
