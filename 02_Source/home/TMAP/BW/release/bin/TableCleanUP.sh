#!/bin/bash
#************************************************************************
#* Program History:                                                     *
#*                                                                      *
#* Client Name  : TMAP-EM                                               *
#* System       : Getsudo Worksheet Rundown System (GWRDS)              *
#* Module       : Data Purging   Sample Program                         *
#* Program ID   : TableCleanUP.sh                                       *
#* Program Name : DB Table data Purging                                 *
#* Description  : DB Table data Purging                                 *
#*                                                                      *
#* Environment  : Redhat Enterprise Linux Server 7.1 64 bit /           *
#*                Oracle standard Edition (12c)                         *
#* Language     : Shell Scripting Language                              *
#* Author       : Y.Asaka                                               *
#* Version      : 1.00                                                  *
#* Creation Date: 2007/01/20                                            *
#*                                                                      *
#* Call Interface :                                                     *
#*     TableCleanUp [-i] [-m Month ]                                    *
#* Input Parameters :                                                   *
#*     -m  ; Force to Use Keeping Period Month                          *
#*           You can use as the Table CleanUP                           *
#*     -i  ; Initilize Parking / Result Tables                          *
#*                                                                      *
#* Update History  Refix Date  Person in Charge      Description        *
#* 2007.01.20 Y.Asaka UPD: Standarize the coding style                  *
#* 2007.01.26 Y.Asaka UPD: Set Tables for FISM                          *
#* 2007.01.29 Y.Asaka UPD: Set APL_ID & Put Process Information         *
#* 2007.03.30 R.Patricio/O.Wachawong UPD : Finalize tables              *
#* 2010.04.21 Y.Asaka UPD: Correct "DB Stastic Update"                  *
#* 2010.04.21 Y.Asaka Add: Index Rebuild  as the optional               *
#* Copyright(C) 2006-TOYOTA Motor Corporation. All Rights Reserved.     *
#************************************************************************
# ------------------------------------------------------------------
set -x
# ==================================================================
## Common Enviroment Setting
# ==================================================================
if [ ! "${ORACLE_HOME}" ];then
    source $HOME/infra.env
fi
if [ ! "${DB_SID}" ];then
    source $HOME/std.env
    source $HOME/apl.env
fi
#----------------------------------------------------------------------
CMD=`basename $0`
CNAME=`echo $CMD | cut -d. -f1`
umask 000

# ==================================================================
V_APL_ID='0000000000'

# ==================================================================
# Function to write directly to the TB_L_LOGGER
# usage : LogDetailIns  V_STATUS V_MESSAGE_TYPE V_MESSAGE
# ==================================================================
LogDetailIns () {
V_MODULE_ID=BW0Z
V_FUNCTION_ID=BBW0Z99
V_USERCRE=SYSTEM
V_MESSAGE_TYPE=$3
V_MESSAGE=$4
V_SEQ_NO=$1
V_MESSAGE_CODE=MBW00003AINF
V_STATUS=$2

sqlplus -s  ${DB_USER}/${DB_PASSWD}@${DB_SID} << SQLLOGEOF
INSERT INTO TB_L_LOGGER \
(V_MODULE_ID,V_FUNCTION_ID,V_USERCRE,V_MESSAGE_TYPE,V_MESSAGE,D_HODTCRE,N_SEQ_NO,V_MESSAGE_CODE,V_STATUS,V_APL_ID ) \
VALUES ('${V_MODULE_ID}','${V_FUNCTION_ID}','${V_USERCRE}','${V_MESSAGE_TYPE}','${V_MESSAGE}',SYSDATE,\
${V_SEQ_NO},'${V_MESSAGE_CODE}','${V_STATUS}','${V_APL_ID}');
exit;
SQLLOGEOF
}

#----------------------------------------------------------------------
# Error Logging File
TIMESTAMP=`date "+%Y%m%d%H%M%S"`
ERRLOGFIL=${APP_LOG_DIR}/${CNAME}${TIMESTAMP}.log

CURRENT_GETSUDO_MONTH=
#####################################
# Verify and parse input parameters #
#####################################
if [ $# -eq 1 ] ; then
   echo "Parameter passed is 1"
   CURRENT_GETSUDO_MONTH=$1
fi

echo "Usage : ${CMD} CURRENT_GETSUDO_MONTH = ${CURRENT_GETSUDO_MONTH} "

# ==================================================================
## Mandatory keep data
# ==================================================================
KEEPMONTH_LOG=12
KEEPMONTH_TRANS12=12

sqlplus -s  ${DB_USER}/${DB_PASSWD}@${DB_SID} << SQLDELETEEOF
SET LINESIZE 140
SET PAGESIZE 0

DELETE FROM TB_L_LOGGER WHERE V_APL_ID=${V_APL_ID};

EXIT SQL.SQLCODE ;
SQLDELETEEOF
status0=$?;

# -----------------------------------------------------------------------------  
#  Start message
# -----------------------------------------------------------------------------  
YMDHMS=`date "+%Y/%m/%d %H:%M:%S"`
echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} Table Purging Started"
echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} Table Purging Started"  > ${ERRLOGFIL}
#LogDetailIns 1 "S" "I" "${CMD} : SystemID=${SYSTEM_ID} Table Purging Started"

# -----------------------------------------------------------------------------  
## Data Purging ; Delete Data based on Keeping Period
# -----------------------------------------------------------------------------  
sqlplus -s  ${DB_USER}/${DB_PASSWD}@${DB_SID} << SQLDELETEEOF
SET LINESIZE 140
SET PAGESIZE 0

DELETE FROM TB_L_LOGGER                 WHERE D_HODTCRE < ADD_MONTHS(SYSDATE,-${KEEPMONTH_LOG}) ;

DELETE FROM TB_M_STD_STOCK 				WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_M_UNIT_PLANT_CAPA		WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_KAIKIENG_H				WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_KAIKIENG_D				WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_WS_MULTI_SOURCE_UNITS	WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_WS_PACKING_MOVEMENT	WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_RUNDOWN_KOMPO_STS		WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_PAMS_RUNDOWN			WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_KOMPO					WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_KOMPO_VAN_RESULT		WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_CAPA_RESULT			WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_R_MULTI_LINE_SPLITING	WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_L_UPLOAD_STS				WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});
DELETE FROM TB_L_UPLOAD_DETAIL			WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') <= ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});

EXIT SQL.SQLCODE ;
SQLDELETEEOF
status=$?

#echo "DELETE FROM TB_R_KAIKIENG_H WHERE TO_DATE(GETSUDO_MONTH,'Mon-YY') < ADD_MONTHS(TO_DATE(NVL('${CURRENT_GETSUDO_MONTH}',to_char(SYSDATE,'Mon-YY')),'Mon-YY'),-${KEEPMONTH_TRANS12});"

# -----------------------------------------------------------------------------  
#  End Purge message
# -----------------------------------------------------------------------------  
YMDHMS=`date "+%Y/%m/%d %H:%M:%S"`
if [ $status -eq 0 ] ; then
  echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} Table Purging SUCCESS "
  echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} Table Purging SUCCESS " >> ${ERRLOGFIL}
  #LogDetailIns 2 "E" "I" "${CMD} : SystemID=${SYSTEM_ID} Table Purging SUCCESS"
else
  echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} Table Purging ERROR : Status= ${status}"
  echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} Table Purging ERROR : Status= ${status}" >> ${ERRLOGFIL}
  #LogDetailIns 2 "E" "E" "${CMD} : SystemID=${SYSTEM_ID} Table Purging ERROR"
fi

# -----------------------------------------------------------------------------  
#  Start Rebuild Index and Analize
# -----------------------------------------------------------------------------  
YMDHMS= `date "+%Y/%m/%d %H:%M:%S"`
echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} Index and Table Rebuild and Analyze Started "  >> ${ERRLOGFIL}
#LogDetailIns "S" "I" "${CMD} : SystemID=${SYSTEM_ID} Index and Table rebuild and analyze Started ${YMDHMS}"

sqlplus -s  ${DB_USER}/${DB_PASSWD}@${DB_SID} << SQLEOF
SET LINESIZE 140
SET PAGESIZE 0
DECLARE
BEGIN
    /*  Rebuild Index & Analize  */
    FOR CUR IN (SELECT INDEX_NAME FROM USER_INDEXES) LOOP
    BEGIN
        EXECUTE IMMEDIATE 'ALTER INDEX "' ||CUR.INDEX_NAME||'" REBUILD COALESCE ';
    EXCEPTION
      WHEN OTHERS THEN
         NULL;
    END;
    END LOOP;
	
	/*  Table Analize  */
    FOR CUR IN (SELECT TABLE_NAME FROM USER_TABLES) LOOP
        EXECUTE IMMEDIATE 'ANALYZE TABLE ' ||CUR.TABLE_NAME||' ESTIMATE STATISTICS SAMPLE 30 PERCENT';
    END LOOP;

END;
/
commit;
EXIT SQL.SQLCODE ;
SQLEOF
statusIndexing=$?
YMDHMS=`date "+%Y/%m/%d %H:%M:%S"`
echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} Index and Table Rebuild and Analyze Finished : Status=${statusIndexing} "  >> ${ERRLOGFIL}
#LogDetailIns "E" "I" "${CMD} : SystemID=${SYSTEM_ID} Index and Table rebuild and analyze Finished ${YMDHMS},status=${statusIndexing} "

STAT_DATE=`date "+%Y/%m/%d %H:%M:%S"`
echo "${STAT_DATE} :${CMD} : SystemID=${SYSTEM_ID} Schema stats Started "  >> ${ERRLOGFIL}
#LogDetailIns "S" "I" "${CMD} : SystemID=${SYSTEM_ID} Schema stats start  ${STAT_DATE}"
sqlplus -s  ${DB_USER}/${DB_PASSWD}@${DB_SID} << SQLSTATSEOF
SET LINESIZE 140
SET PAGESIZE 0
exec dbms_stats.gather_schema_stats('${DB_USER}', estimate_percent => 50, method_opt => 'for all columns size auto',cascade => TRUE);
commit;
EXIT SQL.SQLCODE ;
SQLSTATSEOF
statusSchemaUpdate=$?
STAT_DATE=`date "+%Y/%m/%d %H:%M:%S"`
echo "${STAT_DATE} :${CMD} : SystemID=${SYSTEM_ID} Schema stats Update Finished : Status=${statusSchemaUpdate} "  >> ${ERRLOGFIL}
#LogDetailIns "E" "I" "${CMD} : SystemID=${SYSTEM_ID} Schema stats Finished  ${STAT_DATE}  " 
# -----------------------------------------------------------------------------  
#  End Rebuild Index and Analize
# -----------------------------------------------------------------------------  

# -----------------------------------------------------------------------------  
#  End Table Clean Up
# -----------------------------------------------------------------------------  
if [ $status -eq 0 ] ; then
  exit ${EXIT_SUCCESS}
else
  exit ${EXIT_ERROR}
fi