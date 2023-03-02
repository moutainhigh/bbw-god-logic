package com.bbw;

import com.bbw.common.DateUtil;
import com.bbw.common.JSONUtil;
import com.bbw.common.StrUtil;
import com.bbw.common.ZipUtil;
import com.bbw.god.db.entity.AttackCityStrategyEntity;
import com.bbw.god.db.service.AttackCityStrategyService;
import com.bbw.god.game.combat.data.CombatResultEnum;
import com.bbw.god.game.combat.data.RDCombat;
import com.bbw.god.game.combat.video.CombatVideo;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author：lwb
 * @date: 2020/11/30 16:21
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LogicServerApplication.class }) // 指定启动类
public class CityWarTest {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private AttackCityStrategyService attackCityStrategyService;
    @Test
    public void test() throws Exception{
        File file=new File("D:\\原文件");
        File[] listFiles = file.listFiles();
        int i=0;
        for (File file1 : listFiles) {
            File[] itemFiles = file1.listFiles();
            String city=file1.getName();
            int cityId= CityTool.getChengCByName(city).getId();
            new File("D:\\history\\"+city).mkdir();
            Map<String,Long> map=new HashMap<>();
            for (File itemFile : itemFiles) {
                System.err.println("解压："+itemFile.getName());
                String fileName=itemFile.getName().replaceAll(".zip",".json");
                ZipUtil.releaseZipToFile(itemFile,"D:\\history\\"+city+"\\"+fileName);
                String[] split = fileName.split(".json");
                Long id=Long.valueOf(split[0]);
                if (id<200800000000000000l){
                    File nfile=new File("D:\\history\\"+city+"\\"+fileName);
                    nfile.delete();
                    continue;
                }
                if (!read("D:\\history\\"+city+"\\"+fileName,id,city,cityId,map)){
                    File nfile=new File("D:\\history\\"+city+"\\"+fileName);
                    nfile.delete();
                }
            }
    }
}

    public boolean read(String filePath,long id,String city,int cityId,Map<String,Long> map) throws Exception {
        String encoding="UTF-8";
        File file=new File(filePath);
        String json="";
        if(file.isFile() && file.exists()) {
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                json+=lineTxt;
            }
            read.close();
        }
        if (StrUtil.isBlank(json)){
            System.err.println("空的"+filePath);
            return false;
        }
        List<CombatVideo.RoundData> roundDataList = JSONUtil.fromJsonArray(json, CombatVideo.RoundData.class);
        CombatVideo.RoundData roundData1 = roundDataList.get(0);
        RDCombat.RDPlayer p1 = roundData1.getRdCombat().getP1();
        RDCombat.RDPlayer p2 = roundData1.getRdCombat().getP2();
        if (p1.hasBuff() || p2.hasBuff() || (p1.getUid() < 0 && p2.getUid() < 0)) {
            return false;
        }
        RDCombat.RDPlayer user=p1;
        RDCombat.RDPlayer ai=p2;
        if (p2.getUid()>0){
            user=p2;
            ai=p1;
        }
        if (map.get(user.getName())!=null){
            if (map.get(user.getName())>id){
                attackCityStrategyService.deleteById(map.get(user.getName()));
                File nfile=new File("D:\\history\\"+city+"\\"+map.get(user.getName())+".json");
                nfile.delete();
            }else {
                return false;
            }
        }
        AttackCityStrategyEntity strategyEntity=new AttackCityStrategyEntity();
        strategyEntity.setId(id);
        strategyEntity.setCity(city);
        strategyEntity.setCityId(cityId);
        strategyEntity.setNightmare(0);
        strategyEntity.setUid(user.getUid());
        strategyEntity.setLv(user.getLv());
        strategyEntity.setUseWeapons(getUseWeapons(roundDataList));
        strategyEntity.setNickname(user.getName());
        strategyEntity.setHead(user.getImgId());
        strategyEntity.setIcon(user.getIconId());
        String[] cards = user.getCards().split("N");
        int specialCard=0;
        for (String cardStr:cards){
            String[] ps = cardStr.split("P");
            if (ps.length>10){
                if (Integer.parseInt(ps[10])==1){
                    specialCard++;
                }
            }
        }
        strategyEntity.setCards(cards.length);
        strategyEntity.setSpecialCards(specialCard);
        strategyEntity.setAiLv(ai.getLv());
        strategyEntity.setAiHead(ai.getImgId());
        strategyEntity.setAiNickname(ai.getName());
        strategyEntity.setRound(roundDataList.size());
        strategyEntity.setResultType(getResultType(roundDataList));
//        200824026676011109/1000000000000
        String ids=strategyEntity.getId().toString();
        int parseInt = Integer.parseInt(ids.substring(0, 6))+20000000;
        strategyEntity.setRecordedDate(parseInt);
        strategyEntity.setRecordedTime(DateUtil.fromDateInt(parseInt));
        strategyEntity.setRecordedUrl("http://bbw-god.oss-cn-shenzhen.aliyuncs.com/game/attackCityStrategy/history/"+city+"/"+strategyEntity.getId()+".json");
        if (attackCityStrategyService.insert(strategyEntity)){
            map.put(user.getName(),id);
            return true;
        }
        return false;
    }

    public int getResultType(List<CombatVideo.RoundData> roundDataList ){
        int size = roundDataList.size();
        if (size>=30){
            return CombatResultEnum.ROUND_TIMEOUT.getVal();
        }
        RDCombat rdCombat = roundDataList.get(size-1).getRdCombat();
        if (rdCombat.getP1().getHp()==0 || rdCombat.getP2().getHp()==0){
            return CombatResultEnum.HP_EMPTY.getVal();
        }
        return CombatResultEnum.CARD_EMPTY.getVal();
    }

    public int getUseWeapons(List<CombatVideo.RoundData> roundDataList){
        RDCombat rdCombatOne = roundDataList.get(0).getRdCombat();
        int p1Num=countWeapons(rdCombatOne.getP1().getWeapons());
        int p2Num=countWeapons(rdCombatOne.getP2().getWeapons());
        if (p1Num<=0 && p2Num<=0){
            return 0;
        }
        RDCombat rdCombatEnd = roundDataList.get(roundDataList.size()-1).getRdCombat();
        int useNum1=p1Num-countWeapons(rdCombatEnd.getP1().getWeapons());
        int useNum2=p2Num-countWeapons(rdCombatEnd.getP2().getWeapons());
        return Math.max(useNum1,useNum2);
    }

    public int countWeapons(String weapons){
        int num=0;
        if (StrUtil.isNotBlank(weapons)){
            String[] items=weapons.split("N");
            for (String it:items){
                String[] hs = it.split("H");
                num+=Integer.parseInt(hs[1]);
            }
        }
        return num;
    }
}
