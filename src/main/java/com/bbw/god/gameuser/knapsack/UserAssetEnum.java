package com.bbw.god.gameuser.knapsack;

import com.bbw.common.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lwb
 * @date 2020/4/3 10:56
 */
@Getter
@AllArgsConstructor
public enum UserAssetEnum {
    TREASURE(1,"20,10","法宝"),
    SPECIAL_LOCAL_PRODUCT(2,"","特产"),
    LINGSHI(3,"40,41","灵石"),
    SCROLL(4,"55","卷宗"),
    OTHER(5,"50","通用"),
    FAST_MAP_TREASURE(10,"10","地图快捷法宝");
    private int type;
    private String items;
    private String description;

    public static UserAssetEnum fromType(int type){
        for (UserAssetEnum assetEnum:values()){
            if (assetEnum.getType()==type)
                return assetEnum;
        }
        return OTHER;
    }
    public List<Integer> getTreasureTypes(){
        if (StrUtil.isBlank(items)){
            return new ArrayList<>();
        }
        String[] strs=items.split(",");
        List<Integer> types=new ArrayList<>();
        for (String s:strs){
            if (StrUtil.isBlank(s)){
                continue;
            }
            types.add(Integer.parseInt(s));
        }
        return types;
    }
}
