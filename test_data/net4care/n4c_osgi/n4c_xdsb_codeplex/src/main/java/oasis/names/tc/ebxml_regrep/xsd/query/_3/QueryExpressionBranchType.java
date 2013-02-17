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
import javax.xml.bind.annotation.XmlElement; 
import javax.xml.bind.annotation.XmlType; 
 
 
/** 
 * <p>Java class for QueryExpressionBranchType complex type. 
 *  
 * <p>The following schema fragment specifies the expected content contained within this class. 
 *  
 * <pre> 
 * &lt;complexType name="QueryExpressionBranchType"> 
 *   &lt;complexContent> 
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}BranchType"> 
 *       &lt;sequence> 
 *         &lt;element name="QueryLanguageQuery" type="{urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0}ClassificationNodeQueryType" minOccurs="0"/> 
 *       &lt;/sequence> 
 *     &lt;/extension> 
 *   &lt;/complexContent> 
 * &lt;/complexType> 
 * </pre> 
 *  
 *  
 */ 
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlType(name = "QueryExpressionBranchType", propOrder = { 
    "queryLanguageQuery" 
}) 
public class QueryExpressionBranchType 
    extends BranchType 
{ 
 
    @XmlElement(name = "QueryLanguageQuery") 
    protected ClassificationNodeQueryType queryLanguageQuery; 
 
    /** 
     * Gets the value of the queryLanguageQuery property. 
     *  
     * @return 
     *     possible object is 
     *     {@link ClassificationNodeQueryType } 
     *      
     */ 
    public ClassificationNodeQueryType getQueryLanguageQuery() { 
        return queryLanguageQuery; 
    } 
 
    /** 
     * Sets the value of the queryLanguageQuery property. 
     *  
     * @param value 
     *     allowed object is 
     *     {@link ClassificationNodeQueryType } 
     *      
     */ 
    public void setQueryLanguageQuery(ClassificationNodeQueryType value) { 
        this.queryLanguageQuery = value; 
    } 
 
} 
