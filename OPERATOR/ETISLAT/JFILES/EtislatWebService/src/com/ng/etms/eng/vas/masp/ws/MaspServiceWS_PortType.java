/**
 * MaspServiceWS_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ng.etms.eng.vas.masp.ws;

public interface MaspServiceWS_PortType extends java.rmi.Remote {
    public java.lang.String deactivateMaspSubscriberService(com.ng.etms.eng.vas.masp.ws.DeactivationData data) throws java.rmi.RemoteException;
    public java.lang.String deactivateAllMaspSubscriberServiceByMsisdn(com.ng.etms.eng.vas.masp.ws.DeactivationData data) throws java.rmi.RemoteException;
    public com.ng.etms.eng.vas.masp.ws.MaspEventResponseData createMaspEventInboundSummary(com.ng.etms.eng.vas.masp.ws.MaspEventSummaryData data) throws java.rmi.RemoteException;
}
