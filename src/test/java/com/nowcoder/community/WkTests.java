package com.nowcoder.community;

import java.io.IOException;

/**
 * @author : Real
 * @date : 2021/12/20 17:13
 * @description : 测试 Wk 工具的使用
 */
public class WkTests {

    public static void main(String[] args) throws IOException {
        String cmd = "D:/Java/wkhtmltopdf/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com D:/Java/IdeaProjects/community/log/wk/image/3.jpg";
        Runtime.getRuntime().exec(cmd);
    }

}
