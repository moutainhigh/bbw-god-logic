package com.bbw.god.activity.holiday.processor.holidayspcialyeguai;

import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.holiday.UserYouHun;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.city.yeg.RDArriveYeG;
import com.bbw.god.fight.RDFightEndInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.rd.RDCommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 盗宝贼寇类型活动继承该类
 *
 * @author fzj
 * @date 2021/12/15 13:07
 */
public abstract class AbstractSpecialYeGuaiProcessor extends AbstractActivityProcessor {
    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return false;
    }

    /**
     * 发送活动额外奖励
     *
     * @param uid
     * @param fightEndInfo
     * @param rd
     */
    public abstract void sendActivityExtraAward(long uid, RDFightEndInfo fightEndInfo, RDCommon rd);

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid){
        ActivityEnum activityEnum = this.activityTypeList.stream().findFirst().orElse(null);
        if (null == activityEnum){
            return false;
        }
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return null != a;
    }

    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 1.活动期间，将在大地图所有野怪图标上增加游魂标识，踩在拥有游魂标识的野怪格上时，弹出的标题为“精英游魂来袭”或“游魂来袭”。
     * 生成的野怪将会变更为游魂的头像（老版鬼道士头像），名称变为“游魂”或“游魂（精英）”。
     * 2.击败游魂时，将根据变更为游魂的野怪（普通或者精英，概率跟常规野怪相同）。
     *
     * @param uid
     * @param info
     * @param rdFightsInfo
     */
    public abstract void changeFightInfo(long uid, RDArriveYeG info, RDFightsInfo rdFightsInfo);

    /**
     * 获取特殊野怪存储信息
     *
     * @param needBuild 不存在时是否需要创建
     * @return
     */
    public Optional<UserYouHun> getYouHun(long uid, boolean needBuild) {
        UserYouHun userYouHun = gameUserService.getSingleItem(uid, UserYouHun.class);
        if (userYouHun == null) {
            if (needBuild) {
                userYouHun = UserYouHun.instance(uid);
                gameUserService.addItem(uid, userYouHun);
                return Optional.of(userYouHun);
            }
            return Optional.empty();
        }
        if (userYouHun.updateSpecialYeGuai()) {
            //更新特殊野怪
            updateSpecialYeGuai(userYouHun);
        }
        return Optional.of(userYouHun);
    }

    /**
     * 更新特殊野怪
     *
     * @param userYouHun
     */
    private void updateSpecialYeGuai(UserYouHun userYouHun) {
        userYouHun.setInvalidYouHun(new ArrayList<>());
        userYouHun.updateLastRestTime();
        gameUserService.updateItem(userYouHun);
    }

    /**
     * 野怪格子是否有特殊野怪
     *
     * @param uid
     * @param yeDiId
     * @return
     */
    public boolean hasYouHun(long uid, int yeDiId) {
        Optional<UserYouHun> optional = getYouHun(uid, false);
        if (optional.isPresent()) {
            return !optional.get().getInvalidYouHun().contains(yeDiId);
        }
        return true;
    }

    /**
     * 标记移除特殊野怪
     *
     * @param uid
     * @param yeDiId
     */
    public void removeYouHun(long uid, int yeDiId) {
        if (isOpened(gameUserService.getActiveSid(uid))) {
            UserYouHun userYouHun = getYouHun(uid, true).get();
            userYouHun.getInvalidYouHun().add(yeDiId);
            gameUserService.updateItem(userYouHun);
            return;
        }
        Optional<UserYouHun> userYouHun = getYouHun(uid, false);
        if (userYouHun.isPresent()) {
            gameUserService.deleteItem(userYouHun.get());
        }
    }

    public String getYouHunPos(long uid) {
        if (!isOpened(gameUserService.getActiveSid(uid))) {
            return null;
        }
        List<Integer> allPos = CityTool.getAllYeGuaiPos();
        Optional<UserYouHun> optional = getYouHun(uid, false);
        if (optional.isPresent()) {
            UserYouHun youHun = optional.get();
            allPos.removeAll(youHun.getInvalidYouHun());
        }
        if (ListUtil.isEmpty(allPos)) {
            return null;
        }
        StringBuilder posSB = new StringBuilder();
        for (Integer pos : allPos) {
            posSB.append("N");
            posSB.append(pos);
        }
        return posSB.substring(1);
    }
}
