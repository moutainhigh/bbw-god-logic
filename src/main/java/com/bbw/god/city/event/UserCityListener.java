package com.bbw.god.city.event;

import com.bbw.cache.UserCacheService;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.cache.ShareCacheUtil;
import com.bbw.god.cache.ShareCacheUtil.ShareStatus;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.difficulty.UserAttackDifficultyLogic;
import com.bbw.god.city.yeg.YeGProcessor;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.detail.async.CityOwnDetailAsyncHandler;
import com.bbw.god.event.EventParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.city.CCLevelEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.nightmarecity.chengc.UserNightmareCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 城池事件处理
 *
 * @author suhq
 * @date 2018年11月29日 下午3:29:53
 */
@Component
public class UserCityListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    protected UserCacheService userCacheService;
    @Autowired
    private YeGProcessor yeGProcessor;
    @Autowired
    private MailService mailService;
    @Autowired
    private UserAttackDifficultyLogic userAttackDifficultyLogic;
    @Autowired
    private CityOwnDetailAsyncHandler cityOwnDetailAsyncHandler;

    /**
     * 通过城池关卡
     *
     * @param event
     */
    @EventListener
    public void passCityLevel(UserPassCityLevelEvent event) {
        EPPassCityLevel ep = event.getEP();
        RDFightResult rd = (RDFightResult) ep.getRd();
        int passLevel = ep.getPassLevel();
        CfgCityEntity city = CityTool.getCityById(ep.getCityId());
        String msg = "恭喜您通过了【" + city.getName() + "】第" + passLevel+ "关卡!";
        if(ep.isNightmare()){
            msg = "恭喜您打败了【" + city.getName() + "】护卫军!";
        }else {
            UserCity userCity = getUserCity(ep.getGuId(), ep.getCityId());
            userCity.getProcess()[passLevel-1]=1;
            userAttackDifficultyLogic.updateAttackLevelDifficulty(ep.getGuId(),passLevel);
            userCacheService.updateUserData(userCity);
        }
        // TODO：国际化信息
        rd.setWinDes(msg);
    }

    @EventListener
    public void addUserCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        long guId = ep.getGuId();
        RDFightResult rd = (RDFightResult) ep.getRd();
        CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
        String msg;
        if (ep.getValue().isNightmare()) {
            msg = "您唤醒了梦魇世界 " + CCLevelEnum.fromValue(city.getLevel()).getName() + " " + city.getName() + "!";
            UserNightmareCity nightmareCity = getUserNightmareCity(guId, city.getId());
            nightmareCity.setOwn(true);
            nightmareCity.setOwnTime(new Date());
            gameUserService.updateItem(nightmareCity);
        } else {
            msg = "您攻下了" + CCLevelEnum.fromValue(city.getLevel()).getName() + " " + city.getName() + "!";
            UserCity usercity = getUserCity(guId,city.getId());
            if(!usercity.isOwn()){
                usercity.setOwn(true);
                if (usercity.getProcess()!=null){
                    List<Award> awards=new ArrayList<>();
                    for (int i = 0; i < usercity.getProcess().length; i++) {
                        if (usercity.getProcess()[i]!=1){
                            continue;
                        }
                        if (i<3){
                            awards.addAll(yeGProcessor.getRandomBoxAwards(guId, YeGuaiEnum.YG_NORMAL));
                        }else {
                            awards.addAll(yeGProcessor.getRandomBoxAwards(guId, YeGuaiEnum.YG_ELITE));
                        }
                        usercity.getProcess()[i]=2;
                    }
                    if (ListUtil.isNotEmpty(awards)){
                        String title= LM.I.getMsgByUid(guId,"mail.attack.city.Unopened.award.title",city.getName());
                        String content=LM.I.getMsgByUid(guId,"mail.attack.city.Unopened.award.content");
                        mailService.sendAwardMail(title,content,guId,awards);
                    }
                }
            }
            gameUserService.updateItem(usercity);
        }
        userAttackDifficultyLogic.updateAttackCityDifficulty(ep.getGuId(),city.getLevel(),ep.getValue().isNightmare());
        // TODO：国际化信息
        rd.setWinDes(msg);

        // 设置分享缓存
        if (city.getLevel() > 3) {
            ShareCacheUtil.setShareableAttack(guId, ShareStatus.ENABLE_AWARD);
        }
        cityOwnDetailAsyncHandler.log(ep);
//        System.out.println("添加城池成功");
    }

    private UserCity getUserCity(long uid,int cityId){
        UserCity userCity = userCityService.getUserCity(uid,cityId);
        if (userCity==null){
            //创建
            CfgCityEntity city = CityTool.getCityById(cityId);
            userCity=UserCity.fromCfgCity(uid,city);
            userCity.setOwn(false);
            if (city.getLevel()>1){
                userCity.setProcess(new int[city.getLevel()-1]);
            }
            userCacheService.addUserData(userCity);
        }
        return userCity;
    }

    private UserNightmareCity getUserNightmareCity(long uid,int cityId){
        UserNightmareCity userCity = userCityService.getUserNightmareCity(uid,cityId);
        if (userCity==null){
            //创建
            CfgCityEntity city = CityTool.getCityById(cityId);
            userCity=UserNightmareCity.getInstance(city,uid);
            userCity.setOwn(false);
            userCity.setProcess(new int[1]);
            userCacheService.addUserData(userCity);
        }
        return userCity;
    }
}
