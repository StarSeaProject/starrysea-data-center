package top.starrysea.mapreduce;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.starrysea.dto.SingleMessage;

public class MapReduceContext {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Map<String, Object> contextData = new HashMap<>();
	private Map<String, List<SingleMessage>> mappedFile = new ConcurrentHashMap<>();
	private static final int WRITE_THRESHOLD = 1000;

	public MapReduceContext(String outputFileName, String outputFileSubType, String inputPath, String outputPath) {
		if (contextData.containsKey("inputPath") || contextData.containsKey("outputPath")) {
			throw new UnsupportedOperationException("不可以再初始化该类");
		}
		contextData.put("inputPath", inputPath);
		contextData.put("outputPath", outputPath);
		contextData.put("outputFileName", outputFileName.substring(0, outputFileName.lastIndexOf('.')));
		contextData.put("fileExtendsion",
				outputFileName.substring(outputFileName.lastIndexOf('.') + 1, outputFileName.length()));
		contextData.put("outputFileSubType", outputFileSubType);
	}

	public String getInputPath() {
		return (String) contextData.get("inputPath");
	}

	public String getOutputPath() {
		return (String) contextData.get("outputPath");
	}

	public String getOutputFileName() {
		return (String) contextData.get("outputFileName");
	}

	public String getOutputFileSubType() {
		return (String) contextData.get("outputFileSubType");
	}

	public String getFileExtendsion() {
		return (String) contextData.get("fileExtendsion");
	}

	public void setAttribute(String key, Object value) {
		contextData.put(key, value);
	}

	public Object getAttribute(String key) {
		return contextData.get(key);
	}

	public MapReduceContext write(String group, SingleMessage singleMessage) {
		List<SingleMessage> singleMessages = mappedFile.get(group);
		if (singleMessages == null) {
			singleMessages = new ArrayList<>();
		}
		singleMessages.add(singleMessage);
		if (singleMessages.size() >= WRITE_THRESHOLD) {
			save(group, singleMessages);
			singleMessages.clear();
		}
		mappedFile.put(group, singleMessages);
		return this;
	}

	private void save(String group, List<SingleMessage> messages) {
		File fileToWrite;
		if (messages == null || messages.isEmpty())
			return;
		String strDirectory = getOutputPath() + "/" + getOutputFileName() + "/" + getOutputFileSubType();
		String strFile = strDirectory + "/" + group + ".txt";
		File directory = new File(strDirectory);
		// 按年份和月份创建目录
		if (!directory.exists()) {
			directory.mkdirs();
		}
		fileToWrite = new File(strFile);
		// 在相应目录下建立文件
		if (!fileToWrite.exists()) {
			try {
				if (!fileToWrite.createNewFile()) {
					logger.info("文件夹已经存在");
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		StringBuilder text = new StringBuilder();
		messages.forEach(m -> {
			text.append(m.getHead());
			text.append(m.getBody());
		});
		saveText(text.toString(), strFile);
	}

	private void saveText(String text, String path) {
		try {
			Files.write(Paths.get(path), text.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void writeFlush() {
		for (Map.Entry<String, List<SingleMessage>> entry : mappedFile.entrySet()) {
			save(entry.getKey(), entry.getValue());
		}
		mappedFile.clear();
	}
}
