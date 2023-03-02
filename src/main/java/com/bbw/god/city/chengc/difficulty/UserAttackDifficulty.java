package com.bbw.god.city.chengc.difficulty;

import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

/**
 * 玩家攻城难度
 * @author：lwb
 * @date: 2020/12/17 16:06
 * @version: 1.0
 */
@Data
public class UserAttackDifficulty extends UserSingleObj {
    /**
     * 攻下的城池
     * 数组索引 对应为城池级别-1
     */
    private int[] ownCityNum ={0,0,0,0,0};
    private int[] ownNightMareCityNum ={0,0,0,0,0};
    /**
     * 攻下的关卡数量
     */
    private int[] passLevelNum ={0,0,0,0,0};
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_ATTACK_DIFFICULTY;
    }

    public void clear(){
        for (int i = 0; i < this.ownCityNum.length; i++) {
            this.ownCityNum[i]=0;
        }
        for (int i = 0; i < this.ownNightMareCityNum.length; i++) {
            this.ownNightMareCityNum[i]=0;
        }
        for (int i = 0; i < this.passLevelNum.length; i++) {
            this.passLevelNum[i]=0;
        }
    }
    public void addAttackCity(int level,boolean isNightmare){
        if (isNightmare){
            if (level<0 || level> ownNightMareCityNum.length){
                return;
            }
            ownNightMareCityNum[level-1]++;
        }else {
            if (level<0 || level> ownCityNum.length){
                return;
            }
            ownCityNum[level-1]++;
        }
    }

    public void addLevelDifficulty(int level){
        if (level<0 || level> passLevelNum.length){
            return;
        }
        passLevelNum[level-1]++;
    }

    /**
     * 根据城池级别 获取对应的 卡牌难度加成值
     * @param cityLv
     * @return
     */
    public int settleCardDifficultyByCityLevel(int index){
        int cityLv=index+1;
        if (cityLv==5){
            return 20;
        }
        int attackNum=ownCityNum[index]+ passLevelNum[index];
        switch (cityLv){
            case 1:
                if (attackNum<55){
                    return attackNum/5;
                }
                return 11+(attackNum-55)/3;
            case 2:
                if (attackNum<40){
                    return attackNum/4+1;
                }
                return 11+(attackNum-40)/2;
            case 3: return attackNum/2+1;
            case 4:
                return attackNum+1;
            default: return 20;
        }
    }
}
