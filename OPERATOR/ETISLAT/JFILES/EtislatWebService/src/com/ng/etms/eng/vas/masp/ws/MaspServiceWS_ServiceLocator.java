/**
 * MaspServiceWS_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ng.etms.eng.vas.masp.ws;

public class MaspServiceWS_ServiceLocator extends org.apache.axis.client.Service implements com.ng.etms.eng.vas.masp.ws.MaspServiceWS_Service {

    public MaspServiceWS_ServiceLocator() {
    }


    public MaspServiceWS_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MaspServiceWS_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MaspServiceWSPort
    private java.lang.String MaspServiceWSPort_address = "http://10.161.6.10:8280/masp-ejb/MaspServiceWS/MaspServiceWS";

    public java.lang.String getMaspServiceWSPortAddress() {
        return MaspServiceWSPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MaspServiceWSPortWSDDServiceName = "MaspServiceWSPort";

    public java.lang.String getMaspServiceWSPortWSDDServiceName() {
        return MaspServiceWSPortWSDDServiceName;
    }

    public void setMaspServiceWSPortWSDDServiceName(java.lang.String name) {
        MaspServiceWSPortWSDDServiceName = name;
    }

    public com.ng.etms.eng.vas.masp.ws.MaspServiceWS_PortType getMaspServiceWSPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MaspServiceWSPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMaspServiceWSPort(endpoint);
    }

    public com.ng.etms.eng.vas.masp.ws.MaspServiceWS_PortType getMaspServiceWSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.ng.etms.eng.vas.masp.ws.MaspServiceWSSoapBindingStub _stub = new com.ng.etms.eng.vas.masp.ws.MaspServiceWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getMaspServiceWSPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMaspServiceWSPortEndpointAddress(java.lang.String address) {
        MaspServiceWSPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.ng.etms.eng.vas.masp.ws.MaspServiceWS_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.ng.etms.eng.vas.masp.ws.MaspServiceWSSoapBindingStub _stub = new com.ng.etms.eng.vas.masp.ws.MaspServiceWSSoapBindingStub(new java.net.URL(MaspServiceWSPort_address), this);
                _stub.setPortName(getMaspServiceWSPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MaspServiceWSPort".equals(inputPortName)) {
            return getMaspServiceWSPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://ws.masp.vas.eng.etms.ng.com/", "MaspServiceWS");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://ws.masp.vas.eng.etms.ng.com/", "MaspServiceWSPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MaspServiceWSPort".equals(portName)) {
            setMaspServiceWSPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
