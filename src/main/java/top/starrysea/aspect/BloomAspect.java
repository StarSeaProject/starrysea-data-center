package top.starrysea.aspect;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

@Component
@Aspect
public class BloomAspect {

	private BloomFilter<String> bloomFilter;
	@Value("${starrysea.bloomfile}")
	private String bloomFile;
	private final Log logger = LogFactory.getLog(this.getClass());

	@PostConstruct
	private void initBloomFilter() {
		List<String> filterWords;
		try {
			filterWords = Files.readAllLines(Paths.get(bloomFile), Charset.forName("UTF-8"));
			bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), filterWords.size());
			filterWords.stream().forEach(bloomFilter::put);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), 0);
		}
	}

}
