create table TB_R_ODB_ROLES
(
  doc_id    VARCHAR2(10) not null,
  create_dt TIMESTAMP(6),
  role_id   VARCHAR2(50),
  id        VARCHAR2(60)
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
comment on column TB_R_ODB_ROLES.id
  is 'for model use only';
