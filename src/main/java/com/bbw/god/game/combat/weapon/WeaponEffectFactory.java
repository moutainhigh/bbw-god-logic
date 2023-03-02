package com.bbw.god.game.combat.weapon;

import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.weapon.service.IWeaponAfterEffect;
import com.bbw.god.game.combat.weapon.service.IWeaponInTimeEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 战斗法宝服务工厂
 * @author lwb
 * @version 1.0.0
 * @date 2020-11-24 13:50
 */
@Service
public class WeaponEffectFactory {
	@Lazy
	@Autowired
	private List<IWeaponInTimeEffect> weaponInTimeEffects;
	@Lazy
	@Autowired
	private List<IWeaponAfterEffect> weaponAfterEffects;

	/**
	 * 获取立即生效的法宝服务
	 * @param weaponId
	 * @return
	 */
	public IWeaponInTimeEffect matchInTimeEffectWeapon(int weaponId){
		for (IWeaponInTimeEffect weapon:weaponInTimeEffects){
			if (weapon.match(weaponId)){
				return weapon;
			}
		}
		throw CoderException.high("程序员没有编写法宝ID=" + weaponId + "的服务程序！");
	}

	/**
	 * 获取延后生效的法宝服务
	 * @param weaponId
	 * @return
	 */
	public IWeaponAfterEffect matchAfterEffectWeapon(int weaponId){
		for (IWeaponAfterEffect weapon:weaponAfterEffects){
			if (weapon.match(weaponId)){
				return weapon;
			}
		}
		throw CoderException.high("程序员没有编写法宝ID=" + weaponId + "的服务程序！");
	}

}
