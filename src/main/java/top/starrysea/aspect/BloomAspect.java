package top.starrysea.aspect;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
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
	private static final String UTF8 = "UTF-8";

	@PostConstruct
	private void initBloomFilter() {
		List<String> filterWords;
		try {
			filterWords = Files.readAllLines(Paths.get(bloomFile), Charset.forName(UTF8));
			bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName(UTF8)), filterWords.size());
			filterWords.stream().forEach(bloomFilter::put);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName(UTF8)), 0);
		}
	}

	@Around("execution(* top.starrysea.controller.*.*(..))")
	public Object testWord(ProceedingJoinPoint pjp) throws Throwable {
		Object[] args = pjp.getArgs();
		for (Object arg : args) {
			if (arg instanceof String && bloomFilter.mightContain((String) arg)) {
				throw new IllegalArgumentException(arg + "被布隆过滤器拦截");
			}
		}
		return pjp.proceed();
	}

}
