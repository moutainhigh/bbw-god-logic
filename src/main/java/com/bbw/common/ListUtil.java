package com.bbw.common;

import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtil {
    /**
     * 根据指定的属性去重
     *
     * @param keyExtractor
     * @return
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * 判断两个数字集合的元素是否一样
     *
     * @param nums1
     * @param nums2
     * @return
     */
    public static <T extends Number> boolean isSameList(List<T> nums1, List<T> nums2) {
        boolean isSame = true;
        for (int i = 0; i < nums1.size(); i++) {
            if (!nums1.get(i).equals(nums2.get(i))) {
                isSame = false;
            }
        }
        return isSame;
    }

    /**
     * 是否为空集合
     *
     * @param list
     * @return
     */
    public static boolean isNotEmpty(List<?> list) {
        return list != null && list.size() > 0;
    }

    /**
     * 是否为空集合
     *
     * @param list
     * @return
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 数字集合求和
     *
     * @param values
     * @return
     */
    public static int sumInt(List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 将字符串转换为整型集合
     *
     * @param srcInts num1,num2,num3
     * @return
     */
    @NonNull
    public static List<Integer> parseStrToInts(final String srcInts) {
        if (StrUtil.isEmpty(srcInts)) {
            return new ArrayList<>();
        }
        return parseStrToInts(srcInts, ",");
    }

    public static List<Integer> parseStrToInts(final String srcInts, String split) {
        List<Integer> result = new ArrayList<>();
        if (StrUtil.isEmpty(srcInts)) {
            return result;
        }
        String[] strs = srcInts.split(split);
        for (String str : strs) {
            if (StrUtil.isEmpty(str)) {
                continue;
            }
            result.add(Integer.valueOf(str));
        }
        return result;
    }

    /**
     * 建字符串转换为字符串集合
     *
     * @param src
     * @return
     */
    public static List<String> parseStrToStrs(String src) {
        return Stream.of(src.split(",")).collect(Collectors.toList());
    }
    /**
     * 将字符串转换为String集合
     *
     * @param srcStrings
     * @param split
     * @return
     */
    public static List<String> parseStrToStrs(String srcStrings, String split) {
        if (StrUtil.isEmpty(srcStrings)) {
            return new ArrayList<>();
        }
        return Stream.of(srcStrings.split(split)).collect(Collectors.toList());
    }

    /**
     * 将字符串转换为长整型集合
     *
     * @param srcLongs num1,num2,num3
     * @return
     */
    public static List<Long> parseStrToLongs(String srcLongs) {
        return parseStrToLongs(srcLongs, ",");
    }

    /**
     * 将字符串转换为长整型集合
     *
     * @param srcLongs
     * @param split
     * @return
     */
    public static List<Long> parseStrToLongs(String srcLongs, String split) {
        if (StrUtil.isEmpty(srcLongs)) {
            return new ArrayList<>();
        }
        return Stream.of(srcLongs.split(split)).map(srcLong -> Long.valueOf(srcLong)).collect(Collectors.toList());
    }

    /**
     * 将集合从小到大排序
     *
     * @param strList 集合
     * @param order   -1 倒序 1 正序
     */
    public static void orderList(List<String> strList, int order) {

        strList.stream().sorted((str1, str2) -> {
            if (order == 1) {
                return str1.compareTo(str2);
            } else {
                return str2.compareTo(str1);
            }
        });
    }

    /**
     * 将集合分层若干份，每份最多maxNumber个
     *
     * @param list
     * @param maxNumber
     * @return
     */
    public static List<List> partition(List list, int maxNumber) {
        int limit = (list.size() + maxNumber - 1) / maxNumber;
        List<List> subList = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(limit)
                .forEach(i -> subList.add((List) list.stream().skip(i * maxNumber).limit(maxNumber)
                        .collect(Collectors.toList())));
        return subList;
    }

    /**
     * 列表数据分页
     *
     * @param entityList：列表对象
     * @param pageNum:当前页码
     * @param perPageCount:每页显示条数
     * @return
     */
    public static <T> List<T> subListForPage(List<T> entityList, int pageNum, int perPageCount) {
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (perPageCount < 1) {
            perPageCount = 10;
        }
        List<T> list = new ArrayList<T>(entityList);
        int reqMaxIndex = (pageNum) * perPageCount;
        int reqMinIndex = reqMaxIndex - perPageCount;
        int toIndex = reqMaxIndex > list.size() ? list.size() : reqMaxIndex;
        toIndex = toIndex > 0 ? toIndex : 0;
        int fromIndex = reqMinIndex > list.size() - 1 ? list.size() - 1 : reqMinIndex;
        fromIndex = fromIndex > 0 ? fromIndex : 0;
        return list.subList(fromIndex, toIndex);
    }

    public static <T extends Serializable> List<T> copyList(List<T> list, Class<T> clazz) {
        return CloneUtil.cloneList(list);
    }

    /**
     * 获取集合中元素所在的下标
     *
     * @param list
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Integer getIndex(List<T> list, T clazz) {
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            if (t.equals(clazz)) {
                return i;
            }
        }
        return null;
    }

}
