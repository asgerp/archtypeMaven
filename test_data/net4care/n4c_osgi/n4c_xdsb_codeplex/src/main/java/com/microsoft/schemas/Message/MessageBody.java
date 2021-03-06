/*
 * Copyright 2012 Net4Care, www.net4care.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
 
/** 
 * MessageBody.java 
 * 
 * This file was auto-generated from WSDL 
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter. 
 */ 
 
package com.microsoft.schemas.Message; 
 
public class MessageBody  implements java.io.Serializable, org.apache.axis.encoding.AnyContentType { 
    private org.apache.axis.message.MessageElement [] _any; 
 
    public MessageBody() { 
    } 
 
    public MessageBody( 
           org.apache.axis.message.MessageElement [] _any) { 
           this._any = _any; 
    } 
 
 
    /** 
     * Gets the _any value for this MessageBody. 
     *  
     * @return _any 
     */ 
    public org.apache.axis.message.MessageElement [] get_any() { 
        return _any; 
    } 
 
 
    /** 
     * Sets the _any value for this MessageBody. 
     *  
     * @param _any 
     */ 
    public void set_any(org.apache.axis.message.MessageElement [] _any) { 
        this._any = _any; 
    } 
 
    private java.lang.Object __equalsCalc = null; 
    public synchronized boolean equals(java.lang.Object obj) { 
        if (!(obj instanceof MessageBody)) return false; 
        MessageBody other = (MessageBody) obj; 
        if (obj == null) return false; 
        if (this == obj) return true; 
        if (__equalsCalc != null) { 
            return (__equalsCalc == obj); 
        } 
        __equalsCalc = obj; 
        boolean _equals; 
        _equals = true &&  
            ((this._any==null && other.get_any()==null) ||  
             (this._any!=null && 
              java.util.Arrays.equals(this._any, other.get_any()))); 
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
        if (get_any() != null) { 
            for (int i=0; 
                 i<java.lang.reflect.Array.getLength(get_any()); 
                 i++) { 
                java.lang.Object obj = java.lang.reflect.Array.get(get_any(), i); 
                if (obj != null && 
                    !obj.getClass().isArray()) { 
                    _hashCode += obj.hashCode(); 
                } 
            } 
        } 
        __hashCodeCalc = false; 
        return _hashCode; 
    } 
 
    // Type metadata 
    private static org.apache.axis.description.TypeDesc typeDesc = 
        new org.apache.axis.description.TypeDesc(MessageBody.class, true); 
 
    static { 
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://schemas.microsoft.com/Message", "MessageBody")); 
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
