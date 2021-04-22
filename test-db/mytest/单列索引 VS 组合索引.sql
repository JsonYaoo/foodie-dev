-- 没有索引时ALL全表扫描
explain
select *
from salaries
where from_date = '1986-06-26'
      and to_date = '1987-06-26';

-- 开启OPTIMIZER_TRACE
SET OPTIMIZER_TRACE="enabled=on",END_MARKERS_IN_JSON=on;
SET optimizer_trace_offset=-30, optimizer_trace_limit=30;

-- 创建单列索引: 查询花费70ms => index_merge, Using intersect(salaries_to_date_index,salaries_from_date_index); Using where
CREATE INDEX salaries_from_date_index ON salaries (from_date);
CREATE INDEX salaries_to_date_index ON salaries (to_date);

-- 查看跟踪信息 => 这里88行和86的index_merge索引交集开销并不大, 所以时间花费上看起来并不必ref差, 但是数据量大的话就另说了
-- salaries_from_date_index => "rows": 88, "cost": 65.819,
-- salaries_to_date_index => "rows": 86, "cost": 64.338,
-- intersecting_indexes => 	"rows": 1, "cost": 2.2098,
SELECT * FROM INFORMATION_SCHEMA.OPTIMIZER_TRACE
WHERE QUERY LIKE '%salaries%' LIMIT 30;
{
	"steps": [{
			"join_preparation": {
				"select#": 1,
				"steps": [{
					"expanded_query": "/* select#1 */ select `salaries`.`emp_no` AS `emp_no`,`salaries`.`salary` AS `salary`,`salaries`.`from_date` AS `from_date`,`salaries`.`to_date` AS `to_date` from `salaries` where ((`salaries`.`from_date` = '1986-06-26') and (`salaries`.`to_date` = '1987-06-26'))"
				}] /* steps */
			} /* join_preparation */
		},
		{
			"join_optimization": {
				"select#": 1,
				"steps": [{
						"condition_processing": {
							"condition": "WHERE",
							"original_condition": "((`salaries`.`from_date` = '1986-06-26') and (`salaries`.`to_date` = '1987-06-26'))",
							"steps": [{
									"transformation": "equality_propagation",
									"resulting_condition": "(multiple equal('1986-06-26', `salaries`.`from_date`) and multiple equal('1987-06-26', `salaries`.`to_date`))"
								},
								{
									"transformation": "constant_propagation",
									"resulting_condition": "(multiple equal('1986-06-26', `salaries`.`from_date`) and multiple equal('1987-06-26', `salaries`.`to_date`))"
								},
								{
									"transformation": "trivial_condition_removal",
									"resulting_condition": "(multiple equal(DATE'1986-06-26', `salaries`.`from_date`) and multiple equal(DATE'1987-06-26', `salaries`.`to_date`))"
								}
							] /* steps */
						} /* condition_processing */
					},
					{
						"substitute_generated_columns": {} /* substitute_generated_columns */
					},
					{
						"table_dependencies": [{
							"table": "`salaries`",
							"row_may_be_null": false,
							"map_bit": 0,
							"depends_on_map_bits": [] /* depends_on_map_bits */
						}] /* table_dependencies */
					},
					{
						"ref_optimizer_key_uses": [{
								"table": "`salaries`",
								"field": "from_date",
								"equals": "DATE'1986-06-26'",
								"null_rejecting": true
							},
							{
								"table": "`salaries`",
								"field": "to_date",
								"equals": "DATE'1987-06-26'",
								"null_rejecting": true
							}
						] /* ref_optimizer_key_uses */
					},
					{
						"rows_estimation": [{
							"table": "`salaries`",
							"range_analysis": {
								"table_scan": {
									"rows": 2838426,
									"cost": 2.1e6
								} /* table_scan */ ,
								"potential_range_indexes": [{
										"index": "PRIMARY",
										"usable": false,
										"cause": "not_applicable"
									},
									{
										"index": "salaries_from_date_index",
										"usable": true,
										"key_parts": [
											"from_date",
											"emp_no"
										] /* key_parts */
									},
									{
										"index": "salaries_to_date_index",
										"usable": true,
										"key_parts": [
											"to_date",
											"emp_no",
											"from_date"
										] /* key_parts */
									}
								] /* potential_range_indexes */ ,
								"setup_range_conditions": [] /* setup_range_conditions */ ,
								"group_index_range": {
									"chosen": false,
									"cause": "not_group_by_or_distinct"
								} /* group_index_range */ ,
								"skip_scan_range": {
									"potential_skip_scan_indexes": [{
											"index": "salaries_from_date_index",
											"usable": false,
											"cause": "query_references_nonkey_column"
										},
										{
											"index": "salaries_to_date_index",
											"usable": false,
											"cause": "query_references_nonkey_column"
										}
									] /* potential_skip_scan_indexes */
								} /* skip_scan_range */ ,
								"analyzing_range_alternatives": {
									"range_scan_alternatives": [{
											"index": "salaries_from_date_index",
											"ranges": [
												"0xda840f <= from_date <= 0xda840f"
											] /* ranges */ ,
											"index_dives_for_eq_ranges": true,
											"rowid_ordered": true,
											"using_mrr": false,
											"index_only": false,
											"rows": 88,
											"cost": 65.819,
											"chosen": true
										},
										{
											"index": "salaries_to_date_index",
											"ranges": [
												"0xda860f <= to_date <= 0xda860f"
											] /* ranges */ ,
											"index_dives_for_eq_ranges": true,
											"rowid_ordered": true,
											"using_mrr": false,
											"index_only": false,
											"rows": 86,
											"cost": 64.338,
											"chosen": true
										}
									] /* range_scan_alternatives */ ,
									"analyzing_roworder_intersect": {
										"intersecting_indexes": [{
												"index": "salaries_to_date_index",
												"index_scan_cost": 1.1037,
												"cumulated_index_scan_cost": 1.1037,
												"disk_sweep_cost": 54.992,
												"cumulated_total_cost": 56.095,
												"usable": true,
												"matching_rows_now": 86,
												"isect_covering_with_this_index": false,
												"chosen": true
											},
											{
												"index": "salaries_from_date_index",
												"index_scan_cost": 1.1061,
												"cumulated_index_scan_cost": 2.2098,
												"disk_sweep_cost": 0,
												"cumulated_total_cost": 2.2098,
												"usable": true,
												"matching_rows_now": 0.0027,
												"isect_covering_with_this_index": false,
												"chosen": true
											}
										] /* intersecting_indexes */ ,
										"clustered_pk": {
											"clustered_pk_added_to_intersect": false,
											"cause": "no_clustered_pk_index"
										} /* clustered_pk */ ,
										"rows": 1,
										"cost": 2.2098,
										"covering": false,
										"chosen": true
									} /* analyzing_roworder_intersect */
								} /* analyzing_range_alternatives */ ,
								"chosen_range_access_summary": {
									"range_access_plan": {
										"type": "index_roworder_intersect",
										"rows": 1,
										"cost": 2.2098,
										"covering": false,
										"clustered_pk_scan": false,
										"intersect_of": [{
												"type": "range_scan",
												"index": "salaries_to_date_index",
												"rows": 86,
												"ranges": [
													"0xda860f <= to_date <= 0xda860f AND 0xda840f <= from_date <= 0xda840f"
												] /* ranges */
											},
											{
												"type": "range_scan",
												"index": "salaries_from_date_index",
												"rows": 88,
												"ranges": [
													"0xda840f <= from_date <= 0xda840f"
												] /* ranges */
											}
										] /* intersect_of */
									} /* range_access_plan */ ,
									"rows_for_plan": 1,
									"cost_for_plan": 2.2098,
									"chosen": true
								} /* chosen_range_access_summary */
							} /* range_analysis */
						}] /* rows_estimation */
					},
					{
						"considered_execution_plans": [{
							"plan_prefix": [] /* plan_prefix */ ,
							"table": "`salaries`",
							"best_access_path": {
								"considered_access_paths": [{
										"access_type": "ref",
										"index": "salaries_from_date_index",
										"rows": 88,
										"cost": 65.168,
										"chosen": true
									},
									{
										"access_type": "ref",
										"index": "salaries_to_date_index",
										"rows": 86,
										"cost": 63.687,
										"chosen": true
									},
									{
										"rows_to_scan": 1,
										"access_type": "range",
										"range_details": {
											"used_index": "intersect(salaries_to_date_index,salaries_from_date_index)"
										} /* range_details */ ,
										"resulting_rows": 1,
										"cost": 2.3098,
										"chosen": true
									}
								] /* considered_access_paths */
							} /* best_access_path */ ,
							"condition_filtering_pct": 100,
							"rows_for_plan": 1,
							"cost_for_plan": 2.3098,
							"chosen": true
						}] /* considered_execution_plans */
					},
					{
						"attaching_conditions_to_tables": {
							"original_condition": "((`salaries`.`to_date` = DATE'1987-06-26') and (`salaries`.`from_date` = DATE'1986-06-26'))",
							"attached_conditions_computation": [] /* attached_conditions_computation */ ,
							"attached_conditions_summary": [{
								"table": "`salaries`",
								"attached": "((`salaries`.`to_date` = DATE'1987-06-26') and (`salaries`.`from_date` = DATE'1986-06-26'))"
							}] /* attached_conditions_summary */
						} /* attaching_conditions_to_tables */
					},
					{
						"finalizing_table_conditions": [{
							"table": "`salaries`",
							"original_table_condition": "((`salaries`.`to_date` = DATE'1987-06-26') and (`salaries`.`from_date` = DATE'1986-06-26'))",
							"final_table_condition   ": "((`salaries`.`to_date` = DATE'1987-06-26') and (`salaries`.`from_date` = DATE'1986-06-26'))"
						}] /* finalizing_table_conditions */
					},
					{
						"refine_plan": [{
							"table": "`salaries`"
						}] /* refine_plan */
					}
				] /* steps */
			} /* join_optimization */
		},
		{
			"join_execution": {
				"select#": 1,
				"steps": [] /* steps */
			} /* join_execution */
		}
	] /* steps */
}

-- 创建组合索引(复合索引/多列索引): 查询花费67ms => ref, null
CREATE INDEX salaries_from_date_to_date_index ON salaries (from_date, to_date);

-- 查看跟踪信息
-- salaries_from_date_to_date_index => "rows": 86,"cost": 64.263,
SELECT * FROM INFORMATION_SCHEMA.OPTIMIZER_TRACE
WHERE QUERY LIKE '%salaries%' LIMIT 30;
{
  "steps": [
    {
      "join_preparation": {
        "select#": 1,
        "steps": [
          {
            "expanded_query": "/* select#1 */ select `salaries`.`emp_no` AS `emp_no`,`salaries`.`salary` AS `salary`,`salaries`.`from_date` AS `from_date`,`salaries`.`to_date` AS `to_date` from `salaries` where ((`salaries`.`from_date` = '1986-06-26') and (`salaries`.`to_date` = '1987-06-26'))"
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
              "original_condition": "((`salaries`.`from_date` = '1986-06-26') and (`salaries`.`to_date` = '1987-06-26'))",
              "steps": [
                {
                  "transformation": "equality_propagation",
                  "resulting_condition": "(multiple equal('1986-06-26', `salaries`.`from_date`) and multiple equal('1987-06-26', `salaries`.`to_date`))"
                },
                {
                  "transformation": "constant_propagation",
                  "resulting_condition": "(multiple equal('1986-06-26', `salaries`.`from_date`) and multiple equal('1987-06-26', `salaries`.`to_date`))"
                },
                {
                  "transformation": "trivial_condition_removal",
                  "resulting_condition": "(multiple equal(DATE'1986-06-26', `salaries`.`from_date`) and multiple equal(DATE'1987-06-26', `salaries`.`to_date`))"
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
                "table": "`salaries`",
                "row_may_be_null": false,
                "map_bit": 0,
                "depends_on_map_bits": [
                ] /* depends_on_map_bits */
              }
            ] /* table_dependencies */
          },
          {
            "ref_optimizer_key_uses": [
              {
                "table": "`salaries`",
                "field": "from_date",
                "equals": "DATE'1986-06-26'",
                "null_rejecting": true
              },
              {
                "table": "`salaries`",
                "field": "to_date",
                "equals": "DATE'1987-06-26'",
                "null_rejecting": true
              }
            ] /* ref_optimizer_key_uses */
          },
          {
            "rows_estimation": [
              {
                "table": "`salaries`",
                "range_analysis": {
                  "table_scan": {
                    "rows": 2838426,
                    "cost": 2.1e6
                  } /* table_scan */,
                  "potential_range_indexes": [
                    {
                      "index": "PRIMARY",
                      "usable": false,
                      "cause": "not_applicable"
                    },
                    {
                      "index": "salaries_from_date_to_date_index",
                      "usable": true,
                      "key_parts": [
                        "from_date",
                        "to_date",
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
                        "index": "salaries_from_date_to_date_index",
                        "usable": false,
                        "cause": "query_references_nonkey_column"
                      }
                    ] /* potential_skip_scan_indexes */
                  } /* skip_scan_range */,
                  "analyzing_range_alternatives": {
                    "range_scan_alternatives": [
                      {
                        "index": "salaries_from_date_to_date_index",
                        "ranges": [
                          "0xda840f <= from_date <= 0xda840f AND 0xda860f <= to_date <= 0xda860f"
                        ] /* ranges */,
                        "index_dives_for_eq_ranges": true,
                        "rowid_ordered": true,
                        "using_mrr": false,
                        "index_only": false,
                        "rows": 86,
                        "cost": 64.263,
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
                      "index": "salaries_from_date_to_date_index",
                      "rows": 86,
                      "ranges": [
                        "0xda840f <= from_date <= 0xda840f AND 0xda860f <= to_date <= 0xda860f"
                      ] /* ranges */
                    } /* range_access_plan */,
                    "rows_for_plan": 86,
                    "cost_for_plan": 64.263,
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
                "table": "`salaries`",
                "best_access_path": {
                  "considered_access_paths": [
                    {
                      "access_type": "ref",
                      "index": "salaries_from_date_to_date_index",
                      "rows": 86,
                      "cost": 63.613,
                      "chosen": true
                    },
                    {
                      "access_type": "range",
                      "range_details": {
                        "used_index": "salaries_from_date_to_date_index"
                      } /* range_details */,
                      "chosen": false,
                      "cause": "heuristic_index_cheaper"
                    }
                  ] /* considered_access_paths */
                } /* best_access_path */,
                "condition_filtering_pct": 100,
                "rows_for_plan": 86,
                "cost_for_plan": 63.613,
                "chosen": true
              }
            ] /* considered_execution_plans */
          },
          {
            "attaching_conditions_to_tables": {
              "original_condition": "((`salaries`.`to_date` = DATE'1987-06-26') and (`salaries`.`from_date` = DATE'1986-06-26'))",
              "attached_conditions_computation": [
              ] /* attached_conditions_computation */,
              "attached_conditions_summary": [
                {
                  "table": "`salaries`",
                  "attached": "((`salaries`.`to_date` = DATE'1987-06-26') and (`salaries`.`from_date` = DATE'1986-06-26'))"
                }
              ] /* attached_conditions_summary */
            } /* attaching_conditions_to_tables */
          },
          {
            "finalizing_table_conditions": [
              {
                "table": "`salaries`",
                "original_table_condition": "((`salaries`.`to_date` = DATE'1987-06-26') and (`salaries`.`from_date` = DATE'1986-06-26'))",
                "final_table_condition   ": null
              }
            ] /* finalizing_table_conditions */
          },
          {
            "refine_plan": [
              {
                "table": "`salaries`"
              }
            ] /* refine_plan */
          }
        ] /* steps */
      } /* join_optimization */
    },
    {
      "join_execution": {
        "select#": 1,
        "steps": [
        ] /* steps */
      } /* join_execution */
    }
  ] /* steps */
}