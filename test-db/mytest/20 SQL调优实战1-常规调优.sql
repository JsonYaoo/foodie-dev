-- 关闭MySQL对存储过程的限制
set global log_bin_trust_function_creators = 0;

-- 执行insert into select...，重复repeat_times次 => 执行一次复制一次数据, 直到为数据的2^n倍数据量
drop procedure if exists prepare_test_data;
DELIMITER $$
CREATE PROCEDURE prepare_test_data(IN repeat_times INT(10))
BEGIN
    DECLARE i INT DEFAULT 0;
    loopname:
    LOOP
        SET i = i + 1;

        INSERT INTO carousel (id, image_url, background_color, item_id, cat_id, type, sort, is_show,
                              create_time, update_time)
        SELECT uuid(),
               image_url,
               background_color,
               item_id,
               cat_id,
               type,
               sort,
               is_show,
               create_time,
               update_time
        from carousel;

        insert into category (name, type, father_id, logo, slogan, cat_image, bg_color)
        select name, type, father_id, logo, slogan, cat_image, bg_color
        from category;

        insert into items(id, item_name, cat_id, root_cat_id, sell_counts, on_off_status, content,
                          created_time, updated_time)
        select uuid(),
               item_name,
               cat_id,
               root_cat_id,
               sell_counts,
               on_off_status,
               content,
               created_time,
               updated_time
        from items;

        insert into items_comments (id, user_id, item_id, item_name, item_spec_id, sepc_name, comment_level,
                                    content, created_time, updated_time)
        select uuid(),
               user_id,
               item_id,
               item_name,
               item_spec_id,
               sepc_name,
               comment_level,
               content,
               created_time,
               updated_time
        from items_comments;

        insert into items_img(id, item_id, url, sort, is_main, created_time, updated_time)
        select uuid(), item_id, url, sort, is_main, created_time, updated_time
        from items_img;

        insert into items_param(id, item_id, produc_place, foot_period, brand, factory_name,
                                factory_address, packaging_method, weight, storage_method, eat_method,
                                created_time, updated_time)
        select uuid(),
               item_id,
               produc_place,
               foot_period,
               brand,
               factory_name,
               factory_address,
               packaging_method,
               weight,
               storage_method,
               eat_method,
               created_time,
               updated_time
        from items_param;

        insert into items_spec (id, item_id, name, stock, discounts, price_discount, price_normal,
                                created_time, updated_time)
        select uuid(),
               item_id,
               name,
               stock,
               discounts,
               price_discount,
               price_normal,
               created_time,
               updated_time
        from items_spec;

        insert into order_items (id, order_id, item_id, item_img, item_name, item_spec_id, item_spec_name,
                                 price, buy_counts)
        select uuid(),
               order_id,
               item_id,
               item_img,
               item_name,
               item_spec_id,
               item_spec_name,
               price,
               buy_counts
        from order_items;

        insert into order_status (order_id, order_status, created_time, pay_time, deliver_time,
                                  success_time, close_time, comment_time)
        select uuid(),
               order_status,
               created_time,
               pay_time,
               deliver_time,
               success_time,
               close_time,
               comment_time
        from order_status;

        insert into orders (id, user_id, receiver_name, receiver_mobile, receiver_address, total_amount,
                            real_pay_amount, post_amount, pay_method, left_msg, extand, is_comment,
                            is_delete, created_time, updated_time)
        select uuid(),
               user_id,
               receiver_name,
               receiver_mobile,
               receiver_address,
               total_amount,
               real_pay_amount,
               post_amount,
               pay_method,
               left_msg,
               extand,
               is_comment,
               is_delete,
               created_time,
               updated_time
        from orders;

        insert into stu (name, age)
        select name, age
        from stu;

        insert into user_address (id, user_id, receiver, mobile, province, city, district, detail, extand,
                                  is_default, created_time, updated_time)
        select uuid(),
               user_id,
               receiver,
               mobile,
               province,
               city,
               district,
               detail,
               extand,
               is_default,
               created_time,
               updated_time
        from user_address;

        insert into users(id, username, password, nickname, realname, face, mobile, email, sex, birthday,
                          created_time, updated_time)
        select uuid(),
               username,
               password,
               nickname,
               realname,
               face,
               mobile,
               email,
               sex,
               birthday,
               created_time,
               updated_time
        from users;

        IF i = repeat_times THEN
            LEAVE loopname;
        END IF;
    END LOOP loopname;
END $$;

-- 把foodie-dev项目里面所有的表数据量变成原先的2^10倍
call prepare_test_data(10);
-- 查看慢查询日志是否开启等信息
show variables like '%slow%';
-- 开启慢查询日志
set global slow_query_log=on;
-- 查看慢查询日志的输出类型
show variables like 'log_output';
-- 设置慢查询日志记录阈值, 0.2表示200ms
show variables like '%long_query_time%';
set long_query_time = 0.2;

-- 优化之前需要花费2 s 448 ms
explain
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i
         straight_join
         items_img ii
     on
         i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on
         i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%好吃蛋糕甜点蒸蛋糕%'
order by i.sell_counts desc
LIMIT 10;

-- 问题1: 子查询发生全表扫描
-- 优化1：创建index(item_id,price_discount)后，花费1 s 116 ms
CREATE INDEX items_spec_item_id_price_discount_index ON items_spec (item_id, price_discount);
SELECT item_id, MIN(price_discount) as price_discount
from items_spec
GROUP BY item_id

-- 问题2: items_img发生全表扫描
-- 优化2：创建index(is_main, item_id)后，花费1 s 77 ms
CREATE INDEX items_img_is_main_item_id_index ON items_img (is_main, item_id);

-- 问题3: items表排序字段没有索引, items与items_img连接没有遵循小表驱动大表原则
-- 优化3：创建index(sell_counts,item_name)，并使用straight_join后变成150 ms
CREATE INDEX items_sell_counts_item_name_index ON items (sell_counts, item_name);
-- 优化3.1 -> sell_counts索引逆序排序, 108ms
DROP INDEX items_sell_counts_item_name_index ON items;
CREATE INDEX items_sell_counts_item_name_index ON items (sell_counts DESC, item_name);

-- 问题4: order by字段是动态变化的, 优化前475 ms
-- 优化4：在前面的基础上，额外创建index(item_name,sell_counts) -> 192 ms
CREATE INDEX items_item_name_sell_counts_index ON items (item_name, sell_counts);
explain
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i
         straight_join
         items_img ii
     on
         i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on
         i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%好吃蛋糕甜点蒸蛋糕%'
order by i.item_name asc
LIMIT 10;

-- 问题5: order by字段是动态变化的, 优化前366 ms
-- 优化5：子查询中间表, 不能加索引, 所以需要调大sort_buffer_size -> 382 ms, 作用不大(亲测), 依然是Using filesort
set sort_buffer_size = 4 * 1024 * 1024;
explain
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i
         straight_join
         items_img ii
     on
         i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on
         i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%好吃蛋糕甜点蒸蛋糕%'
order by tempSpec.price_discount asc
LIMIT 10;

-- 问题6: items表使用临时表, 使用了文件排序 => 不改sql很难优化了
-- 6、终极优化方案：反模式设计，引入冗余，把商品的最低优惠价（MIN(price_discount)）冗余到items表
-- 这样tempSpec子查询不需要了, price_discount排序也可以走上索引

-- ====================================================================================================================
-- 激进优化方案测试(不要滥用, 要看业务是否需要)
explain
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i straight_join items_img ii
     on i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%好吃蛋糕甜点蒸蛋糕%'
order by
    --
    i.sell_counts desc
    -- i.item_name asc
    -- tempSpec.price_discount asc
LIMIT 10;

-- SQL注入问题: mybatis xml中的SQL语句应该使用CONCAT('%', #{keyword}, '%')替换掉'%${keyword}%', 防止SQL注入
-- 比如使用keyword注入危险SQL方式:
SELECT i.id                    as itemId,
       i.item_name             as itemName,
       i.sell_counts           as sellCounts,
       ii.url                  as imgUrl,
       tempSpec.price_discount as price
FROM items i straight_join items_img ii
     on i.id = ii.item_id
         LEFT JOIN
     (SELECT item_id, MIN(price_discount) as price_discount from items_spec GROUP BY item_id) tempSpec
     on i.id = tempSpec.item_id
WHERE ii.is_main = 1
  AND i.item_name like '%xxx%';
drop table users;
select *
from items
where item_name like 'yyy%'
order by
    --
    i.sell_counts desc
    -- i.item_name asc
    -- tempSpec.price_discount asc
LIMIT 10;

-- 激进优化方案1：尽量使用右模糊，避免全模糊, 优化前108ms, 优化后92ms
explain
SELECT
  i.id                    as itemId,
  i.item_name             as itemName,
  i.sell_counts           as sellCounts,
  ii.url                  as imgUrl,
  tempSpec.price_discount as price
FROM
  items i
  straight_join items_img ii on i.id = ii.item_id
  LEFT JOIN (
   SELECT
     item_id,
     MIN(price_discount) as price_discount
   from items_spec
   GROUP BY item_id
 ) tempSpec on i.id = tempSpec.item_id
WHERE
  ii.is_main = 1
  AND i.item_name like '好吃蛋糕甜点蒸蛋糕%'
order by
      i.sell_counts desc
--   tempSpec.price_discount asc
LIMIT 10;

-- 激进优化方案2：彻底使用冗余优化SQL
 -- 由于多处是关联items_spec表, 所以可以把商品的最低优惠价，直接冗余到items表
 -- 由于多处是关联items_msg表, 所以也可以把商品主图也冗余到items表【商品主表字段较大，实际项目中如果要冗余较大的字段，应该谨慎考虑，看是否有必要】
/*
在items表中添加两个字段: price_discount, img_url

=> 优化后的覆盖索引查询语句: => 三表查询转换成单表查询
select id, item_name, sell_counts,img_url,price_discount
from items
where item_name like '%好吃蛋糕甜点蒸蛋糕%' order by
    sell_counts desc
    -- item_name asc
    -- price_discount asc
*/

-- 激进优化方案3：考虑使用非关系型数据库(elasticsearch | mongodb)
-- => 商品业务不需要非常严格的事务支持, 但性能确实硬需求, 可以考虑非关系型数据库(不是万能药, 要根据业务场景选择合适的数据库)

-- 激进优化方案4：业务妥协
-- eg: 如果可以不展示price_discount, 则可以省掉items_spec子查询的成本, 这个要看业务能不能这样妥协