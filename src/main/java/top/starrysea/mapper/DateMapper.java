package top.starrysea.mapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.starrysea.mapreduce.Mapper;

public class DateMapper extends Mapper {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private String fileNameWithoutExtension;
	private StringBuilder item;
	private String dateNow = "";

	@Override
	protected void map(WatchEvent<?> event) {
		split(event.context().toString());
	}

	private void split(String fileName) {
		this.fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
		item = new StringBuilder();
		File file = new File(inputPath, fileName);
		try (Stream<String> stream = Files.lines(file.toPath())) {
			stream.forEach(s -> execStr(s.replace("\ufeff", "")));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		execStr();
		// 将最后的聊天记录送出
		logger.info("分割好的文件已写入至{}/{}/", outputPath, fileNameWithoutExtension);
	}

	private void execStr(String str) {
		String strToSend;
		String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{2} .+([<(]).+([>)])";
		// 用于判断单个群聊聊天记录开头(日期,昵称,QQ号或邮箱)的正则表达式
		if (str == null)
			return;
		if (Pattern.matches(pattern, str)) {
			if (dateNow.equals(str.substring(0, 10))) {
				item.append(str);
				item.append("\n");
			} else {
				dateNow = str.substring(0, 10);
				// 当日期与前一天不一样时才写入
				strToSend = item.toString();
				saveStr(strToSend);
				item = new StringBuilder();
				item.append(str);
				item.append("\n");
			}
		} else {
			item.append(str);
			item.append("\n");
		}
	}

	private void execStr() {
		// 将最后一天的聊天记录送出
		String strToSend;
		strToSend = item.toString();
		saveStr(strToSend);
	}

	private void saveStr(String str) {
		File fileToWrite;
		File directory;
		if (str.equals(""))
			return;
		String year = str.substring(0, 4);
		String month = str.substring(5, 7);
		String date = str.substring(0, 10);
		String strDirectory = outputPath + "/" + fileNameWithoutExtension + "/" + year + "/" + month;
		String strFile = strDirectory + "/" + date + ".txt";
		directory = new File(strDirectory);
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
		try {
			Files.write(Paths.get(strFile), str.getBytes());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	protected void mapReduceFinish(List<Future<?>> futures) {
		// map、reduce结束后的回调,这里还没写
	}

}
