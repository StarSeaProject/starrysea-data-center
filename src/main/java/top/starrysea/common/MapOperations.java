package top.starrysea.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapOperations {//对Map进行操作
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(final Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted((Map.Entry.<K, V>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }//按照value降序排列

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueAsc(final Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted((Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }//按照value升序排列
}
