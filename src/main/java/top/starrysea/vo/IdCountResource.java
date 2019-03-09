package top.starrysea.vo;

import top.starrysea.common.MapOperations;
import top.starrysea.dto.Count;
import top.starrysea.hateoas.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdCountResource extends Resource {

    private String type;
    private List<Map<String, Object>> resultList;

    private IdCountResource(Count search){
        this.type = search.getType();
        //排序后取前10个数据
        Map<String, Long> result = MapOperations.sortByValueDesc(search.getResult());
        Map<String, Long> top10Result = result.entrySet().stream().limit(10).collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
        //取完前10个又变乱了所以要再排一次
        this.resultList = Count.mapToList(MapOperations.sortByValueDesc(top10Result));
    }

    public static IdCountResource of(Count search) {
        return new IdCountResource(search);
    }

    public String getType() {
        return type;
    }

    public List<Map<String, Object>> getResultList() {
        return resultList;
    }

}
