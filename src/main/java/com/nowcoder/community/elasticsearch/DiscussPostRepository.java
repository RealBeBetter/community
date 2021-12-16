package com.nowcoder.community.elasticsearch;

import com.nowcoder.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Real
 * @date : 2021/12/13 17:43
 * @description : 创建 DiscussPost 的接口供 ES 使用
 * 泛型声明为 数据类型 + 主键数据类型
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
