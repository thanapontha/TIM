
create table TB_L_EXCEL_DOWNLOAD_STATUS
(
  doc_id           VARCHAR2(10) not null,
  criteria_legend  VARCHAR2(1000),
  ext_xls_gen      VARCHAR2(100),
  ext_xls_params   VARCHAR2(256),
  file_cnt         NUMBER(10),
  function_id      VARCHAR2(8) not null,
  max_exe_time     NUMBER(10) not null,
  max_xls_size     NUMBER(10) not null,
  model_class_name VARCHAR2(500),
  module_id        VARCHAR2(8) not null,
  override_path    VARCHAR2(200),
  pic_email        VARCHAR2(100),
  report_name      VARCHAR2(100),
  request_by       VARCHAR2(20) not null,
  request_dt       TIMESTAMP(6) not null,
  sql_stmt         CLOB,
  start_column     NUMBER(10) not null,
  start_row        NUMBER(10) not null,
  status           CHAR(1) not null,
  update_by        VARCHAR2(20) not null,
  update_dt        TIMESTAMP(6) not null,
  display_names    VARCHAR2(500)
)
tablespace TBSP_USER00
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );
alter table TB_L_EXCEL_DOWNLOAD_STATUS
  add primary key (DOC_ID)
  using index 
  tablespace TBSP_USER00
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

