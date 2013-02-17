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
 
 
package oasis.names.tc.ebxml_regrep.xsd.query._3; 
 
import javax.xml.bind.annotation.XmlAccessType; 
import javax.xml.bind.annotation.XmlAccessorType; 
import javax.xml.bind.annotation.XmlAttribute; 
import javax.xml.bind.annotation.XmlType; 
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter; 
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter; 
 
 
/** 
 * <p>Java class for ResponseOptionType complex type. 
 *  
 * <p>The following schema fragment specifies the expected content contained within this class. 
 *  
 * <pre> 
 * &lt;complexType name="ResponseOptionType"> 
 *   &lt;complexContent> 
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"> 
 *       &lt;attribute name="returnType" default="RegistryObject"> 
 *         &lt;simpleType> 
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NCName"> 
 *             &lt;enumeration value="ObjectRef"/> 
 *             &lt;enumeration value="RegistryObject"/> 
 *             &lt;enumeration value="LeafClass"/> 
 *             &lt;enumeration value="LeafClassWithRepositoryItem"/> 
 *           &lt;/restriction> 
 *         &lt;/simpleType> 
 *       &lt;/attribute> 
 *       &lt;attribute name="returnComposedObjects" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /> 
 *     &lt;/restriction> 
 *   &lt;/complexContent> 
 * &lt;/complexType> 
 * </pre> 
 *  
 *  
 */ 
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlType(name = "ResponseOptionType") 
public class ResponseOptionType { 
 
    @XmlAttribute(name = "returnType") 
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class) 
    protected String returnType; 
    @XmlAttribute(name = "returnComposedObjects") 
    protected Boolean returnComposedObjects; 
 
    /** 
     * Gets the value of the returnType property. 
     *  
     * @return 
     *     possible object is 
     *     {@link String } 
     *      
     */ 
    public String getReturnType() { 
        if (returnType == null) { 
            return "RegistryObject"; 
        } else { 
            return returnType; 
        } 
    } 
 
    /** 
     * Sets the value of the returnType property. 
     *  
     * @param value 
     *     allowed object is 
     *     {@link String } 
     *      
     */ 
    public void setReturnType(String value) { 
        this.returnType = value; 
    } 
 
    /** 
     * Gets the value of the returnComposedObjects property. 
     *  
     * @return 
     *     possible object is 
     *     {@link Boolean } 
     *      
     */ 
    public boolean isReturnComposedObjects() { 
        if (returnComposedObjects == null) { 
            return false; 
        } else { 
            return returnComposedObjects; 
        } 
    } 
 
    /** 
     * Sets the value of the returnComposedObjects property. 
     *  
     * @param value 
     *     allowed object is 
     *     {@link Boolean } 
     *      
     */ 
    public void setReturnComposedObjects(Boolean value) { 
        this.returnComposedObjects = value; 
    } 
 
} 