package top.starrysea.mapper;

import top.starrysea.bo.SingleMessage;
import top.starrysea.mapreduce.MapReduceContext;
import top.starrysea.mapreduce.Mapper;

public class DateMapper extends Mapper {

	@Override
	protected MapReduceContext map(SingleMessage singleMessage, MapReduceContext context) {
		return context.write(singleMessage.getYear() + "-" + singleMessage.getMonth() + "-" + singleMessage.getDay(),
				singleMessage);
	}

	@Override
	protected String outputFileSubType() {
		return "byDate";
	}

}
