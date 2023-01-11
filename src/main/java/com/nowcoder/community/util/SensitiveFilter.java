package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Real
 * @date : 2021/11/27 16:19
 * @description : 敏感词过滤器
 */
@Component
public class SensitiveFilter {

    public static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    public static final Character REPLACEMENT = '*';

    private final TrieNode rootNode = new TrieNode();

    /**
     * 初始化，读取敏感词
     */
    @PostConstruct
    public void init() {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            String keyword;
            while ((keyword = bufferedReader.readLine()) != null) {
                this.addKeyWord(keyword);
            }
        } catch (IOException exception) {
            logger.error("敏感词文件读取失败：" + exception.getMessage());
        }
    }

    /**
     * 将敏感词添加到前缀树中
     *
     * @param keyword 待添加的敏感词
     */
    private void addKeyWord(String keyword) {
        TrieNode tempNode = rootNode;
        // 需要构建树形结构
        char[] chars = keyword.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            TrieNode childrenNode = tempNode.getChildrenNode(chars[i]);
            if (childrenNode == null) {
                // 初始化子节点
                childrenNode = new TrieNode();
                tempNode.setChildrenNode(chars[i], childrenNode);
            }
            // 将临时节点下移至子节点位置
            tempNode = childrenNode;
            // 判断是否为末尾节点
            if (i == chars.length - 1) {
                childrenNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤文本，输出过滤之后的字符串
     *
     * @param text 待过滤的文本
     * @return {@link String}
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 设定三个指针，分别指向前缀树、起始匹配字符、末尾匹配字符
        TrieNode tempNode = rootNode;
        int begin = 0, end = 0;
        StringBuilder result = new StringBuilder();
        while (begin < text.length()) {
            char c = text.charAt(end);
            // 跳过符号
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    begin++;
                }
                end++;
                continue;
            }
            // 检查下级节点
            tempNode = tempNode.getChildrenNode(c);
            if (tempNode == null) {
                // 以 begin 开头的字符不是敏感词
                result.append(text.charAt(begin));
                // 进入下一个位置
                end = ++begin;
                // 重新指向根节点
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                // 发现敏感词，将 begin ~ end 之间的字符替换为常量
                result.append(StringUtils.repeat(REPLACEMENT, end - begin + 1));
                // 进入下一个节点
                begin = ++end;
                // 重新指向根节点
                tempNode = rootNode;
            } else {
                // 检查下一个字符
                if (end < text.length() - 1) {
                    end++;
                } else {
                    end = begin;
                }
            }
        }

        // 将最后的字符计入
        result.append(text.substring(begin));
        return result.toString();
    }

    /**
     * 判断是否为符号
     *
     * @param character 字符判断
     * @return 输出是否需要跳过
     */
    private boolean isSymbol(Character character) {
        // 0x2E80 ~ 0x9FFF 是东亚文字
        return !CharUtils.isAsciiAlphanumeric(character) && (character < 0x2E80 || character > 0x9FFF);
    }

    /**
     * 设定一个 前缀树节点类，用来存放每个节点
     */
    private static class TrieNode {

        // 是否为末尾节点
        private boolean isKeywordEnd = false;

        // 节点的孩子节点
        private final Map<Character, TrieNode> childrenNode = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public TrieNode getChildrenNode(Character c) {
            return childrenNode.get(c);
        }

        public void setChildrenNode(Character c, TrieNode node) {
            childrenNode.put(c, node);
        }

    }

}
