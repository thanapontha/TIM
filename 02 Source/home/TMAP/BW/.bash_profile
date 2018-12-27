# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
	. ~/.bashrc
fi

# User specific environment and startup programs

PATH=$PATH:$HOME/.local/bin:$HOME/bin

export PATH


#       This is the default standard profile provided to a user.
#       They are expected to edit it to meet their own needs.
#*******************************************************************
#* System    : TMAP Development Standard 
#* Module    : TMAP Development Standard  Shell Environment Setting
#* Function  : 
#      
#* OutLine   : 
#*   TMAP Development Standard  bashrc
#*
#* Call Interface :
#*  Call by OS at Login 
#*    Input  
#*    Output
#*
#* @author   : Y.Asaka
#* @version  : 1.0
# ------------------------------------------------------------------  
#* Revision History
#* 2006.12.15 Y.Asaka UPD:  Add Greeting
#* 2007.06.06 D.Nuestro UPD: Modify for 1.2B release
#*******************************************************************

#Server Classification
SERVER_CLASS="DB"   # APP or DB
SERVER_TYPE="PRODUCTION"    # DEV or PRODUCTION

#MAIL=/usr/mail/${LOGNAME:?}
. $HOME/infra.env
. $HOME/std.env

# FOR TWS Setting
MAESTRO_OUTPUT_STYLE=LONG
export MAESTRO_OUTPUT_STYLE
MAESTROCOLUMNS=180
export MAESTROCOLUMNS

# ==================================================================
# Display Greeting
# ==================================================================
echo " "
echo " =========================================================== "
echo "login at `/bin/date`"

HOUR=`/bin/date '+%H'`
if [ $HOUR -le 12 ] ; then
   echo "Good Morning "
   echo " GWRDS ${SERVER_CLASS}-${SERVER_TYPE} "
elif [ $HOUR -lt 18 ] ; then 
         echo "Good Afternoon "
         echo " GWRDS ${SERVER_CLASS}-${SERVER_TYPE} "
else 
         echo "Good Evening "
         echo " GWRDS ${SERVER_CLASS}-${SERVER_TYPE} "
fi
unset HOUR

#banner $SERVER_CLASS
#banner $SERVER_TYPE " ENV"
echo " =========================================================== "    
