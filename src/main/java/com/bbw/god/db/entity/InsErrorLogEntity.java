package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.bbw.common.DateUtil;
import lombok.Data;

import java.io.Serializable;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年10月25日 上午10:07:22 
* 类说明 访问日志记录
*/
@Data
@TableName("ins_error_detail")
public class InsErrorLogEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	@TableId(type = IdType.INPUT)
	private Long id;
	private Long uid = 0l;
	private String ip;//访问Ip	
	private String method;//Controller对应请求接口
	private String params;//请求参数
	private String res;//结果
	private String protocol;// 协议
	private String type;//POST/GET类型
	private Long logTime = DateUtil.toDateTimeLong();// 访问时间
}
