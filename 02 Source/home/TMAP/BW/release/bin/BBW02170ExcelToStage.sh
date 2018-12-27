#!/bin/bash
#************************************************************************
#* Program History:                                                     *
#*                                                                      *
#* Client Name  : TDEM                                                  *
#* System       : GWRDS : Getsudo Worksheet Rundown System              *
#* Module       : Calendar                                              *
#* Program ID   : BBW02170ExcelToStage.sh                               *
#* Program Name : Convert Excel to staging and target table             *
#* Description  : Convert Excel to staging and target table             *
#*                                                                      *
#* Environment  : Sun Solaris ver 10 / Oracle 11g                       *
#* Language     : Shell Scripting Language                              *
#* Author       : Thanapon T .                                          *
#* Version      : 1.0                                                   *
#* Creation Date: October 06, 2017                                       *
#*                                                                      *
#* Call Interface :                                                     *
#*                                                                      *
#* Input Parameters :                                                   *
#*    1. Getsudo Month                                                  *
#*    2. Timing                                                         *
#*    3. Unit Plant                                                     *
#*    4. Parent Line                                                    *
#*    5. Upload File name                                               *
#*    6. User Login                                                     *
#*    7. Apl Id                                                         *
#* Update History  Refix Date  Person in Charge      Description        *
#*                 2017/10/06  Thanapon              Creation           *
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
	echo "  PARAM1 = Getsudo Month"
	echo "  PARAM2 = Timing"
	echo "  PARAM3 = Unit Plant"
	echo "  PARAM4 = Parent Line"
	echo "  PARAM5 = Upload File name"
	echo "  PARAM6 = User Login"
	echo "  PARAM7 = Apl Id"
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

CLASS_NAME=th.co.toyota.bw0.batch.main.CBW02170ExcelToStage

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
  echo "${YMDHMS} :${CMD} : CBW02170ExcelToStage SUCCESS "
  rm -f ${ERRLOGFIL}
  echo "${ERRLOGFIL} removed .."
  exit ${status}
elif [ $status -eq 2 ] ; then
  echo "${YMDHMS} :${CMD} : CBW02170ExcelToStage WARNING : Status= ${status}" | tee -a ${ERRLOGFIL}
  exit ${EXIT_WARNING}
else       
  echo "${YMDHMS} :${CMD} : CBW02170ExcelToStage ERROR : Status= ${status}" | tee -a ${ERRLOGFIL}
  exit ${EXIT_ERROR}       
fi           

# ========================================================================

echo "$YMDHMS : $CMD : Convert Excel to staging and target table Ended "