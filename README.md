# 社区项目介绍

本项目是牛客网社区项目，实现了一个牛客网的论坛社区，基本功能包括登录注册、发帖评论、关注点赞等。

## 框架版本

- 整体框架：Spring Boot 2.4.10、Spring 5.3.9 、jdk 14.0.1
- 版本控制：Git 2.33.1
- 数据库：MySQL 8.0.23 、Redis 3.2.100
- 模板引擎：Thymeleaf
- 应用服务器：Apache Tomcat 9.0.52
- 消息队列：Kafka 2.12-2.8.1
- 分布式搜索：Elasticsearch 6.4.3

## Kafka 配置

打开 config 目录下的 zookeeper.properties 文件以及 server.properties 文件，分别修改其中的属性：

```properties
dataDir=D:/Java/IdeaProjects/community/log/zookeeper
log.dirs=D:/Java/IdeaProjects/community/log/kafka-logs
```

修改的目的是修改其日志以及数据文件的存放目录。

> 启动 kafka 的方式很简单，进入到安装目录之后，启动 cmd 输入 bin\windows\kafka-server-start.bat config\server.properties 启动 kafka ，之后报错：提示 ERROR Disk error while writing log start offsets checkpoint in directory 。查询解决方法，两种可能：① jdk 版本过低（安装的 14 ，原因除外）；② 更换 Kafka 版本，按照提示，更换成 kafka_2.12-2.8.1 版本。下载链接：https://archive.apache.org/dist/kafka/2.8.1/kafka_2.12-2.8.1.tgz ，下载之后，修改两个配置文件，重新启动 zookeeper 以及 Kafka ，启动成功。

## Elasticsearch 配置

进入到安装目录下的 config 目录中，打开 elasticsearch.yml 文件，配置其中的集群名字、data 数据文件夹、 logs 文件夹：

```yml
cluster.name: community
path.data: D:\Java\IdeaProjects\community\log\Elaticsearch-6.4.3\data
path.logs: D:\Java\IdeaProjects\community\log\Elaticsearch-6.4.3\logs
```

将中文检索词进行分词，需要下载一个分词插件。分词插件为 IK 分词插件。

> 下载地址：[GitHub - medcl/elasticsearch-analysis-ik: The IK Analysis plugin integrates Lucene IK analyzer into elasticsearch, support customized dictionary.](https://github.com/medcl/elasticsearch-analysis-ik)

下载之后，将文件解压缩到 ES 安装目录的 plugins 目录中，在此新建一个 ik 文件夹，解压缩至此即可。

Spring 整合 ES 时出现的问题，避免踩坑：

> 遇到的问题：因为 Spring Boot 版本不一致问题，导致导入的 ES 版本无法和本地安装的 ES 进行配置。在实体类上标注 type 属性找不到该 value 值，最后在 maven 仓库找到教程中 2.1.5 版本的 Spring Boot 对应的 ES 依赖版本为 3.1.8.RELEASE 版本，在 ES 依赖上标注即可。
>
> 查找版本对应的关系可以在网站：[Maven Repository: org.springframework.boot » spring-boot-starter-data-elasticsearch » 2.1.5.RELEASE (mvnrepository.com)](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-elasticsearch/2.1.5.RELEASE) 找到。
>
> 重新配置版本依赖之后，依然会出现 org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'discussPostRepository' 错误的提示。更换 Spring Boot 的版本为 2.1.5.RELEASE 版本。之后出现插件加载错误：Cannot resolve plugin org.apache.maven.plugins:maven-site-plugin:3.7.1 提示。解决办法：向 pom.xml 文件中添加具体版本依赖：
>
> ```
> <dependency>
> <groupId>org.apache.maven.plugins</groupId>
> <artifactId>maven-site-plugin</artifactId>
> <version>3.7.1</version>
> </dependency>
> ```
>
> 重新下载一下指定的依赖。
>
> 之后还是发生了依赖错误的问题，最终降级 Spring Boot 版本，将 Spring Boot 版本降级到 2.1.5.RELEASE 得以解决。降级的时候不只修改 `<version>` ，还需要删除可能产生冲突的依赖。最后直接删除 repository 文件夹得以解决。
>
> 之后测试的时候又出现错误：mapper [createTime] of different type, current_type [long], merged_type [date] 导致整个服务不能正常启动。查看提示发现是类型不匹配导致的，但是检查发现实体类代码不存在错误。clean install 清除缓存都尝试了之后还是不行，最后重新启动 ES 服务器，通过 postman 将已经存在的 ES 索引删除，之后重新进行测试，成功！
> 原因：ES 中的索引和映射发生了改变
> 解决：删除 ES 中已经存在的索引和映射，重新导入
>
> 至此，ES 导致的错误全部解决了。

