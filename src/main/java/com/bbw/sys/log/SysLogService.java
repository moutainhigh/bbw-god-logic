package com.bbw.sys.log;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.db.PageUtils;

public interface SysLogService extends IService<SysLogEntity> {

	PageUtils queryPage(Map<String, Object> params);

}
