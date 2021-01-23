package com.test;

import com.imooc.EsApplication;
import com.imooc.es.pojo.Stu;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void deleteIndexStu(){
        esTemplate.deleteIndex(Stu.class);
    }
}
