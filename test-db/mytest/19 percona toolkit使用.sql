-- 查看慢查询日志是否开启等信息
show variables like '%slow%';
-- 开启慢查询日志
set global slow_query_log=on;
-- 查看慢查询日志的输出类型
show variables like 'log_output';
-- 设置慢查询日志记录阈值
show variables like '%long_query_time%';
set long_query_time = 0.001;

-- 大于Mysql 8.0版本的可以使用 => 用于标记某个索引不可见
alter table employees alter index employees_first_name_last_name_birth_date_index invisible;