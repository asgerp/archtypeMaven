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
 
// 
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5  
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>  
// Any modifications to this file will be lost upon recompilation of the source schema.  
// Generated on: 2012.04.17 at 09:07:48 PM MAGST  
// 
 
 
package oasis.names.tc.ebxml_regrep.xsd.lcm._3; 
 
import javax.xml.bind.annotation.XmlAccessType; 
import javax.xml.bind.annotation.XmlAccessorType; 
import javax.xml.bind.annotation.XmlAttribute; 
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlRootElement; 
import javax.xml.bind.annotation.XmlType; 
import oasis.names.tc.ebxml_regrep.xsd.rim._3.AdhocQueryType; 
import oasis.names.tc.ebxml_regrep.xsd.rim._3.ObjectRefListType; 
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryRequestType; 
 
 
/** 
 * <p>Java class for anonymous complex type. 
 *  
 * <p>The following schema fragment specifies the expected content contained within this class. 
 *  
 * <pre> 
 * &lt;complexType> 
 *   &lt;complexContent> 
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0}RegistryRequestType"> 
 *       &lt;sequence> 
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}AdhocQuery" minOccurs="0"/> 
 *         &lt;element ref="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}ObjectRefList" minOccurs="0"/> 
 *       &lt;/sequence> 
 *       &lt;attribute name="deletionScope" type="{urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0}referenceURI" default="urn:oasis:names:tc:ebxml-regrep:DeletionScopeType:DeleteAll" /> 
 *     &lt;/extension> 
 *   &lt;/complexContent> 
 * &lt;/complexType> 
 * </pre> 
 *  
 *  
 */ 
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlType(name = "", propOrder = { 
    "adhocQuery", 
    "objectRefList" 
}) 
@XmlRootElement(name = "RemoveObjectsRequest") 
public class RemoveObjectsRequest 
    extends RegistryRequestType 
{ 
 
    @XmlElement(name = "AdhocQuery", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0") 
    protected AdhocQueryType adhocQuery; 
    @XmlElement(name = "ObjectRefList", namespace = "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0") 
    protected ObjectRefListType objectRefList; 
    @XmlAttribute(name = "deletionScope") 
    protected String deletionScope; 
 
    /** 
     * Gets the value of the adhocQuery property. 
     *  
     * @return 
     *     possible object is 
     *     {@link AdhocQueryType } 
     *      
     */ 
    public AdhocQueryType getAdhocQuery() { 
        return adhocQuery; 
    } 
 
    /** 
     * Sets the value of the adhocQuery property. 
     *  
     * @param value 
     *     allowed object is 
     *     {@link AdhocQueryType } 
     *      
     */ 
    public void setAdhocQuery(AdhocQueryType value) { 
        this.adhocQuery = value; 
    } 
 
    /** 
     * Gets the value of the objectRefList property. 
     *  
     * @return 
     *     possible object is 
     *     {@link ObjectRefListType } 
     *      
     */ 
    public ObjectRefListType getObjectRefList() { 
        return objectRefList; 
    } 
 
    /** 
     * Sets the value of the objectRefList property. 
     *  
     * @param value 
     *     allowed object is 
     *     {@link ObjectRefListType } 
     *      
     */ 
    public void setObjectRefList(ObjectRefListType value) { 
        this.objectRefList = value; 
    } 
 
    /** 
     * Gets the value of the deletionScope property. 
     *  
     * @return 
     *     possible object is 
     *     {@link String } 
     *      
     */ 
    public String getDeletionScope() { 
        if (deletionScope == null) { 
            return "urn:oasis:names:tc:ebxml-regrep:DeletionScopeType:DeleteAll"; 
        } else { 
            return deletionScope; 
        } 
    } 
 
    /** 
     * Sets the value of the deletionScope property. 
     *  
     * @param value 
     *     allowed object is 
     *     {@link String } 
     *      
     */ 
    public void setDeletionScope(String value) { 
        this.deletionScope = value; 
    } 
 
} 