package top.starrysea.reducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import top.starrysea.dto.Count;
import top.starrysea.mapreduce.Reducer;
import top.starrysea.repository.CountRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class YearReducer extends Reducer {
    private Map<String, Long> chatCount;
    private ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private CountRepository countRepository;

    @Override
    protected void reduce() {
        chatCount = new ConcurrentHashMap<>();
        String fileNameWithoutExtension = getFileName().substring(0, getFileName().lastIndexOf('.'));
        analyze(inputPath + "/" + fileNameWithoutExtension);
    }

    private void analyze(String fileDirectory) {
        List<File> files = new ArrayList<>();
        File rootDir = new File(fileDirectory);
        File[] years = rootDir.listFiles();
        for (File i : years) {
            if (i.isDirectory()) {
                files.add(i);
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(files.size());
        files.forEach(f -> managerThreadPool.apply(new ChatCount(f, countDownLatch)));
        try {
            countDownLatch.await();
            logger.info("对每年发言数的分析结束.");
            logger.info("共有{}年.", chatCount.size());
            Count count = new Count();
            count.setType("year");
            count.setResult(chatCount);
            countRepository.findById("year").defaultIfEmpty(count).subscribe(chatCountTemp -> {
                chatCountTemp.getResult().putAll(chatCount);
                chatCountTemp.setResult(chatCount);
                countRepository.save(chatCountTemp).subscribe();
                logger.info("每年分析已存入数据库.");
            });
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();

    }
}

    private class ChatCount implements Runnable {
        private File dirPath;
        private String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{2} .+([<(]).+([>)])";
        private CountDownLatch countDownLatch;

        ChatCount(File dirPath, CountDownLatch countDownLatch) {
            this.dirPath = dirPath;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            String dir = dirPath.toString();
            String date = dir.substring(dir.length() - 4);
            File[] months = dirPath.listFiles();
            List<File> itemsArrayList = new ArrayList<>();
            for (File i : months) {
                if (i.isDirectory()) {
                    File[] days = i.listFiles();
                    for (File j : days) {
                        if (j.isFile()) {
                            itemsArrayList.add(j);
                        }
                    }
                }
            }
            long count = itemsArrayList.stream().mapToLong(f -> {
                try {
                    return Files.lines(f.toPath()).map(s -> s.replace("\ufeff", ""))
                            .filter(s -> Pattern.matches(pattern, s)).count();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    return 0;
                }
            }).sum();
            chatCount.put(date, count);
            countDownLatch.countDown();
        }
    }

    public Reducer setCountRepository(CountRepository countRepository) {
        this.countRepository = countRepository;
        return this;
    }
}
