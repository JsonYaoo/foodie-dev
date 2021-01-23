package com.test;

import com.imooc.EsApplication;
import com.imooc.es.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EsApplication.class)
public class ESTest {

    /**
     * 不建议使用 ElasticsearchTemplate 对索引进行管理（创建索引，更新映射，删除索引）
     * 索引就像是数据库或者数据库中的表，我们平时是不会是通过java代码频繁的去创建修改删除数据库或者表的
     * 我们只会针对数据做CRUD的操作
     * 在es中也是同理，我们尽量使用 ElasticsearchTemplate 对文档数据做CRUD的操作
     * 1. 属性（FieldType）类型不灵活
     * 2. 主分片与副本分片数无法设置(该版本有bug)
     */
    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 创建索引
     */
    @Test
    public void createIndexStu(){
        Stu stu = new Stu();
        stu.setStuId(1002L);
        stu.setName("spider man");
        stu.setAge(22);
        stu.setMoney(18.8F);
        stu.setSign("I am spider man");
        stu.setDescription("I wish i am spider man");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        esTemplate.index(indexQuery);
    }

    /**
     * 删除索引
     */
    @Test
    public void deleteIndexStu(){
        esTemplate.deleteIndex(Stu.class);
    }

    /**
     * 更新文档
     */
    @Test
    public void updateStuDoc(){
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("sign", "I am not a super man");
        sourceMap.put("money", 88.6F);
        sourceMap.put("age", 33);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = new UpdateQueryBuilder()
                .withClass(Stu.class)
                .withId("1002")
                .withIndexRequest(indexRequest)
                .build();
        esTemplate.update(updateQuery);
    }

    /**
     * 查询文档
     */
    @Test
    public void getStuDoc(){
        GetQuery getQuery = new GetQuery();
        getQuery.setId("1002");
        Stu stu = esTemplate.queryForObject(getQuery, Stu.class);
        System.out.println(stu);
    }

    /**
     * 删除文档
     */
    @Test
    public void deleteStuDoc(){
        esTemplate.delete(Stu.class, "1002");
    }

    /**
     * 分页检索文档
     */
    @Test
    public void searchStuDoc(){
        PageRequest pageRequest = PageRequest.of(0, 10);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "save man"))
                .withPageable(pageRequest)
                .build();
        AggregatedPage<Stu> pagedStu = esTemplate.queryForPage(nativeSearchQuery, Stu.class);

        System.out.println("检索后的总分页数目为: " + pagedStu.getTotalPages());
        for (Stu stu : pagedStu.getContent()) {
            System.out.println(stu);
        }
    }
}
