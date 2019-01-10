#!/bin/bash
#************************************************************************
#* Program History:                                                     *
#*                                                                      *
#* Client Name  : TDEM                                                  *
#* System       : GWRDS : Getsudo Worksheet Rundown System              *
#* Module       : Calendar                                              *
#* Program ID   : BBW04220ExcelToStage.sh                               *
#* Program Name : Convert Excel to staging and target table             *
#* Description  : Convert Excel to staging and target table             *
#*                                                                      *
#* Environment  : Sun Solaris ver 10 / Oracle 11g                       *
#* Language     : Shell Scripting Language                              *
#* Author       : Thanawut T .                                          *
#* Version      : 1.0                                                   *
#* Creation Date: August 30, 2017                                       *
#*                                                                      *
#* Call Interface :                                                     *
#*                                                                      *
#* Input Parameters :                                                   *
#*    1. Version                                                		*
#*    2. Getsudo Month                                                  *
#*    3. Timing                                                         *
#*    4. File name                                                      *
#*    5. User ID                                                        *
#*    6. Apl Id (Vehicle Palnt)                                         *
#*    7. Apl Id (Unit Plant)                                            *
#* Update History  Refix Date  Person in Charge      Description        *
#*                 2017/08/22  Thanapon              Creation           *
#* Copyright(C) 2010-TOYOTA Motor Asia Pacific. All Rights Reserved.    *
#************************************************************************
# ==================================================================
## Common Environment Setting & Function Load
# ==================================================================

#if [ ! "${ORACLE_HOME}" ];then
#    source $HOME/infra.env
#fi
if [ ! "${DB_SID}" ];then
    source $HOME/std.env
#   source $HOME/apl.env
fi

# =====================================================================
CMD=`basename $0`
CNAME=`echo $CMD | cut -d. -f1`
umask 000

#----------------------------------------------------------------------
# Error Logging File
TIMESTAMP=`date "+%Y%m%d%H%M%S"`
ERRLOGFIL=${APP_LOG_DIR}/${CNAME}${TIMESTAMP}.log

YMDHMS=`date "+%Y/%m/%d %H:%M:%S"`
echo "${YMDHMS} :${CMD} : Starting program " | tee -a ${ERRLOGFIL}

# ==================================================================
if [ $# -lt 7 ] ; then
   echo "Usage : $0 PARAM1 PARAM2 PARAM3 ... PARAM7"
   echo "Where : " 
   echo "  PARAM1 = Version"
   echo "  PARAM2 = Getsudo Month"
   echo "  PARAM3 = Timing"
   echo "  PARAM4 = File name"
   echo "  PARAM5 = User ID"
   echo "  PARAM6 = Apl Id (Vehicle Palnt)"
   echo "  PARAM7 = Apl Id (Unit Plant)"
   exit ${EXIT_ERROR}
else
   PARAM1=${1}
   PARAM2=${2}
   PARAM3=${3}
   PARAM4=${4}
   PARAM5=${5}
   PARAM6=${6}
   PARAM7=${7}
fi

CLASS_NAME=th.co.toyota.bw0.batch.main.CBW04220ExcelToStage

echo "$YMDHMS : $CMD : Convert Excel to staging and target table begin"
echo "PARAM1 : ${PARAM1}"
echo "PARAM2 : ${PARAM2}"
echo "PARAM3 : ${PARAM3}"
echo "PARAM4 : ${PARAM4}"
echo "PARAM5 : ${PARAM5}"
echo "PARAM6 : ${PARAM6}"
echo "PARAM7 : ${PARAM7}"

DEBUG_OPTION=" "

java -Xmx1024m $DEBUG_OPTION -cp ${CLASSPATH} ${CLASS_NAME} ${PARAM1} ${PARAM2} ${PARAM3} ${PARAM4} ${PARAM5} ${PARAM6} ${PARAM7}
status=$?                            

#-------------------------------------------------------------------------
# ========================================================================
## Write Log File
# ========================================================================
if [ $status -eq 0 ] ; then
  echo "${YMDHMS} :${CMD} : CBW04220ExcelToStage SUCCESS "
  rm -f ${ERRLOGFIL}
  echo "${ERRLOGFIL} removed .."
  exit ${status}
elif [ $status -eq 2 ] ; then
  echo "${YMDHMS} :${CMD} : CBW04220ExcelToStage WARNING : Status= ${status}" | tee -a ${ERRLOGFIL}
  exit ${EXIT_WARNING}
else       
  echo "${YMDHMS} :${CMD} : CBW04220ExcelToStage ERROR : Status= ${status}" | tee -a ${ERRLOGFIL}
  exit ${EXIT_ERROR}       
fi           

# ========================================================================

echo "$YMDHMS : $CMD : Convert Excel to staging and target table Ended "