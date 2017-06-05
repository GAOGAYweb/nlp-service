import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static util.NPLUtil.keywords;
import static util.NPLUtil.sentimentAnalysis;

public class testUtil {

    public static void main(String[] args) {
        // 输入数据
        List<String> data = Arrays.asList("他是个傻逼", "美好的世界");
        try {
            // 输出情感分析结果
            System.out.println("sentiment: " + sentimentAnalysis(data));
            // 输出关键词，阈值（0-1）可以自己设定
            System.out.println("keywords:" + keywords(data, 0.1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}