package com.bbw.god;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年5月14日 下午4:44:52
 */
public class Const {
	//游客账号结束关键词
	public static final String GUESS_ACCOUNT_KEY = "@bbw";

	/**
	 * 渠道参数
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2018年8月23日 上午1:07:17
	 */
	public static class BasePlat {
		/**
		 * 审核服id
		 */
		public static final int ID_SHEN_HE = 0;
	}

	/**
	 * 礼包常数
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2018年5月16日 下午2:17:27
	 */
	public static class Packs {
		/**
		 *  10:通用码
		 *  pack.type的字典值。
		 */
		public static final int TYPE_COMMON = 10;
		/**
		 *  20:微信绑定礼包
		 *  pack.type的字典值。
		 */
		public static final int TYPE_WECHAT_BINDED = 20;
		/**
		 *  30:微信每周礼包
		 *  pack.type的字典值。
		 */
		public static final int TYPE_WECHAT_WEEKLY = 30;
	}

	/**
	 * 服务器常数
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2018年5月16日 下午2:17:43
	 */
	public static class Server {
		/**
		 * 0:IOS审核过程中
		 * server.plat的字典值。用于标识服务器分组。
		 */
		public static final int PLAT_IOS_CHECKING = 0;

		/**
		 * 10:竹风软件自营的ios服务器组
		 * server.plat的字典值。用于标识服务器分组。
		 */
		public static final int PLAT_IOS_ZF = 10;

		/**
		 * 10000:IOS口袋ATM。
		 * server.plat的字典值。用于标识服务器分组。
		 */
		public static final int PLAT_IOS_ATM = 10000;

		/**
		 * 20:安卓。
		 * server.plat的字典值。用于标识服务器分组。
		 */
		public static final int PLAT_ANDROID_CHANNELS = 20;
		/**
		 * 16:安卓买量包。
		 * server.plat的字典值。用于标识服务器分组。
		 */
		public static final int PLAT_ANDROID_AD = 16;
		/**
		 * 状态可用的服务器
		 */
		public static final int STATUS_AVAILABLE = 20;

	}

	/**
	 * 系统参数及服务器额外参数
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2018年5月30日 下午8:28:57
	 */
	public static class SystemParam {
		/**
		 * 10:苹果渠道。
		 * system_param.plat的字典值。
		 */
		public static final int PLAT_APPLE = Server.PLAT_IOS_ZF;

	}

	/**
	 * 账号绑定常数
	 * @author lsj@bamboowind.cn
	 * @version 1.0.0
	 * @date 2018年5月17日 下午10:36:42
	 */

	public static class AccountBind {
		/**
		 * 1:微信
		 * goduc_account_bind.type字典值
		 */
		public static final int BIND_TYPE_WECHAT = 1;
		/**
		 * 2:手机号
		 * goduc_account_bind.type字典值
		 */
		public static final int BIND_TYPE_MOBILEPHONE = 2;
		/**
		 * 4:支付宝
		 * goduc_account_bind.type字典值
		 */
		public static final int BIND_TYPE_ALIPAY = 4;
	}
}
