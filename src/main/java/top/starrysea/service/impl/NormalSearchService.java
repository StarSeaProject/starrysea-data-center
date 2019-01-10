package top.starrysea.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Mono;
import top.starrysea.dto.Most;
import top.starrysea.repository.MostRepository;
import top.starrysea.service.ISearchService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.regex.Pattern;

@Service("normalSearchService")
public class NormalSearchService implements ISearchService {
    private static WatchService watchService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();

    @PostConstruct
    private void init() {
        threadPool.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        threadPool.setMaxPoolSize(10);
        threadPool.setQueueCapacity(25);
        threadPool.initialize();
        threadPool.execute(new SplitDialog());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                watchService.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }));
    }

    @Autowired
    private MostRepository mostRepository;

    @Override
    public Mono<Most> searchMostService(String keyword) {
        return mostRepository.findById(keyword);
    }

    @PreDestroy
    private void destroy() {
        try {
            watchService.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private class SplitDialog implements Runnable {
        private String fileName;
        private StringBuilder item;
        private String dateNow = "";

        @Override
        public void run() {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                Path path = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "chatHistory/raw/placeholder.txt").getParentFile().toPath();
                //placeholder.txt是为了方便寻找路径设的文件,下同
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                WatchKey key;
                while ((key = watchService.take()) != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        logger.info(LocalTime.now() + " " + "检测到文件变化: " + event.context().toString() + " " + event.kind().toString());
                        split(event.context().toString());
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

        void split(String fileName) {
            this.fileName = fileName;
            item = new StringBuilder();
            try {
                File file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "chatHistory/raw/" + fileName);
                Files.lines(file.toPath()).forEach(s -> {
                    s = s.replace("\ufeff", "");
                    //处理UTF-8 BOM
                    execStr(s);
                });
                execStr();
                //将最后的聊天记录送出
                logger.info("文件已写入至" + ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "chatHistory/result/placeholder.txt").getParentFile().getPath() + "\\" + fileName + "\\");
            } catch (FileSystemException e) {
                if (!e.getMessage().contains(fileName + ": 另一个程序正在使用此文件，进程无法访问。")) {
                    logger.error(e.getMessage(), e);
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        void execStr(String str) {
            String strToSend;
            String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{2} .+([<(]).+([>)])";
            //用于判断单个群聊聊天记录开头(日期,昵称,QQ号或邮箱)的正则表达式
            if (str == null)
                return;
            if (Pattern.matches(pattern, str)) {
                if (dateNow.equals(str.substring(0, 10))) {
                    item.append(str);
                    item.append("\n");
                } else {
                    dateNow = str.substring(0, 10);
                    //当日期与前一天不一样时才写入
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

        void execStr() {
            //将最后一天的聊天记录送出
            String strToSend;
            strToSend = item.toString();
            saveStr(strToSend);
        }

        void saveStr(String str) {
            File fileToWrite;
            File directory;
            if (str.equals(""))
                return;
            String year = str.substring(0, 4);
            String month = str.substring(5, 7);
            String date = str.substring(0, 10);
            try {
                String strRoot = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + "chatHistory/result/placeholder.txt").getParentFile().getPath();
                String strDirectory = strRoot + "/" + fileName + "/" + year + "/" + month;
                String strFile = strDirectory + "/" + date + ".txt";
                directory = new File(strDirectory);
                //按年份和月份创建目录
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                fileToWrite = new File(strFile);
                //在相应目录下建立文件
                if (!fileToWrite.exists()) {
                    try {
                        fileToWrite.createNewFile();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                try {
                    Files.write(Paths.get(strFile), str.getBytes());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            } catch (FileNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
