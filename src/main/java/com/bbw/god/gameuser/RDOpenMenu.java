package com.bbw.god.gameuser;

import java.util.ArrayList;
import java.util.List;

import com.bbw.god.rd.RDSuccess;

import lombok.Data;

/** 
* @author 作者 ：lwb
* @version 创建时间：2020年4月9日 上午9:21:22 
* 类说明 
*/
@Data
public class RDOpenMenu extends RDSuccess{
    private List<Integer> menus=null;

    public void  addMenu(int id){
        if (menus==null){
            menus=new ArrayList<>();
        }
        if (!menus.contains(id)){
            menus.add(id);
        }
    }
}
