package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    // 轮播图缓存key
    private static final String REDIS_KEY_CAROUSEL = "carousel";

    // 一级分类缓存key
    private static final String REDIS_KEY_CATS = "cats";

    // 二级分类缓存key
    private static final String REDIS_KEY_SUBCAT = "subCat";

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public IMOOCJSONResult carousel() {
        // 查询Redis缓存
        String carouselRedisStr = redisOperator.get(REDIS_KEY_CAROUSEL);
        if(StringUtils.isNotBlank(carouselRedisStr)){
            return IMOOCJSONResult.ok(JsonUtils.jsonToList(carouselRedisStr, Carousel.class));
        }

        // 设置Redis缓存
        List<Carousel> list = carouselService.queryAll(YesOrNo.YES.type);
        redisOperator.set(REDIS_KEY_CAROUSEL, JsonUtils.objectToJson(list));
        return IMOOCJSONResult.ok(list);

        /**
         * 关于更新缓存的三种方法：
         * 1. 后台运营系统，一旦广告（轮播图）发生更改，就可以删除缓存，然后重置
         * 2. 定时重置，比如每天凌晨三点重置 => 批量更新
         * 3. 每个轮播图都有可能是一个广告，每个广告都会有一个过期时间，过期了，再重置
         */
    }

    /**
     * 首页分类展示需求：
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public IMOOCJSONResult cats() {
        // 查询Redis缓存
        String catsRedisStr = redisOperator.get(REDIS_KEY_CATS);
        if(StringUtils.isNotBlank(catsRedisStr)){
            return IMOOCJSONResult.ok(JsonUtils.jsonToList(catsRedisStr, Category.class));
        }

        // 设置Redis缓存
        List<Category> list = categoryService.queryAllRootLevelCat();
        redisOperator.set(REDIS_KEY_CATS, JsonUtils.objectToJson(list));
        return IMOOCJSONResult.ok(list);

        // 后台系统更新缓存
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public IMOOCJSONResult subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {
        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        // 查询缓存
        String subCatRedisStr = redisOperator.get(REDIS_KEY_SUBCAT);
        if(StringUtils.isNotBlank(subCatRedisStr)){
            return IMOOCJSONResult.ok(JsonUtils.jsonToList(subCatRedisStr, CategoryVO.class));
        }

        // 设置缓存
        List<CategoryVO> list = categoryService.getSubCatList(rootCatId);
        redisOperator.set(REDIS_KEY_SUBCAT, JsonUtils.objectToJson(list));
        return IMOOCJSONResult.ok(list);

        // 后台系统更新缓存
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public IMOOCJSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return IMOOCJSONResult.errorMsg("分类不存在");
        }

        // 经常发生变换缓存酌情需用
        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return IMOOCJSONResult.ok(list);
    }

}
