package com.bbw.god.rd;

import lombok.Data;

/**
 * 客户端返回对象成功标识,自定义对象需要继承，返回res=0
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-21 18:20
 */
@Data
public class RDSuccess {
	protected int res = ResCode.SUCCESS;
}
