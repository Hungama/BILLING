/**
 * MaspEventSummaryData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.ng.etms.eng.vas.masp.ws;

public class MaspEventSummaryData  implements java.io.Serializable {
    private java.lang.String billingCd;

    private java.lang.String channel;

    private java.lang.Long eventId;

    private java.lang.String eventTs;

    private java.lang.String eventType;

    private java.lang.String messageText;

    private java.lang.String msisdn;

    private java.lang.String serviceCd;

    private java.lang.String shortCode;

    private java.lang.String status;

    private java.lang.String vendorCd;

    public MaspEventSummaryData() {
    }

    public MaspEventSummaryData(
           java.lang.String billingCd,
           java.lang.String channel,
           java.lang.Long eventId,
           java.lang.String eventTs,
           java.lang.String eventType,
           java.lang.String messageText,
           java.lang.String msisdn,
           java.lang.String serviceCd,
           java.lang.String shortCode,
           java.lang.String status,
           java.lang.String vendorCd) {
           this.billingCd = billingCd;
           this.channel = channel;
           this.eventId = eventId;
           this.eventTs = eventTs;
           this.eventType = eventType;
           this.messageText = messageText;
           this.msisdn = msisdn;
           this.serviceCd = serviceCd;
           this.shortCode = shortCode;
           this.status = status;
           this.vendorCd = vendorCd;
    }


    /**
     * Gets the billingCd value for this MaspEventSummaryData.
     * 
     * @return billingCd
     */
    public java.lang.String getBillingCd() {
        return billingCd;
    }


    /**
     * Sets the billingCd value for this MaspEventSummaryData.
     * 
     * @param billingCd
     */
    public void setBillingCd(java.lang.String billingCd) {
        this.billingCd = billingCd;
    }


    /**
     * Gets the channel value for this MaspEventSummaryData.
     * 
     * @return channel
     */
    public java.lang.String getChannel() {
        return channel;
    }


    /**
     * Sets the channel value for this MaspEventSummaryData.
     * 
     * @param channel
     */
    public void setChannel(java.lang.String channel) {
        this.channel = channel;
    }


    /**
     * Gets the eventId value for this MaspEventSummaryData.
     * 
     * @return eventId
     */
    public java.lang.Long getEventId() {
        return eventId;
    }


    /**
     * Sets the eventId value for this MaspEventSummaryData.
     * 
     * @param eventId
     */
    public void setEventId(java.lang.Long eventId) {
        this.eventId = eventId;
    }


    /**
     * Gets the eventTs value for this MaspEventSummaryData.
     * 
     * @return eventTs
     */
    public java.lang.String getEventTs() {
        return eventTs;
    }


    /**
     * Sets the eventTs value for this MaspEventSummaryData.
     * 
     * @param eventTs
     */
    public void setEventTs(java.lang.String eventTs) {
        this.eventTs = eventTs;
    }


    /**
     * Gets the eventType value for this MaspEventSummaryData.
     * 
     * @return eventType
     */
    public java.lang.String getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this MaspEventSummaryData.
     * 
     * @param eventType
     */
    public void setEventType(java.lang.String eventType) {
        this.eventType = eventType;
    }


    /**
     * Gets the messageText value for this MaspEventSummaryData.
     * 
     * @return messageText
     */
    public java.lang.String getMessageText() {
        return messageText;
    }


    /**
     * Sets the messageText value for this MaspEventSummaryData.
     * 
     * @param messageText
     */
    public void setMessageText(java.lang.String messageText) {
        this.messageText = messageText;
    }


    /**
     * Gets the msisdn value for this MaspEventSummaryData.
     * 
     * @return msisdn
     */
    public java.lang.String getMsisdn() {
        return msisdn;
    }


    /**
     * Sets the msisdn value for this MaspEventSummaryData.
     * 
     * @param msisdn
     */
    public void setMsisdn(java.lang.String msisdn) {
        this.msisdn = msisdn;
    }


    /**
     * Gets the serviceCd value for this MaspEventSummaryData.
     * 
     * @return serviceCd
     */
    public java.lang.String getServiceCd() {
        return serviceCd;
    }


    /**
     * Sets the serviceCd value for this MaspEventSummaryData.
     * 
     * @param serviceCd
     */
    public void setServiceCd(java.lang.String serviceCd) {
        this.serviceCd = serviceCd;
    }


    /**
     * Gets the shortCode value for this MaspEventSummaryData.
     * 
     * @return shortCode
     */
    public java.lang.String getShortCode() {
        return shortCode;
    }


    /**
     * Sets the shortCode value for this MaspEventSummaryData.
     * 
     * @param shortCode
     */
    public void setShortCode(java.lang.String shortCode) {
        this.shortCode = shortCode;
    }


    /**
     * Gets the status value for this MaspEventSummaryData.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this MaspEventSummaryData.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the vendorCd value for this MaspEventSummaryData.
     * 
     * @return vendorCd
     */
    public java.lang.String getVendorCd() {
        return vendorCd;
    }


    /**
     * Sets the vendorCd value for this MaspEventSummaryData.
     * 
     * @param vendorCd
     */
    public void setVendorCd(java.lang.String vendorCd) {
        this.vendorCd = vendorCd;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MaspEventSummaryData)) return false;
        MaspEventSummaryData other = (MaspEventSummaryData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.billingCd==null && other.getBillingCd()==null) || 
             (this.billingCd!=null &&
              this.billingCd.equals(other.getBillingCd()))) &&
            ((this.channel==null && other.getChannel()==null) || 
             (this.channel!=null &&
              this.channel.equals(other.getChannel()))) &&
            ((this.eventId==null && other.getEventId()==null) || 
             (this.eventId!=null &&
              this.eventId.equals(other.getEventId()))) &&
            ((this.eventTs==null && other.getEventTs()==null) || 
             (this.eventTs!=null &&
              this.eventTs.equals(other.getEventTs()))) &&
            ((this.eventType==null && other.getEventType()==null) || 
             (this.eventType!=null &&
              this.eventType.equals(other.getEventType()))) &&
            ((this.messageText==null && other.getMessageText()==null) || 
             (this.messageText!=null &&
              this.messageText.equals(other.getMessageText()))) &&
            ((this.msisdn==null && other.getMsisdn()==null) || 
             (this.msisdn!=null &&
              this.msisdn.equals(other.getMsisdn()))) &&
            ((this.serviceCd==null && other.getServiceCd()==null) || 
             (this.serviceCd!=null &&
              this.serviceCd.equals(other.getServiceCd()))) &&
            ((this.shortCode==null && other.getShortCode()==null) || 
             (this.shortCode!=null &&
              this.shortCode.equals(other.getShortCode()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.vendorCd==null && other.getVendorCd()==null) || 
             (this.vendorCd!=null &&
              this.vendorCd.equals(other.getVendorCd())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getBillingCd() != null) {
            _hashCode += getBillingCd().hashCode();
        }
        if (getChannel() != null) {
            _hashCode += getChannel().hashCode();
        }
        if (getEventId() != null) {
            _hashCode += getEventId().hashCode();
        }
        if (getEventTs() != null) {
            _hashCode += getEventTs().hashCode();
        }
        if (getEventType() != null) {
            _hashCode += getEventType().hashCode();
        }
        if (getMessageText() != null) {
            _hashCode += getMessageText().hashCode();
        }
        if (getMsisdn() != null) {
            _hashCode += getMsisdn().hashCode();
        }
        if (getServiceCd() != null) {
            _hashCode += getServiceCd().hashCode();
        }
        if (getShortCode() != null) {
            _hashCode += getShortCode().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getVendorCd() != null) {
            _hashCode += getVendorCd().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MaspEventSummaryData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://ws.masp.vas.eng.etms.ng.com/", "maspEventSummaryData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("billingCd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "billingCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("channel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "channel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "eventId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventTs");
        elemField.setXmlName(new javax.xml.namespace.QName("", "eventTs"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "eventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("messageText");
        elemField.setXmlName(new javax.xml.namespace.QName("", "messageText"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("msisdn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "msisdn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceCd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "serviceCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "shortCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vendorCd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "vendorCd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
