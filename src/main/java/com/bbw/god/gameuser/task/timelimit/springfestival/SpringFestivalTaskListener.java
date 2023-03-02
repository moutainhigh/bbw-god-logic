package com.bbw.god.gameuser.task.timelimit.springfestival;

import com.bbw.god.game.config.special.SpecialEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 村庄疑云监听
 *
 * @author fzj
 * @date 2022/1/5 11:44
 */
@Component
public class SpringFestivalTaskListener {
    @Autowired
    GameUserService gameUserService;
    @Autowired
    UserSpringFestivalTaskService userSpringFestivalTaskService;

    /** 活动需要特产 */
    private static final Map<Integer, Integer> ACTIVITY_SPECIALTY = new HashMap<Integer, Integer>() {
        private static final long serialVersionUID = -8516266763323893175L;

        {
        put(SpecialEnum.SL.getValue(), 140004);
        put(SpecialEnum.GD.getValue(), 140009);
//        put(SpecialEnum.XJ.getValue(), 140013);
        put(SpecialEnum.XR.getValue(), 140019);
        put(SpecialEnum.BC.getValue(), 140028);
    }};

    /** 活动需要收集产品 */
    private final static List<Integer> ACTIVITY_TREASURES = Arrays.asList(
            TreasureEnum.CHARCOAL.getValue(),
            TreasureEnum.SULFUR.getValue(),
            TreasureEnum.SALTPETER.getValue());

//    @Async
//    @EventListener
//    @Order(1000)
//    public void addSpecialty(SpecialAddEvent event) {
//        EPSpecialAdd ep = event.getEP();
//        long uid = ep.getGuId();
//        List<EVSpecialAdd> specialAdds = ep.getAddSpecials().stream().filter(s -> ACTIVITY_SPECIALTY.get(s.getSpecialId()) != null).collect(Collectors.toList());
//        if (specialAdds.isEmpty()) {
//            return;
//        }
//        for (EVSpecialAdd special : specialAdds) {
//            int taskId = ACTIVITY_SPECIALTY.get(special.getSpecialId());
//            achieveTask(uid, Collections.singletonList(taskId), 1);
//        }
//    }
//
//    @Async
//    @EventListener
//    @Order(1000)
//    public void payTreasure(TreasureDeductEvent event) {
//        EPTreasureDeduct ep = event.getEP();
//        EVTreasure deductTreasure = ep.getDeductTreasure();
//        if (ep.getWay() != WayEnum.CUNZ_YIYUN) {
//            return;
//        }
//        if (!ACTIVITY_TREASURES.contains(deductTreasure.getId())){
//            return;
//        }
//        long uid = ep.getGuId();
//        int taskId = 140020;
//        achieveTask(uid, Collections.singletonList(taskId), deductTreasure.getNum());
//    }
//
//    @Async
//    @EventListener
//    @Order(1000)
//    public void deductTreasure(TreasureDeductEvent event) {
//        EPTreasureDeduct ep = event.getEP();
//        EVTreasure deductTreasure = ep.getDeductTreasure();
//        if (ep.getWay() != WayEnum.CUNZ_YIYUN) {
//            return;
//        }
//        if (deductTreasure.getId() != TreasureEnum.LING_SHI.getValue()) {
//            return;
//        }
//        long uid = ep.getGuId();
//        int taskId = 140029;
//        achieveTask(uid, Collections.singletonList(taskId), deductTreasure.getNum());
//    }
//
//
//    private void achieveTask(long uid, List<Integer> taskIds, long addedNum) {
//        List<UserTimeLimitTask> uts = userSpringFestivalTaskService.getTasks(uid);
//        uts = uts.stream().filter(tmp -> taskIds.contains(tmp.getBaseId()) &&
//                tmp.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
//        if (ListUtil.isEmpty(uts)) {
//            return;
//        }
//        for (UserTimeLimitTask ut : uts) {
//            ut.addValue(addedNum);
//            if (ut.getStatus() != TaskStatusEnum.ACCOMPLISHED.getValue()) {
//                continue;
//            }
//            TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(uid, TaskGroupEnum.SPRING_FESTIVAL_TASK, ut.getBaseId());
//        }
//        gameUserService.updateItems(uts);
//    }
}
