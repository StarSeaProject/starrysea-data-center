package top.starrysea.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class SingleMessage {

    private String head;//聊天记录头
    private String body;//发言部分
    private String id;//邮箱或QQ号
    private String nickname;//昵称
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private String second;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void setHead(String head) {
        this.head = head;
        analyze();
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public String getHead() {
        return head;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDate() {
        return head.substring(0, 10);//这是为了输出文件方便直接用的,不是分析出的日期
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public String getSecond() {
        return second;
    }

    private void analyze() {
        String patternQQ = "\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{2} .+([(]).+([)])\\n";
        String patternMail = "\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{2} .+([<]).+([>])\\n";
        //区分不同的账号类型，有QQ号还有邮箱
        String time = head.substring(0, 19).trim();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        try {//获取时间信息
            Date exactDate = simpleDateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(exactDate);
            year = String.valueOf(calendar.get(Calendar.YEAR));
            month = String.valueOf(calendar.get(Calendar.MONTH) + 1);//1月的数值是0,+1得到实际月份
            day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            minute = String.valueOf(calendar.get(Calendar.MINUTE));
            second = String.valueOf(calendar.get(Calendar.SECOND));
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        if (Pattern.matches(patternQQ, head)) {//获取昵称和QQ号
            id = head.substring(head.lastIndexOf('(') + 1, head.lastIndexOf(')'));
            nickname = head.substring(head.indexOf(' ', 11) + 1, head.lastIndexOf('(')).trim();
        } else if (Pattern.matches(patternMail, head)) {
            id = head.substring(head.lastIndexOf('<') + 1, head.lastIndexOf('>'));
            nickname = head.substring(head.indexOf(' ', 11) + 1, head.lastIndexOf('<')).trim();
        }
    }
}
