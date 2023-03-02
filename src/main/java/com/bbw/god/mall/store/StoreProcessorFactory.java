package com.bbw.god.mall.store;

import com.bbw.god.mall.processor.AbstractMallProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * @author lwb
 */
@Service
public class StoreProcessorFactory {
	@Autowired
	@Lazy
	private List<AbstractStoreProcessor> storeProcessors;

	/**
	 * 根据商品类型获取物品服务实现对象
	 * 
	 * @param mallType
	 * @return
	 */
	public AbstractStoreProcessor getStoreProcessor(int mallType) {
		return storeProcessors.stream().filter(mp -> mp.isMatch(mallType)).findFirst().orElse(null);
	}
}
