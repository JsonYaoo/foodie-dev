create table user_test_count
(
    id       int primary key not null auto_increment,
    name     varchar(45),
    age      int,
    email    varchar(60),
    birthday date
) engine 'innodb';

insert into user_test_count (id, name, age, email, birthday)
values (1, '张三', 20, 'zhangsan@imooc.com', '2000-01-01');
insert into user_test_count (id, name, age, email, birthday)
values (2, '李四', 30, 'lisi@imooc.com', '1990-01-01');
insert into user_test_count (id, name, age, email, birthday)
values (3, '王五', 40, null, null);
insert into user_test_count (id, name, age, email, birthday)
values (4, '大目', 18, null, null);

/*
 * 1. 当没有非主键索引时，会使用主键索引 PRIMARY => ken_len: 4
 * 2. 如果存在非主键索引的话，会使用非主键索引 user_test_count_email_index => ken_len: 243
 * 3. 如果存在多个非主键索引，会使用一个最小的非主键索引 user_test_count_birthday_index => ken_len: 4
 * 为什么?
 * -innodb非主键索引：叶子节点存储的是：索引+主键+(索引值 => 覆盖索引时使用)
 * 主键索引叶子节点：主键+表数据+(主键值 => 覆盖索引时使用)
 * 在1个page里面，非主键索引可以存储更多的条目，对于一张表，1000000数据，
 * 使用非主键索引 扫描page 100 ，主键索引 500
 * => mysql为了在相同的page大小中, 能够存放更多的数据, 从而减少page的扫描次数, 所以才有非主键索引时走非主键索引, 而不是主键索引
 * => mysql默认一页大小16k, 不是1024k => SHOW GLOBAL STATUS like 'Innodb_page_size';
 */
explain select count(*)
        from user_test_count;

/*
 * count(字段)只会针对该字段统计，使用这个字段上面的索引（如果有的话）, 如果没有则会全表扫描
 * count(字段)会排除掉该字段值为null的行
 * count(*)时选择的email索引则不会排除为null的行
 */
explain select count(email)
        from user_test_count;

/*
 * count(*)和count(1)没有区别，详见官方文档：https://dev.mysql.com/doc/refman/8.0/en/group-by-functions.html#function_count
 * 对于MyISAM引擎，如果count(*)没有where条件(形如 select count(*) from 表名)，查询会非常的快 => 因为表数据是只直接顺序存储在磁盘中(非聚蔟索引)
 * 对于MySQL 8.0.13，InnoDB引擎，如果count(*)没有where条件(形如 select count(*) from 表名)，查询也会被优化，性能有所提升
 */
explain select count(1)
        from user_test_count;


