package com.bbw.god.gameuser.treasure.xianrenbox;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lwb
 * @date 2020/8/12 16:19
 */
@Data
public class CfgXianRenBox implements CfgInterface {
    private List<BoxSetting> setting=new ArrayList<>();//特殊设定
    private List<BoxAward> awardsPool;//奖池

    @Override
    public Serializable getId() {
        return "唯一";
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Data
    public static class BoxAward implements Serializable{
        private Integer id;
        private String name;
        private List<Award> awards;
    }
    @Data
    public static class BoxSetting implements Serializable{
        private Integer id;
        private Integer minIndex;
        private Integer maxIndex;

        public boolean valid(int index){
            int rel=index+1;
            return minIndex<= rel && rel <=maxIndex;
        }
    }
}
