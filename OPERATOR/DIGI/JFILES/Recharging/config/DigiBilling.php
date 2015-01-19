<?php

class digisoaprequest {

  private $ServiceID;
  private $cp_id;
  private $price_code;

 public function __construct($ServiceID,$cp_id,$price_code) {

        $this->ServiceID=$ServiceID;
        $this->cp_id=$cp_id;
        $this->price_code=$price_code;
   }

   public function __destruct() {
         echo 'Digi Object was just destroyed ';
   }

}

$login_name="M7ygaSWeMHQ=";
$pwd='hun123';
$service_id='Q4sv4jPncR8IMiDLcsWQCA==';
$cp_id=7868;
$charge_party='S';
$source_mobtel=$_REQUEST['smsisdn'];
$destination_mobtel=$_REQUEST['dmsisdn'];
$delivery_channel='IVR';
$price_code='VAS220020';
$variableBundles=array('cpid'=>$cp_id,'serviceid'=>$service_id,'PriceSplit'=>$price_code);
$referenceCode = date('dmHis').'000022'.substr($source_mobtel,-4);

// start code to write the log
$logDir='/var/www/html/billing/log/digi_billing';
$logFile='log_'.date('Ymd').".txt";
$logPath=$logDir.$logFile;
$fp=fopen($logPath,'a+');
chmod($logPath,0777);
try {
	$digisoaprequestobj = new digisoaprequest($ServiceID,$cp_id,$price_code);
	$soapClient = new SoapClient ("http://192.100.86.204:8001/billing/services/SDPValidateBill?wsdl",array('login_name'=>$login_name,'password'=>$pwd));

	$param_array = array("variableBundles" =>array('value'=>$digisoaprequestobj),"charge_party"=>$charge_party,"source_mobtel"=>$source_mobtel,"destination_mobtel"=>$destination_mobtel,"delivery_channel"=>$delivery_channel);
	$response=$soapClient->__soapCall("ValidateAndBill",array_values($param_array));
	print_r($response);
	echo $referenceCode."#ok";
	fwrite($fp,$billingText."|".$response."|".date('his')."\r\n");
}
catch (Exception $e) {
	echo "Error!<br />";
	echo "<pre>";
	print_r($e);

}
fclose($fp);


/*

try
{
	$digisoaprequestobj = new digisoaprequest($login_name,$ServiceID,$cp_id,$price_code,$charge_party,$source_mobtel,$destination_mobtel,$delivery_channel);
	$soapClient = new SoapClient ("http://192.100.86.204:8001/billing/services/SDPValidateBill?wsdl");
	$response=$soapClient->__soapCall("ValidateAndBill",$digisoaprequestobj);
	print_r($response);
	echo $referenceCode."#ok";
}
catch (Exception $e)
{
	echo "Error!<br />";
	echo $referenceCode."#".$e->faultstring;
	fwrite($fp,$billingText."|".$e."|".date('his')."\r\n");
}
*/

?>
