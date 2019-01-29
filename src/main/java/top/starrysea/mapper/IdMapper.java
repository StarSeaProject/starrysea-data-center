package top.starrysea.mapper;

import top.starrysea.bo.SingleMessage;
import top.starrysea.mapreduce.MapReduceContext;
import top.starrysea.mapreduce.Mapper;

public class IdMapper extends Mapper {
	
	@Override
	protected MapReduceContext map(SingleMessage singleMessage, MapReduceContext context) {
		return context.write(singleMessage.getId(), singleMessage);
	}

	@Override
	protected String outputFileSubType() {
		return "byId";
	}
}
