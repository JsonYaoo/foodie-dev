-- 重复索引
create table test_table
(
    id int not null primary key auto_increment,
    a  int not null,
    b  int not null,
    UNIQUE (id),
    INDEX (id)
) ENGINE = InnoDB;
-- 发生了重复索引，改进方案： 删掉唯一索引和普通索引
create table test_table
(
    id int not null primary key auto_increment,
    a  int not null,
    b  int not null
) ENGINE = InnoDB;

-- 冗余索引: index(a)是index(a, b)的冗余索引
-- 冗余索引特例:
explain
select *
from salaries
where from_date = '1986-06-26'
order by emp_no;
-- 创建from_date索引: salaries_from_date_index
-- index(from_date): type=ref, extra=null，使用了索引
-- index(from_date) 某种意义上来说就相当于index(from_date, emp_no) => 因为emp_no是主键索引, 所以order by子句可以使用索引

-- CREATE INDEX salaries_from_date_to_date_index ON salaries (from_date, to_date);
-- DROP INDEX salaries_from_date_index ON salaries;
-- 而index(from_date, to_date): type=ref, extra=Using filesort，order by子句无法使用索引
-- index(from_date, to_date)某种意义上来说就相当于index(from_date, to_date, emp_no) => 跳过了to_date, 不满足最左前缀原则, 所以没走索引, 走了Using filesort
-- 因此, 这种特例下: 在有了index(from_date, to_date)复合索引, 为了保证order by能走索引, 还需要创建冗余索引index(from_date)


-- 未使用的索引: 即某个索引创建了没用 => 累赘, 直接删除, 使用工具分析找出来

