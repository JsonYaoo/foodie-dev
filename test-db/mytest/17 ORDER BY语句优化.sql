/*
 * 为什么一会儿是ALL一会儿是INDEX
 * - MySQL优化器发现全表扫描开销更低时，会直接用全表扫描
 * - 这里是可以使用索引避免排序的
 */
explain
select *
from employees
order by first_name, last_name
limit 10;

/*
 * 可以使用索引避免排序
 * [Bader,last_name1, emp_no]
 * [Bader,last_name2, emp_no]
 * [Bader,last_name3, emp_no]
 * [Bader,last_name4, emp_no]
 * [Bader,last_name5, emp_no]
 * ..
 */
explain
select *
from employees
where first_name = 'Bader'
order by last_name;

/*
 * 可以使用索引避免排序
 * ['Angel', firstname1, emp_no1]
 * ['Anni', firstname1, emp_no1]
 * ['Anz', firstname1, emp_no1]
 * ['Bader', firstname1, emp_no1]
 */
explain
select *
from employees
where first_name < 'Bader'
order by first_name;

/*
 * 可以使用索引避免排序
 */
explain
select *
from employees
where first_name = 'Bader'
  and last_name > 'Peng'
order by last_name;

/*
 * 无法利用索引避免排序【排序字段存在于多个索引中】
 * - first_name => (first_name,last_name)复合索引中
 * - emp_no => 主键索引中
 */
explain
select *
from employees
order by first_name, emp_no
limit 10;

/*
 * 无法利用索引避免排序【升降序不一致】
 */
explain
select *
from employees
order by first_name desc, last_name asc
limit 10;

/*
 * 无法利用索引避免排序【使用key_part1范围查询，使用key_part2排序】
 * ['Angel', lastname2, emp_no1]
 * ['Anni', lastname1, emp_no1]
 * ['Anz', lastname2, emp_no1]
 * ['Bader', lastname1, emp_no1]
 */
explain
select *
from employees
where first_name < 'Bader'
order by last_name;

-- sort buffer = 256k => sort_buffer不够时, 则会使用归并操作, 多次读写&排序:
-- 满足条件的(id, order_column) = 100m
-- [(10001,'Angel'),(88888,'Keeper'),(100001,'Zaker')] => file1
-- [(77777,'Jim'),(99999,'Lucy'),(5555, 'Hanmeimei')] => file2
-- [(10001,'Angel'),(5555, 'Hanmeimei'),(77777,'Jim'),(88888,'Keeper'),(99999,'Lucy'),(100001,'Zaker')]

-- 打开OPTIMIZER_TRACE分析
SET OPTIMIZER_TRACE="enabled=on",END_MARKERS_IN_JSON=on;
SET optimizer_trace_offset=-30, optimizer_trace_limit=30;
-- 执行业务SQL
select *
from employees
where first_name < 'Bader'
order by last_name;
-- 查询分析结果
select * from `information_schema`.OPTIMIZER_TRACE
where QUERY like '%Bader%';
{
  "steps": [
    {
      "join_preparation": {
        "select#": 1,
        "steps": [
          {
            "expanded_query": "/* select#1 */ select `employees`.`emp_no` AS `emp_no`,`employees`.`birth_date` AS `birth_date`,`employees`.`first_name` AS `first_name`,`employees`.`last_name` AS `last_name`,`employees`.`gender` AS `gender`,`employees`.`hire_date` AS `hire_date`,`employees`.`first_name_hash` AS `first_name_hash` from `employees` where (`employees`.`first_name` < 'Bader') order by `employees`.`last_name`"
          }
        ] /* steps */
      } /* join_preparation */
    },
    {
      "join_optimization": {
        "select#": 1,
        "steps": [
          {
            "condition_processing": {
              "condition": "WHERE",
              "original_condition": "(`employees`.`first_name` < 'Bader')",
              "steps": [
                {
                  "transformation": "equality_propagation",
                  "resulting_condition": "(`employees`.`first_name` < 'Bader')"
                },
                {
                  "transformation": "constant_propagation",
                  "resulting_condition": "(`employees`.`first_name` < 'Bader')"
                },
                {
                  "transformation": "trivial_condition_removal",
                  "resulting_condition": "(`employees`.`first_name` < 'Bader')"
                }
              ] /* steps */
            } /* condition_processing */
          },
          {
            "substitute_generated_columns": {
            } /* substitute_generated_columns */
          },
          {
            "table_dependencies": [
              {
                "table": "`employees`",
                "row_may_be_null": false,
                "map_bit": 0,
                "depends_on_map_bits": [
                ] /* depends_on_map_bits */
              }
            ] /* table_dependencies */
          },
          {
            "ref_optimizer_key_uses": [
            ] /* ref_optimizer_key_uses */
          },
          {
            "rows_estimation": [
              {
                "table": "`employees`",
                "range_analysis": {
                  "table_scan": {
                    "rows": 299202,
                    "cost": 30155
                  } /* table_scan */,
                  "potential_range_indexes": [
                    {
                      "index": "PRIMARY",
                      "usable": false,
                      "cause": "not_applicable"
                    },
                    {
                      "index": "employees_first_name_last_name_index",
                      "usable": true,
                      "key_parts": [
                        "first_name",
                        "last_name",
                        "emp_no"
                      ] /* key_parts */
                    }
                  ] /* potential_range_indexes */,
                  "setup_range_conditions": [
                  ] /* setup_range_conditions */,
                  "group_index_range": {
                    "chosen": false,
                    "cause": "not_group_by_or_distinct"
                  } /* group_index_range */,
                  "skip_scan_range": {
                    "potential_skip_scan_indexes": [
                      {
                        "index": "employees_first_name_last_name_index",
                        "usable": false,
                        "cause": "query_references_nonkey_column"
                      }
                    ] /* potential_skip_scan_indexes */
                  } /* skip_scan_range */,
                  "analyzing_range_alternatives": {
                    "range_scan_alternatives": [
                      {
                        "index": "employees_first_name_last_name_index",
                        "ranges": [
                          "first_name < Bader"
                        ] /* ranges */,
                        "index_dives_for_eq_ranges": true,
                        "rowid_ordered": false,
                        "using_mrr": false,
                        "index_only": false,
                        "rows": 45208,
                        "cost": 15823,
                        "chosen": true
                      }
                    ] /* range_scan_alternatives */,
                    "analyzing_roworder_intersect": {
                      "usable": false,
                      "cause": "too_few_roworder_scans"
                    } /* analyzing_roworder_intersect */
                  } /* analyzing_range_alternatives */,
                  "chosen_range_access_summary": {
                    "range_access_plan": {
                      "type": "range_scan",
                      "index": "employees_first_name_last_name_index",
                      "rows": 45208,
                      "ranges": [
                        "first_name < Bader"
                      ] /* ranges */
                    } /* range_access_plan */,
                    "rows_for_plan": 45208,
                    "cost_for_plan": 15823,
                    "chosen": true
                  } /* chosen_range_access_summary */
                } /* range_analysis */
              }
            ] /* rows_estimation */
          },
          {
            "considered_execution_plans": [
              {
                "plan_prefix": [
                ] /* plan_prefix */,
                "table": "`employees`",
                "best_access_path": {
                  "considered_access_paths": [
                    {
                      "rows_to_scan": 45208,
                      "filtering_effect": [
                      ] /* filtering_effect */,
                      "final_filtering_effect": 1,
                      "access_type": "range",
                      "range_details": {
                        "used_index": "employees_first_name_last_name_index"
                      } /* range_details */,
                      "resulting_rows": 45208,
                      "cost": 20344,
                      "chosen": true
                    }
                  ] /* considered_access_paths */
                } /* best_access_path */,
                "condition_filtering_pct": 100,
                "rows_for_plan": 45208,
                "cost_for_plan": 20344,
                "chosen": true
              }
            ] /* considered_execution_plans */
          },
          {
            "attaching_conditions_to_tables": {
              "original_condition": "(`employees`.`first_name` < 'Bader')",
              "attached_conditions_computation": [
                {
                  "table": "`employees`",
                  "rechecking_index_usage": {
                    "recheck_reason": "low_limit",
                    "limit": 502,
                    "row_estimate": 45208
                  } /* rechecking_index_usage */
                }
              ] /* attached_conditions_computation */,
              "attached_conditions_summary": [
                {
                  "table": "`employees`",
                  "attached": "(`employees`.`first_name` < 'Bader')"
                }
              ] /* attached_conditions_summary */
            } /* attaching_conditions_to_tables */
          },
          {
            "optimizing_distinct_group_by_order_by": {
              "simplifying_order_by": {
                "original_clause": "`employees`.`last_name`",
                "items": [
                  {
                    "item": "`employees`.`last_name`"
                  }
                ] /* items */,
                "resulting_clause_is_simple": true,
                "resulting_clause": "`employees`.`last_name`"
              } /* simplifying_order_by */
            } /* optimizing_distinct_group_by_order_by */
          },
          {
            "reconsidering_access_paths_for_index_ordering": {
              "clause": "ORDER BY",
              "steps": [
              ] /* steps */,
              "index_order_summary": {
                "table": "`employees`",
                "index_provides_order": false,
                "order_direction": "undefined",
                "index": "employees_first_name_last_name_index",
                "plan_changed": false
              } /* index_order_summary */
            } /* reconsidering_access_paths_for_index_ordering */
          },
          {
            "finalizing_table_conditions": [
              {
                "table": "`employees`",
                "original_table_condition": "(`employees`.`first_name` < 'Bader')",
                "final_table_condition   ": "(`employees`.`first_name` < 'Bader')"
              }
            ] /* finalizing_table_conditions */
          },
          {
            "refine_plan": [
              {
                "table": "`employees`",
                "pushed_index_condition": "(`employees`.`first_name` < 'Bader')",
                "table_condition_attached": null
              }
            ] /* refine_plan */
          },
          {
            "considering_tmp_tables": [
              {
                "adding_sort_to_table": "employees"
              } /* filesort */
            ] /* considering_tmp_tables */
          }
        ] /* steps */
      } /* join_optimization */
    },
    {
      -- 3. 执行阶段的执行过程
      "join_execution": {
        "select#": 1,
        "steps": [
          {
            -- 排序后特有的内容
            "sorting_table": "employees",
            "filesort_information": [
              {
                "direction": "asc",
                "expression": "`employees`.`last_name`"
              }
            ] /* filesort_information */,
            "filesort_priority_queue_optimization": {
              "limit": 502,
              "chosen": true
            } /* filesort_priority_queue_optimization */,
            "filesort_execution": [
            ] /* filesort_execution */,
            -- 重点关注 filesort_summary
            "filesort_summary": {
              -- 可用内存, 其实就是sort_buffer_size => 默认256k
              "memory_available": 262144,
              "key_size": 264,
              "row_size": 401,
              "max_rows_per_buffer": 503,
              "num_rows_estimate": 45208,
              -- 本次排序一共参与排序的行数
              "num_rows_found": 22287,
              -- 本次排序产生了几个临时文件, 0则代表是完全基于内存排序
              "num_initial_chunks_spilled_to_disk": 0,
              "peak_memory_used": 205727,
              "sort_algorithm": "std::sort",
              "unpacked_addon_fields": "using_priority_queue",
              -- 使用的排序模式: 这里全字段排序 => rowid、additional_fields、packed_additional_fields
              "sort_mode": "<varlen_sort_key, additional_fields>"
            } /* filesort_summary */
          }
        ] /* steps */
      } /* join_execution */
    }
  ] /* steps */
}
-- 查询一共执行多少次归并操作
show status like '%sort_merge_passes%'

-- 调优实战
-- 调优之前271ms
select *
from employees
where first_name < 'Bader'
order by last_name;
-- eg: => "num_initial_chunks_spilled_to_disk": 47,
-- 则调优之后168ms
set sort_buffer_size = 1024*1024;
select *
from employees
where first_name < 'Bader'
order by last_name;
-- eg: => "num_initial_chunks_spilled_to_disk": 0,