package com.bbw.god.city.chengc.difficulty;

import com.bbw.common.ID;
import com.bbw.common.ListUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 玩家攻城难度
 * @author：lwb
 * @date: 2020/12/17 16:11
 * @version: 1.0
 */
@Service
public class UserAttackDifficultyLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;

    /**
     * 获取攻城难度，如果不存在 则创建新的对象
     * @param uid
     * @return
     */
    public UserAttackDifficulty getAttackDifficulty(long uid){
        UserAttackDifficulty attackDifficulty = gameUserService.getSingleItem(uid, UserAttackDifficulty.class);
        if (attackDifficulty==null){
            attackDifficulty=new UserAttackDifficulty();
            attackDifficulty.setGameUserId(uid);
            attackDifficulty.setId(ID.INSTANCE.nextId());
            resettleAttackDifficulty(attackDifficulty);
            gameUserService.addItem(uid,attackDifficulty);
        }
        return attackDifficulty;
    }

    /**
     * 重新计算攻城难度
     * @param attackDifficulty
     */
    public void resettleAttackDifficulty(UserAttackDifficulty attackDifficulty){
        if (attackDifficulty==null){
            return;
        }
        long uid=attackDifficulty.getGameUserId();
        List<UserCity> userCities = userCityService.getUserCities(uid);
        attackDifficulty.clear();
        if (ListUtil.isNotEmpty(userCities)){
            for (UserCity city : userCities) {
                if (city.isOwn()){
                    int lv=city.gainCity().getLevel();
                    attackDifficulty.addAttackCity(lv,false);
                    if (lv>1){
                        for (int i=lv-1;i>0;i--){
                            attackDifficulty.addLevelDifficulty(i);
                        }
                    }
                }else if (city.getProcess()!=null){
                    for (int i = 0; i < city.getProcess().length; i++) {
                        if (city.getProcess()[i]>0){
                            attackDifficulty.addLevelDifficulty(i+1);
                        }
                    }
                }
            }
        }
        List<UserNightmareCity> nightmareCities = userCityService.getUserOwnNightmareCities(uid);
        if (ListUtil.isNotEmpty(nightmareCities)){
            for (UserNightmareCity nightmareCity : nightmareCities) {
                if (nightmareCity.isOwn()){
                    attackDifficulty.addAttackCity(nightmareCity.gainCity().getLevel(),true);
                }
            }
        }
        gameUserService.updateItem(attackDifficulty);
    }

    /**
     * 更新攻城关卡难度
     * @param uid
     * @param passLevel
     */
    public void updateAttackLevelDifficulty(long uid,int passLevel){
        UserAttackDifficulty difficulty = getAttackDifficulty(uid);
        difficulty.addLevelDifficulty(passLevel);
        gameUserService.updateItem(difficulty);
    }

    /**
     * 更新攻下的城池
     * @param uid
     * @param cityLevel
     * @param isNightmare
     */
    public void updateAttackCityDifficulty(long uid,int cityLevel,boolean isNightmare){
        UserAttackDifficulty difficulty = getAttackDifficulty(uid);
        difficulty.addAttackCity(cityLevel,isNightmare);
        gameUserService.updateItem(difficulty);
    }
}
