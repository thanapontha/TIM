#!/bin/bash
#************************************************************************
#* Program History:                                                     *
#*                                                                      *
#* Client Name  : TDEM                                                  *
#* System       : Getsudo Worksheet Rundown System (GWRDS)              *
#* Module       : Aplication File Purging                               *
#* Program ID   : FileCleanUp.sh                                        *
#* Program Name : File clean Up                                         *
#* Description  : File clean Up                                         *
#*                                                                      *
#* Environment  : Redhat Enterprise Linux Server 7.1 64 bit / Oracle Client standard Edition (12c)         *
#* Language     : Shell Scripting Language                              *
#* Author       : Y.Asaka                                               *
#* Version      : 1.00                                                  *
#* Creation Date: 2007/01/19                                            *
#*                                                                      *
#* Call Interface :                                                     *
#*    FileCleanUp.sh  COMPANY_CD [ -d Days ]  By Cron or TWS            *
#*                                                                      *
#* Input Parameters :                                                   *
#*    -d  Days  ; Force to Use Keeping Days                             *
#* Update History   Refix Date  Person in Charge      Description       *
#* 2017.11.16                   FTH)Thanapon          New Creation      *
#* Copyright(C) 2007-TOYOTA Motor Corporation. All Rights Reserved.     *
#************************************************************************
# ------------------------------------------------------------------
## set -x
# ==================================================================
## Common Enviroment Setting
# ==================================================================
#if [ ! "${ORACLE_HOME}" ];then
#    source $HOME/infra.env
#fi
if [ ! "${DB_SID}" ];then
    source $HOME/std.env
#   source $HOME/apl.env
fi

#----------------------------------------------------------------------
CMD=`basename $0`
CNAME=`echo $CMD | cut -d. -f1`
umask 000

#----------------------------------------------------------------------
# Error Logging File
TIMESTAMP=`date "+%Y%m%d%H%M%S"`
ERRLOGFIL=${APP_LOG_DIR}/${CNAME}${TIMESTAMP}.log

# ==================================================================
## Option Check keep day
# ==================================================================
DOWNLOAD_KEEPDAYS=35
UPLOAD_TMP_KEEPDAYS=35
ARCHIVE_KEEPDAYS=35
FILEKEEPDAYS=35
LOGKEEPDAYS=180

#####################################
# Verify and parse input parameters #
#####################################
if [ $# -lt 1 ] ; then
   echo "Usage : ${CMD} COMPANY_CD "
   echo "Or"
   echo "Usage : ${CMD} COMPANY_CD -D DAYS "
   exit ${EXIT_ERROR}
elif [ $# -eq 1 ] ; then
   echo "Parameter passed is 1"
   COMPANY_CD=$1
elif [ $# -eq 3 ] ; then
   echo "Parameter passed is 3"
   COMPANY_CD=$1
   if [ "$2" = "-d" ] || [ "$2" = "-D" ] ; then
	  DOWNLOAD_KEEPDAYS=$3
	  UPLOAD_TMP_KEEPDAYS=$3
	  ARCHIVE_KEEPDAYS=$3
      FILEKEEPDAYS=$3
      LOGKEEPDAYS=$3
      shift
   else
       echo "Usage : ${CMD} COMPANY_CD -D DAYS "
       exit ${EXIT_ERROR}
   fi
else
   echo "Usage : ${CMD} COMPANY_CD "
   echo "Or"
   echo "Usage : ${CMD} COMPANY_CD -D DAYS "
   exit ${EXIT_ERROR}
fi

DATA_DIR=${APP_SHARED}/${COMPANY_CD}/data
UPLOAD_TMP_DIR=${APP_SHARED}/tmp

# ==================================================================
## Basic Sending / Receiving Directory
# ==================================================================
INPUT_DIR=${DATA_DIR}/input
OUTPUT_DIR=${DATA_DIR}/output
REJECT_DIR=${DATA_DIR}/reject
UPLOAD_DIR=${DATA_DIR}/upload
DOWNLOAD_DIR=${DATA_DIR}/download

# ==================================================================
## Archive	Directory
# ==================================================================
ARCHIVE_DIR=${DATA_DIR}/archive
ARCHIVE_ERROR_DIR=${ARCHIVE_DIR}/error
ARCHIVE_OUTPUT_DIR=${ARCHIVE_DIR}/output
ARCHIVE_SUCCESS_DIR=${ARCHIVE_DIR}/success
ARCHIVE_WARNING_DIR=${ARCHIVE_DIR}/warning

# =========================================================================
# Start Message output
YMDHMS=`date "+%Y/%m/%d %H:%M:%S"`
echo "$YMDHMS : $CMD : SystemID=${SYSTEM_ID} File ClenUp Started"
echo "$YMDHMS : $CMD : SystemID=${SYSTEM_ID} File ClenUp Started" > ${ERRLOGFIL}

#========================================
## Prefix file name for remove
find $ARCHIVE_DIR  -type f -name '*.xlsx' -mtime +${ARCHIVE_KEEPDAYS} -print -exec rm -f \{\} \;
find $ARCHIVE_DIR  -type f -name '*.xls' -mtime +${ARCHIVE_KEEPDAYS} -print -exec rm -f \{\} \;
find $UPLOAD_TMP_DIR  -type f -name '*.xlsx' -mtime +${UPLOAD_TMP_KEEPDAYS} -print -exec rm -f \{\} \;
find $UPLOAD_TMP_DIR  -type f -name '*.xls' -mtime +${UPLOAD_TMP_KEEPDAYS} -print -exec rm -f \{\} \;

## Download function 
find $DOWNLOAD_DIR  -type f -name '*.xlsx' -mtime +${DOWNLOAD_KEEPDAYS} -print -exec rm -f \{\} \;
find $DOWNLOAD_DIR  -type f -name '*.xls' -mtime +${DOWNLOAD_KEEPDAYS} -print -exec rm -f \{\} \;

# ==================================================================
## Application Log Directory                 Mandatory for Cleaning
# ==================================================================
find $APP_LOG_DIR  -type f -mtime +${LOGKEEPDAYS} -print -exec rm -f \{\} \;

# =========================================================================
# End Message output
YMDHMS=`date "+%Y/%m/%d %H:%M:%S"`
echo "$YMDHMS : $CMD : SystemID=${SYSTEM_ID} File ClenUp Finished"
echo "${YMDHMS} :${CMD} : SystemID=${SYSTEM_ID} File ClenUp Finished" >> ${ERRLOGFIL}

exit 0
