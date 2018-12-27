-------------------------------------
--  New table hibernate_sequences  --
-------------------------------------
-- Create table
create table HIBERNATE_SEQUENCES
(
  sequence_name          VARCHAR2(255),
  sequence_next_hi_value NUMBER(10)
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
-- Add comments to the table 
comment on table HIBERNATE_SEQUENCES
  is 'CATEGORY MASTER';
  
-------------------------------
--  New table tb_r_sequence  --
-------------------------------
-- Create table
create table TB_R_SEQUENCE
(
  tbl_name               VARCHAR2(50) not null,
  sequence_next_hi_value NUMBER not null
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
-- Create/Recreate primary, unique and foreign key constraints 
alter table TB_R_SEQUENCE
  add constraint PK_SEQ primary key (TBL_NAME)
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
