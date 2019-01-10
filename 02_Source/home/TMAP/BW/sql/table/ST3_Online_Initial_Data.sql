--HIBERNATE_SEQUENCES
insert into HIBERNATE_SEQUENCES (sequence_name, sequence_next_hi_value)
values ('TB_L_LOGGER', 1);
--TB_M_MODULE_D
insert into TB_M_MODULE_D (v_module_id, v_function_id, v_function_name, v_error_flag, create_by, create_dt, update_by, update_dt)
values ('ST3400', 'ST3090', 'Excel Download Screen', 'N', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
--TB_M_MODULE_H
insert into TB_M_MODULE_H (v_module_id, v_module_name, create_by, create_dt, update_by, update_dt)
values ('ST3400', 'IT Development Standard Online Screens', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
--TB_M_SYSTEM
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'BATCH_STATUS', 'E', 'Error', 'Error Occurred During Processing', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'BATCH_STATUS', 'F', 'Fatal', 'Fatal Error Occurred During Processing', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'BATCH_STATUS', 'I', 'Success', 'Batch Process Completed Successfully', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'BATCH_STATUS', 'P', 'Processing', 'Batch Process Ongoing', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'BATCH_STATUS', 'W', 'Warning', 'Batch Process Completed with Error(s)', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'PROCESS_STATUS', 'E', 'End', 'End Process', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'PROCESS_STATUS', 'P', 'Processing', 'Ongoing Process', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'PROCESS_STATUS', 'S', 'Start', 'Start Process', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'MESSAGE_LEVEL', 'E', 'Error', 'Error Message', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'MESSAGE_LEVEL', 'F', 'Fatal', 'Fatal Message', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'MESSAGE_LEVEL', 'I', 'Info', 'Information Message', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
insert into TB_M_SYSTEM (category, sub_category, cd, value, remark, status, create_by, create_dt, update_by, update_dt)
values ('ST3', 'MESSAGE_LEVEL', 'W', 'Warning', 'Warning Message', 'Y', 'SYSTEM', SYSDATE, 'SYSTEM', SYSDATE);
