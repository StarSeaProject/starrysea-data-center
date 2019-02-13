package top.starrysea.mapper;

import org.springframework.stereotype.Component;

import top.starrysea.bo.SingleMessage;
import top.starrysea.mapreduce.MapReduceContext;
import top.starrysea.mapreduce.Mapper;

@Component
public class YearidMapper extends Mapper {

	@Override
	protected MapReduceContext map(SingleMessage singleMessage, MapReduceContext context) {
		return context.write(singleMessage.getYear() + "-" + singleMessage.getId(),
				singleMessage);
	}

	@Override
	protected String outputFileSubType() {
		return "byDateAndId";
	}

}
