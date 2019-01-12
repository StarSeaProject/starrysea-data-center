package top.starrysea.reducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import top.starrysea.mapreduce.Reducer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class DateReducer extends Reducer<TreeMap<String, Integer>> {
    private TreeMap<String, Integer> chatCount;
    private ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected TreeMap<String, Integer> reduce() {
        String fileNameWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));
        threadPool.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        threadPool.setMaxPoolSize(10);
        threadPool.setQueueCapacity(25);
        threadPool.initialize();
        chatCount = new TreeMap<>();
        analyze(outputPath + "/" + fileNameWithoutExtension);
        return chatCount;
    }

    private void analyze(String fileDirectory) {
        ArrayList<File> files = new ArrayList<>();
        File rootDir = new File(fileDirectory);
        File[] years = rootDir.listFiles();
        for (File i : years
        ) {
            if (i.isDirectory()) {
                File[] months = i.listFiles();
                for (File j : months
                ) {
                    if (j.isDirectory()) {
                        files.add(j);
                    }
                }
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        files.forEach(f -> threadPool.execute(new ChatCount(f, countDownLatch)));
        try {
            countDownLatch.await();
            logger.info("对每月发言数的分析结束.");
            for (Map.Entry<String, Integer> entry : chatCount.entrySet()) {
                logger.info(entry.getKey() + " " + entry.getValue());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ChatCount implements Runnable {
        private File dirPath;
        private String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{2} .+([<(]).+([>)])";
        //用于判断单个群聊聊天记录开头(日期,昵称,QQ号或邮箱)的正则表达式
        private CountDownLatch countDownLatch;

        ChatCount(File dirPath, CountDownLatch countDownLatch) {
            this.dirPath = dirPath;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            String dir = dirPath.toString();
            String date = dir.substring(dir.length() - 7);
            date = date.replace("\\", "/");
            File[] items = dirPath.listFiles();
            AtomicInteger count = new AtomicInteger();
            ArrayList<File> itemsArrayList = new ArrayList<>();
            for (File f : items
            ) {
                if (f.isFile())
                    itemsArrayList.add(f);
            }
            itemsArrayList.forEach(f -> {
                try {
                    Files.lines(f.toPath()).forEach(s -> {
                        s = s.replace("\ufeff", "");
                        if (Pattern.matches(pattern, s))
                            count.getAndIncrement();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            chatCount.put(date, count.intValue());
            //logger.info(date + " " + count);
            countDownLatch.countDown();
        }
    }
}
