package com.imooc.service.impl;

import com.imooc.es.pojo.Items;
import com.imooc.service.ItemsESService;
import com.imooc.utils.PagedGridResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsESServiceImpl implements ItemsESService {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Override
    public PagedGridResult searhItems(String keywords, String sort, Integer page, Integer pageSize) {
        page--;// ES分页从0开始

        String itemNameField = "itemName";

        // 使用默认</em>标签
        HighlightBuilder.Field highlightBuilderField = new HighlightBuilder.Field(itemNameField);
//                .preTags("<font color='red'>")
//                .postTags("</font>");

        // 排序判断
        SortBuilder sortBuilder = null;
        switch (sort){
            case "c":
                sortBuilder = new FieldSortBuilder("sellCounts").order(SortOrder.DESC);
                break;
            case "p":
                sortBuilder = new FieldSortBuilder("price").order(SortOrder.ASC);
                break;
            default:
                sortBuilder = new FieldSortBuilder("itemName.keyword").order(SortOrder.ASC);
                break;
        }

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameField, keywords))
                .withHighlightFields(highlightBuilderField)
                .withSort(sortBuilder)
                .withPageable(PageRequest.of(page, pageSize))
                .build();
        AggregatedPage<Items> pagedObject = esTemplate.queryForPage(nativeSearchQuery, Items.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<Items> highlightedObjectList = new ArrayList<>();

                SearchHits hits = searchResponse.getHits();
                for (SearchHit hit : hits) {
                    HighlightField highlightField = hit.getHighlightFields().get(itemNameField);

                    Items items = new Items();
                    items.setItemId(String.valueOf((hit.getSourceAsMap().get("itemId").toString())));
                    items.setItemName(highlightField.getFragments()[0].toString());
                    items.setImgUrl(String.valueOf((hit.getSourceAsMap().get("imgUrl").toString())));
                    items.setPrice(Integer.valueOf((hit.getSourceAsMap().get("price").toString())));
                    items.setSellCounts(Integer.valueOf((hit.getSourceAsMap().get("sellCounts").toString())));

                    highlightedObjectList.add(items);
                }

                return new AggregatedPageImpl<>((List)highlightedObjectList, pageable, searchResponse.getHits().totalHits);
            }
        });

        PagedGridResult pagedGridResult = new PagedGridResult();
        pagedGridResult.setRows(pagedObject.getContent());
        pagedGridResult.setPage(page + 1);
        pagedGridResult.setTotal(pagedObject.getTotalPages());
        pagedGridResult.setRecords(pagedObject.getTotalElements());
        return pagedGridResult;
    }
}
