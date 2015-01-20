var1=$(mysql -h database.master -u billing -pbilling master_db -B --disable-column-names -e "select etislat_hsep.EtisAlert(10)")
echo "value $var1"

if [ "$var1" == "ERROR" ]; then
#!/bin/bash
process=`pgrep -f "EtislatReceiverTF_TRANS" | wc -l`
if [ $process -ge 1 ]; then
echo It is not safe to kill this process...
else
pkill -f EtislatReceiverTF_TRANS
sh /home/ivr/jfiles/Alert_System/conf/RunScripts/EtislatReceiverTF_TRANS.sh
echo $1 process killed...
fi
        echo "You have Error!"
      elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=8586968482&shortcode=590900&msgtype=plaintext&msg=Etislat Application issue PLease restart Etislat TF_TRANS "
 elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=8586968481&shortcode=590900&msgtype=plaintext&msg=Etislat Application issue PLease restart Etislat TF_TRANS "
elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=8586968485&shortcode=590900&msgtype=plaintext&msg=Etislat Application issue PLease restart Etislat TF_TRANS"
elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=8587800614&shortcode=590900&msgtype=plaintext&msg=Etislat Application issue PLease restart Etislat TF_TRANS"
elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=8588838347&shortcode=590900&msgtype=plaintext&msg=Etislat Application issue PLease restart Etislat TF_TRANS"
elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=7838884633&shortcode=590900&msgtype=plaintext&msg=Etislat Application issue PLease restart Etislat TF_TRANS"
elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=9582220348&shortcode=590900&msgtype=plaintext&msg=Etislat Application issue PLease restart Etislat TF_TRANS"
else
        echo "SUCCESS $var1"
fi

