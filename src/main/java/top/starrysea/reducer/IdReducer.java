package top.starrysea.reducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.starrysea.dto.Count;
import top.starrysea.mapreduce.MapReduceContext;
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

public class IdReducer extends Reducer {
    private Map<String, Long> chatCount;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private CountRepository countRepository;

    @Override
    protected void reduce(MapReduceContext context) {
        chatCount = new ConcurrentHashMap<>();
        String fileNameWithoutExtension = getFileName().substring(0, getFileName().lastIndexOf('.'));
        analyze(getInputPath() + "/" + fileNameWithoutExtension + "/" + context.getOutputFileSubType());
    }

    private void analyze(String fileDirectory) {
        List<File> fileList = new ArrayList<>();
        File rootDir = new File(fileDirectory);
        File[] files = rootDir.listFiles();
        for (File i : files) {
            if (i.isFile())
                fileList.add(i);
        }
        CountDownLatch countDownLatch = new CountDownLatch(fileList.size());
        fileList.forEach(f -> managerThreadPool.apply(new ChatCount(f, countDownLatch)));
        try {
            countDownLatch.await();
            logger.info("按id分析结束.");
            logger.info("共有{}位用户发言.", chatCount.size());
            Count count = new Count();
            count.setType("userId");
            count.setResult(chatCount);
            countRepository.findById("userId").defaultIfEmpty(count).subscribe(chatCountTemp -> {
                chatCountTemp.getResult().putAll(chatCount);
                chatCountTemp.setResult(chatCount);
                countRepository.save(chatCountTemp).subscribe();
                logger.info("id分析已存入数据库.");
            });
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    private class ChatCount implements Runnable {
        private File path;
        private String id;
        private CountDownLatch countDownLatch;

        ChatCount(File path, CountDownLatch countDownLatch) {
            this.path = path;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            id = path.getName();
            id = id.substring(0, id.lastIndexOf('.'));
            id = id.replace('.','^');
            //有的人用邮箱登录,MongoDB中不允许key带点,需要替换
            long count = 0;
            try {
                count = Files.lines(path.toPath()).map(s -> s.replace("\ufeff", ""))
                        .count();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            if (chatCount.containsKey(id)) {
                chatCount.put(id, chatCount.get(id) + count);
            } else {
                chatCount.put(id, count);
            }
            countDownLatch.countDown();
        }
    }

    public Reducer setCountRepository(CountRepository countRepository) {
        this.countRepository = countRepository;
        return this;
    }

}
