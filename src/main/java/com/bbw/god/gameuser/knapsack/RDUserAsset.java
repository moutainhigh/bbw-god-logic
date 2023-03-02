package com.bbw.god.gameuser.knapsack;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lwb
 * @date 2020/4/3 10:40
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDUserAsset extends RDSuccess implements Serializable {
    private static final long serialVersionUID = -1387744709490926443L;
    private List<UserAsset> userAssets=null;//用户资源

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserAsset implements Serializable{
        private int id;
        private int num;
        private Integer type=null;
        private Integer status=null;

        public static UserAsset instance(int id){
            UserAsset userAsset=new UserAsset();
            userAsset.setId(id);
            userAsset.setNum(0);
            return userAsset;
        }
        public static UserAsset instance(int id,int num){
            UserAsset userAsset=instance(id);
            userAsset.setNum(num);
            return userAsset;
        }

        public static UserAsset instance(int id,int num,int type){
            UserAsset userAsset=instance(id, num);
            userAsset.setType(type);
            return userAsset;
        }
    }
}
