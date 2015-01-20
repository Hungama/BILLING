echo "Process is going to killed..."
kill -9 `ps -aef | grep 'EtislatReceiverTF_TRANS' | grep -v grep | awk '{print $2}'`
kill -9 `ps -aef | grep 'EtislatReceiverTF_REC' | grep -v grep | awk '{print $2}'`
echo "Process has been killed......"
elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=7838102430&shortcode=590999&msgtype=plaintext&msg=ETISLATTF%20TRANS%20and%20REC%20code%20has%20been%20stopped%20for%20restart"
elinks -dump "http://192.168.100.212:1111/HMXP/push.jsp?smppgateway=HMXP&msisdn=8373917355&shortcode=590999&msgtype=plaintext&msg=ETISLATTF%20TRANS%20and%20REC%20code%20has%20been%20stopped%20for%20restart"
