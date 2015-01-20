
interface CIMD_Constants
{
    int GSM2UC_ST_LENGTH 			= 43;
    int GSM2UC_CT_LENGTH			= 128;
    int UC2GSM_CT_LENGTH	        = 256;
    int USERDATA_LENGTH 			= 160;
    int BINDATA_LENGTH				= 140;
    int MAX_DATA_LENGTH 	        = 480;
    int MIN_STATUS_REP_VAL			= 0;
    int MAX_STATUS_REP_VAL 			= 63;
    int NO_ERROR        	        = -100;
    int LOGIN  						= 0x3031; // = 01
    int LOGOUT 						= 0x3032; // = 02
    int SUBMIT 						= 0x3033; // = 03
    int ENQ_STATUS 					= 0x3034; // = 04
	int DEL_REQUEST 				= 0x3035; // = 05
    int DEL_MESG_RESP 				= 0x3730; // = 70
    int DEL_STATUS_RESP				= 0x3733; // = 73
    int DEL_MESG 					= 0x3230; // = 20
    int DEL_STATUS 					= 0x3233; // = 23
    int LOGIN_RESP					= 0x3531; // = 51
    int LOGOUT_RESP					= 0x3532; // = 52
    int SUBMIT_RESP					= 0x3533; // = 53
    int ENQ_STATUS_RESP				= 0x3534; // = 54
    int ALIVE_RESP					= 0x3930; // = 90
    int GEN_ERROR_RESP 				= 0x3938; // = 98
    int NACK 						= 0x3939; // = 99
    int ALIVE 						= 0x3430; // = 40
    int NULL 						= 0x00;
    int START_OF_TEXT   			= 0x02; // = 2
    int END_OF_TEXT    				= 0x03; // = 3
    int TAB 						= 0x09; // = 9
    int COLON 						= 0x3A; // = 58
    int USER_ID 	 				= 0x303130;
    int PASSWD 		 				= 0x303131;
    int DEST_ADDRESS			 	= 0x303231;
    int ORIG_ADDRESS			 	= 0x303233;
    int DATA_CODING	 				= 0x303330;
    int USER_DATA_HEADER		 	= 0x303332;
    int USER_DATA 	 				= 0x303333;
    int USER_DATA_BIN 			 	= 0x303334;
    int VAL_PERIOD_REL 			 	= 0x303530;
    int VAL_PERIOD_ABS 			 	= 0x303531;
    int PROT_ID 					= 0x303532;
    int DEL_TIME_REL 				= 0x303533;
    int DEL_TIME_ABS 				= 0x303534;
    int REPLY_PATH 					= 0x303535;
    int STATUS_REPORT_REQ			= 0x303536;
    int CANCEL_ENABLED				= 0x303538;
    int CANCEL_MODE					= 0x303539;
    int SC_TIMESTAMP 				= 0x303630;
    int STATUS_CODE 				= 0x303631;
    int DISCHARGE_TIME 				= 0x303633;
    int TARIFF_CLASS 				= 0x303634;
    int SERVICE_DESCR 				= 0x303635;
    int MESG_COUNT 					= 0x303636;
    int PRIORITY 					= 0x303637;
    int DEL_REQ_MODE 				= 0x303638;
    int GET_PARAM 					= 0x353030;
    int SMSC_TIME 					= 0x353031;
    int ERROR_CODE 					= 0x393030;
    int ERROR_TEXT 					= 0x393031;
    int REQ_TEMP_ERROR 				= 1;
    int REQ_VAL_PERIOD_EXP			= 2;
    int REQ_DEL_FAILED 				= 4;
    int REQ_DEL_SUCCESSFUL			= 8;
    int REQ_MESG_CANCELLED			= 16;
    int REQ_MESG_DELETED 			= 32;
    int REP_IN_PROGRESS				= 0x31;
    int REP_VAL_PERIOD_EXP			= 0x32;
    int REP_DEL_FAILED 				= 0x33;
    int REP_DEL_SUCCESSFUL			= 0x34;
    int REP_NO_RESPONSE 			= 0x35;
    int REP_LAST_NO_RESPONSE		= 0x36;
    int REP_MESSAGE_CANCELLED		= 0x37;
    int REP_MESSAGE_DELETED 		= 0x38;
    int REP_MESSAGE_DEL_BY_CANCEL	= 0x39;
    byte[] BIN_DATA_CODING_SCHEME 	= {0x32, 0x34, 0x35}; // coding scheme is 245 for binary
    int ASCII_PLUS 					= 0x2B;
    int ASCII_MIN 					= 0x2D;
    int ASCII_ZERO 					= 0x30;
    int ASCII_NINE 					= 0x39;
}

