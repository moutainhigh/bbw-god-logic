package com.bbw.common;

import java.util.HashMap;

/**
 * <pre>
 * 伪随机概率算法,概率对应表。
 * 以100为概率，HashMap<随机概率,对应的范围>。
 * 概率为1%算法示例描述：
 * 1. 根据1到lookupTable获取映射的值为0.0156[初始值],并初始化为[概率系数值]。
 * 2. 调用java随机一个[0,100]的double[随机值]。
 * 3. 如果[随机值] > [概率系数值]，则未命中，则 设置 [概率系数值] = [概率系数值] + [初始值]，供下次使用。每次未命中则累加。
 * 4. 如果[随机值] < [概率系数值] 则命中。重置 [概率系数值] = [初始值]
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-06 14:36
 */
public class PRDTable {
	//以[0,10000]为概率范围
	private static final int P_RANGE = 10000;
	private static HashMap<Integer, Double> lookupTable = new HashMap<>();//
	static {
		lookupTable.put(1, 1.5706986965616896E-6);
		lookupTable.put(2, 6.282351238851592E-6);
		lookupTable.put(3, 1.413434262489315E-5);
		lookupTable.put(4, 2.5126043326703468E-5);
		lookupTable.put(5, 3.9256825456341706E-5);
		lookupTable.put(6, 5.652606158576883E-5);
		lookupTable.put(7, 7.693312457810505E-5);
		lookupTable.put(8, 1.0047738754554047E-4);
		lookupTable.put(9, 1.2715822383515925E-4);
		lookupTable.put(10, 1.5697500702283835E-4);
		lookupTable.put(20, 6.274822658011775E-4);
		lookupTable.put(30, 0.0014108968106417057);
		lookupTable.put(40, 0.0025065958958685975);
		lookupTable.put(50, 0.003913958986484388);
		lookupTable.put(60, 0.00563236774716309);
		lookupTable.put(70, 0.007661206031413153);
		lookupTable.put(80, 0.009999859870817434);
		lookupTable.put(90, 0.012647717464343573);
		lookupTable.put(100, 0.015604169167720937);
		lookupTable.put(200, 0.06200876164358948);
		lookupTable.put(300, 0.1386177720390883);
		lookupTable.put(400, 0.2448555471647783);
		lookupTable.put(500, 0.3801658303553155);
		lookupTable.put(600, 0.5440108614899369);
		lookupTable.put(700, 0.73587052890401);
		lookupTable.put(800, 0.9552415696806041);
		lookupTable.put(900, 1.2016368150795191);
		lookupTable.put(1000, 1.474584478107266);
		lookupTable.put(1100, 1.7736274804569137);
		lookupTable.put(1200, 2.0983228162532135);
		lookupTable.put(1300, 2.448240950228553);
		lookupTable.put(1400, 2.8229652481287912);
		lookupTable.put(1500, 3.222091437308764);
		lookupTable.put(1600, 3.645227095623867);
		lookupTable.put(1700, 4.091991166860266);
		lookupTable.put(1800, 4.562013501080302);
		lookupTable.put(1900, 5.054934418517177);
		lookupTable.put(2000, 5.570404294978182);
		lookupTable.put(2100, 6.108083171449882);
		lookupTable.put(2200, 6.667640362150809);
		lookupTable.put(2300, 7.248754339844679);
		lookupTable.put(2400, 7.851112066400392);
		lookupTable.put(2500, 8.474409185231705);
		lookupTable.put(2600, 9.118346091312295);
		lookupTable.put(2700, 9.782638048546705);
		lookupTable.put(2800, 10.467022737491504);
		lookupTable.put(2900, 11.171175824210339);
		lookupTable.put(3000, 11.894919272540397);
		lookupTable.put(3100, 12.63793161208354);
		lookupTable.put(3200, 13.40008645349125);
		lookupTable.put(3300, 14.180519568675281);
		lookupTable.put(3400, 14.98100879493791);
		lookupTable.put(3500, 15.798309812574708);
		lookupTable.put(3600, 16.632877680643805);
		lookupTable.put(3700, 17.49092435951355);
		lookupTable.put(3800, 18.36246523722509);
		lookupTable.put(3900, 19.2485957970884);
		lookupTable.put(4000, 20.154741360775407);
		lookupTable.put(4100, 21.09200313959977);
		lookupTable.put(4200, 22.03645774003488);
		lookupTable.put(4300, 22.989867636265352);
		lookupTable.put(4400, 23.95401522844584);
		lookupTable.put(4500, 24.930699844016324);
		lookupTable.put(4600, 25.98723505886278);
		lookupTable.put(4700, 27.045293670119385);
		lookupTable.put(4800, 28.100763520154636);
		lookupTable.put(4900, 29.155226664271815);
		lookupTable.put(5000, 30.210302534874202);
		lookupTable.put(5100, 31.26766393399555);
		lookupTable.put(5200, 32.3290547144763);
		lookupTable.put(5300, 33.4119960942593);
		lookupTable.put(5400, 34.73699930849593);
		lookupTable.put(5500, 36.03978509331689);
		lookupTable.put(5600, 37.32168294719914);
		lookupTable.put(5700, 38.583961178195445);
		lookupTable.put(5800, 39.82783321856845);
		lookupTable.put(5900, 41.05446351769762);
		lookupTable.put(6000, 42.264973081037425);
		lookupTable.put(6100, 43.46044471809662);
		lookupTable.put(6200, 44.641928058933836);
		lookupTable.put(6300, 45.81044439647124);
		lookupTable.put(6400, 46.96699141100894);
		lookupTable.put(6500, 48.112547833722914);
		lookupTable.put(6600, 49.248078107744774);
		lookupTable.put(6700, 50.74626865671645);
		lookupTable.put(6800, 52.94117647058827);
		lookupTable.put(6900, 55.072463768115966);
		lookupTable.put(7000, 57.14285714285715);
		lookupTable.put(7100, 59.15492957746478);
		lookupTable.put(7200, 61.111111111111114);
		lookupTable.put(7300, 63.01369863013699);
		lookupTable.put(7400, 64.86486486486487);
		lookupTable.put(7500, 66.66666666666667);
		lookupTable.put(7600, 68.42105263157896);
		lookupTable.put(7700, 70.12987012987013);
		lookupTable.put(7800, 71.79487179487181);
		lookupTable.put(7900, 73.41772151898735);
		lookupTable.put(8000, 75.00000000000003);
		lookupTable.put(8100, 76.54320987654322);
		lookupTable.put(8200, 78.04878048780488);
		lookupTable.put(8300, 79.51807228915662);
		lookupTable.put(8400, 80.95238095238095);
		lookupTable.put(8500, 82.35294117647058);
		lookupTable.put(8600, 83.72093023255815);
		lookupTable.put(8700, 85.0574712643678);
		lookupTable.put(8800, 86.36363636363636);
		lookupTable.put(8900, 87.64044943820225);
		lookupTable.put(9000, 88.8888888888889);
		lookupTable.put(9100, 90.10989010989012);
		lookupTable.put(9200, 91.30434782608697);
		lookupTable.put(9300, 92.47311827956992);
		lookupTable.put(9400, 93.61702127659574);
		lookupTable.put(9500, 94.73684210526315);
		lookupTable.put(9600, 95.83333333333334);
		lookupTable.put(9700, 96.90721649484534);
		lookupTable.put(9800, 97.9591836734694);
		lookupTable.put(9900, 98.98989898989899);
		lookupTable.put(10000, 100.0);
	}

	/**
	 * 根据概率值获取伪随机初始系数
	 * @param p 值范围(0,100]
	 * @return
	 */
	public static Double getCFromP(Double p) {
		//换算称万
		Double realP = p * (P_RANGE / 100);
		Double result = lookupTable.get(realP.intValue());
		if (result == null) {//没有就需要算一个了
			double cp = cFromP(p / 100.0) * 100;
			lookupTable.put(realP.intValue(), cp);
			return cp;
		}
		return result;
	}

	private static double pFromC(double c)//不断试验当前C对应的实际概率，用1/当前的数学期望值 得到当前概率
	{
		double dCurP = 0.0;
		double dPreSuccessP = 0.0;
		double dPE = 0;//全概率
		double nMaxFail = Math.ceil(1.0 / c);//最多需要的次数
		for (int i = 1; i <= nMaxFail; ++i) {
			dCurP = Math.min(1.0, i * c) * (1 - dPreSuccessP);
			dPreSuccessP += dCurP;
			dPE += i * dCurP;
		}
		return 1.0 / dPE;
	}

	public static double cFromP(double p) {
		double dUp = p;
		double dLow = 0.0;
		double dMid = p;
		double dPLast = 1.0;
		while (true) {
			dMid = (dUp + dLow) / 2.0;
			double dPtested = pFromC(dMid);//使用二分法，不断试验当前C对应的实际概率
			if (Math.abs(dPtested - dPLast) <= 0.0)//前后两次计算结果相同，说明到了逼近极限，不与P比较是因为有误差，可能永远无法再逼近
				break;
			if (dPtested > p)
				dUp = dMid;
			else
				dLow = dMid;
			dPLast = dPtested;

		}
		return dMid;
	}
}
