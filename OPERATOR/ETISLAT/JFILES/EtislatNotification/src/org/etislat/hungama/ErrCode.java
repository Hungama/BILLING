package org.etislat.hungama;

public enum ErrCode 
{
	ERROR_MASP_001("Msisdn is mandatory for this operation"),
	ERROR_MASP_002("Event Timestamp is mandatory for this operation"),
	ERROR_MASP_003("Error occurred during conversion of event timestamp"),
	ERROR_MASP_004("Short-Code is mandatory for this operation"),
	ERROR_MASP_005("SMS message is mandatory for this operation"),
	ERROR_MASP_006("Event type is mandatory for this operation"),
	ERROR_MASP_007("Event Types must be {0,1,2,3,4,5}"),
	ERROR_MASP_008("Channel is mandatory for this operation"),
	ERROR_MASP_009("Channel code must be {SMS, IVR, USSD}"),
	ERROR_MASP_010("Vendor code is mandatory for this operation"),
	ERROR_MASP_011("Service code is mandatory for this operation"),
	ERROR_MASP_012("Billing-Code code is mandatory for this operation"),
	ERROR_MASP_013("Status code is mandatory for this operation"),
	ERROR_MASP_014("Channel code must be {A, I}"),
	ERROR_MASP_015("Channel code must be {SMS, IVR, USSD}");



	private String code="0";
	ErrCode(String  code)
	{
		this.code=code;
	}
	
	 
	 public String getCode()
	 {
		 return code;
	 }

	
};
