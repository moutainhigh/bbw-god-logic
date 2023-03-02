package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficulty;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficultyLogic;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.ServerUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author suchaobin
 * @description 城池操作接口
 * @date 2020/11/2 9:35
 **/
@RestController
@RequestMapping("/gm/city")
public class GMCityCtrl {
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserGmService userGmService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private UserAttackDifficultyLogic attackDifficultyLogic;

    /**
     * 加封地
     *
     * @param sId
     * @param nickname
     * @param cityNames
     * @return
     */
    @RequestMapping("user!addCities")
    public Rst addCities(int sId, String nickname, String cityNames,Integer nightmare,Integer useEvent) {
        boolean isEvent=useEvent!=null && useEvent==1?true:false;
        if (nightmare!=null && nightmare==1){
            //添加梦魇城池
          return this.userGmService.addNightmareCities(sId, nickname, cityNames,isEvent);
        }
        return this.userGmService.addCities(sId, nickname, cityNames,isEvent);
    }

    @RequestMapping("user!updateAllCityTop")
    public Rst updateAllCityTop(String server, String nickname) {
        CfgServerEntity cfgServer = ServerTool.getServer(server);
        Optional<Long> optional = serverUserService.getUidByNickName(cfgServer.getMergeSid(), nickname);
        if (!optional.isPresent()) {
            return Rst.businessFAIL("玩家不存在");
        }
        long uid = optional.get();
        List<UserCity> userCities = userCityService.getUserCities(uid);
        userCities.forEach(userCity -> {
            userCity.setTcp(10);
            userCity.setQz(10);
            userCity.setLdf(10);
            userCity.setLbl(10);
            userCity.setKc(10);
            userCity.setJxz(10);
            userCity.setHierarchy(5);
            userCity.setFy(10);
            userCity.setDc(10);
            userCity.setFt(10);
        });
        gameUserService.updateItems(userCities);
        return Rst.businessOK();
    }

    /**
     * 完全重新计算攻城难度
     * @param server
     * @param nickname
     * @return
     */
    @RequestMapping("user!restAttackDifficulty")
    public Rst restAttackDifficulty(String server, String nickname){
        CfgServerEntity cfgServer = ServerTool.getServer(server);
        Optional<Long> optional = serverUserService.getUidByNickName(cfgServer.getMergeSid(), nickname);
        if (!optional.isPresent()) {
            return Rst.businessFAIL("玩家不存在");
        }
        long uid=optional.get();
        UserAttackDifficulty difficulty = attackDifficultyLogic.getAttackDifficulty(uid);
        attackDifficultyLogic.resettleAttackDifficulty(difficulty);
        return Rst.businessOK();
    }
}
