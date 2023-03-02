package com.bbw.god.gameuser.biyoupalace;

import com.bbw.common.ID;
import com.bbw.common.StrUtil;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import com.bbw.god.gameuser.biyoupalace.cfg.Chapter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家碧游宫
 *
 * @author suhq
 * @date 2019-09-11 10:15:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserBYPalaceLockSkill extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 9005217330075919061L;
    /** 已解锁的技能，格式为 法宝id,篇章 */
    private String lockSkill = "";

    public static UserBYPalaceLockSkill instance(long uid) {
        UserBYPalaceLockSkill instance = new UserBYPalaceLockSkill();
        instance.setGameUserId(uid);
        instance.setId(ID.INSTANCE.nextId());
        return instance;
    }

    public static UserBYPalaceLockSkill instance(long uid, String lockSkill) {
        UserBYPalaceLockSkill instance = new UserBYPalaceLockSkill();
        instance.setGameUserId(uid);
        instance.setId(ID.INSTANCE.nextId());
        instance.setLockSkill(lockSkill);
        return instance;
    }

    public void addSkill(int treasureId, int chapter) {
        String str = treasureId + "," + chapter + ";";
        if (!this.lockSkill.contains(str)) {
            this.lockSkill += str;
        }
    }

    /**
     * 是否为新的秘传
     * @param treasureId
     * @return
     */
    public boolean isNewSkill(int treasureId,int chapter){
        if(chapter < Chapter.SB1.getValue()){
            return false;
        }
        String str1 = treasureId + "," + Chapter.SB1.getValue() + ";";
        String str2 = treasureId + "," + Chapter.SB2.getValue() + ";";
        return !this.lockSkill.contains(str1) || !this.lockSkill.contains(str2);
    }

    /**
     * 获取秘传种类数量
     * @return
     */
    public int getSkillNumByMZ(){
        if (StrUtil.isNull(this.lockSkill)) {
            return 0;
        }
        List<Integer> mz = new ArrayList<>();
        for(String str : this.lockSkill.split(";")){
            List<Integer> ls = StrUtil.toList(str,",");
            if(ls.get(1) < Chapter.SB1.getValue()){
                continue;
            }
            if(mz.contains(ls.get(0))){
                continue;
            }
            mz.add(ls.get(0));
        }
        return mz.size();
    }


    @Override
    public UserDataType gainResType() {
        return UserDataType.BI_YOU_PALACE_LOCK_SKILL;
    }

}
