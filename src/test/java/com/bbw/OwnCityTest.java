package com.bbw;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.god.db.entity.AttackCityStrategyEntity;
import com.bbw.god.db.entity.InsCityOwnDetailEntity;
import com.bbw.god.db.service.AttackCityStrategyService;
import com.bbw.god.db.service.InsCityOwnDetailService;
import com.bbw.god.game.config.city.CityTool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author：lwb
 * @date: 2020/11/30 16:21
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LogicServerApplication.class }) // 指定启动类
public class OwnCityTest {
    @Autowired
    private InsCityOwnDetailService insCityOwnDetailService;
    @Autowired
    private AttackCityStrategyService attackCityStrategyService;

    @Test
    public void test(){
        EntityWrapper<AttackCityStrategyEntity> ew=new EntityWrapper<>();
        ew.isNull("seq");
        List<AttackCityStrategyEntity> entities = attackCityStrategyService.selectList(ew);
        for (AttackCityStrategyEntity entity:entities){
            EntityWrapper<InsCityOwnDetailEntity> entityWrapper=new EntityWrapper<>();
            entityWrapper.eq("city_name",entity.getCity()).eq("uid",entity.getUid());
            InsCityOwnDetailEntity ownDetailEntity = insCityOwnDetailService.selectOne(entityWrapper);
            if (ownDetailEntity!=null){
                entity.setSeq(ownDetailEntity.getCityLvNum());
            }else {
                continue;
            }
        }
        attackCityStrategyService.updateBatchById(entities,entities.size());

    }

    @Test
    public void test2(){
        EntityWrapper<AttackCityStrategyEntity> ew=new EntityWrapper<>();
        ew.isNull("recorded_url");
        List<AttackCityStrategyEntity> entities = attackCityStrategyService.selectList(ew);
        for (AttackCityStrategyEntity entity:entities){
            String url="http://bbw-god.oss-cn-shenzhen.aliyuncs.com/game/attackCityStrategy/"+entity.getGid()+"/"+entity.getCity()+"/"+entity.getRecordedDate()+"/"+entity.getId()+".json";
            entity.setRecordedUrl(url);
        }
        attackCityStrategyService.updateBatchById(entities,entities.size());
    }

    @Test
    public void test3(){
        List<Integer> idByLevel = CityTool.getAllCityIdByLevel(4);
        System.err.println(idByLevel.size());
    }


}
