package com.bbw.god.road;

import com.bbw.god.gameuser.shake.ShakeRandomParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 路径服务
 *
 * @author suhq
 * @date 2020-10-14 11:59
 **/
@Service
public class RoadPathService {

    /**
     * 获取随机路径
     *
     * @param beginPos
     * @param dir
     * @param maxPathLength
     * @return
     */
    public List<PathRoad> getRandomPath(int beginPos, int dir, int maxPathLength, int minPathLength, ShakeRandomParam param) {
//        long begin = System.nanoTime();
        List<PathRoad> targetPath = RoadPathTool.I.getRandomPath(beginPos, dir, maxPathLength, minPathLength, param.getShakePropConfig());
//        long exceuteTime = System.nanoTime() - begin;
//        if (exceuteTime / 1000 > 300) {
//            System.out.println("获取真实路径执行时间(微妙)：" + exceuteTime / 1000);
//        }
        return targetPath;
    }

    /**
     * 获取指定长度的路径
     *
     * @param beginPos
     * @param dir
     * @param assignPathLength
     * @return
     */
    public List<PathRoad> getAssignPath(int beginPos, int dir, int assignPathLength) {
//        long begin = System.currentTimeMillis();
        List<PathRoad> targetPath = RoadPathTool.I.getTargetPath(beginPos, dir, assignPathLength);
//        System.out.println("获取真实路径执行时间(ms)：" + (System.currentTimeMillis() - begin));
        return targetPath;
    }

}
