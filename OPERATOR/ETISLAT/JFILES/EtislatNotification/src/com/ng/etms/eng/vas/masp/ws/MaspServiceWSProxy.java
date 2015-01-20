package com.ng.etms.eng.vas.masp.ws;

public class MaspServiceWSProxy implements com.ng.etms.eng.vas.masp.ws.MaspServiceWS_PortType {
  private String _endpoint = null;
  private com.ng.etms.eng.vas.masp.ws.MaspServiceWS_PortType maspServiceWS_PortType = null;
  
  public MaspServiceWSProxy() {
    _initMaspServiceWSProxy();
  }
  
  public MaspServiceWSProxy(String endpoint) {
    _endpoint = endpoint;
    _initMaspServiceWSProxy();
  }
  
  private void _initMaspServiceWSProxy() {
    try {
      maspServiceWS_PortType = (new com.ng.etms.eng.vas.masp.ws.MaspServiceWS_ServiceLocator()).getMaspServiceWSPort();
      if (maspServiceWS_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)maspServiceWS_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)maspServiceWS_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (maspServiceWS_PortType != null)
      ((javax.xml.rpc.Stub)maspServiceWS_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.ng.etms.eng.vas.masp.ws.MaspServiceWS_PortType getMaspServiceWS_PortType() {
    if (maspServiceWS_PortType == null)
      _initMaspServiceWSProxy();
    return maspServiceWS_PortType;
  }
  
  public java.lang.String deactivateMaspSubscriberService(com.ng.etms.eng.vas.masp.ws.DeactivationData data) throws java.rmi.RemoteException{
    if (maspServiceWS_PortType == null)
      _initMaspServiceWSProxy();
    return maspServiceWS_PortType.deactivateMaspSubscriberService(data);
  }
  
  public java.lang.String deactivateAllMaspSubscriberServiceByMsisdn(com.ng.etms.eng.vas.masp.ws.DeactivationData data) throws java.rmi.RemoteException{
    if (maspServiceWS_PortType == null)
      _initMaspServiceWSProxy();
    return maspServiceWS_PortType.deactivateAllMaspSubscriberServiceByMsisdn(data);
  }
  
  public com.ng.etms.eng.vas.masp.ws.MaspEventResponseData createMaspEventInboundSummary(com.ng.etms.eng.vas.masp.ws.MaspEventSummaryData data) throws java.rmi.RemoteException{
    if (maspServiceWS_PortType == null)
      _initMaspServiceWSProxy();
    return maspServiceWS_PortType.createMaspEventInboundSummary(data);
  }
  
  
}