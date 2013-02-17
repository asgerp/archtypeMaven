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
 
package org.net4care.test.common; 
 
import static org.junit.Assert.*; 
 
import org.junit.Test; 
import org.net4care.utility.Utility; 
 
public class TestUtility { 
 
  @Test public void shouldConvertCodeListIntoBarFormat() { 
    String[] codelist; 
    String barPackedCodeList; 
     
    codelist = new String[]{ "19868-9" }; 
    barPackedCodeList = Utility.convertCodeListToBarSeparatedString(codelist); 
    assertEquals("|19868-9|", barPackedCodeList); 
 
    codelist = new String[]{ "19868-9","20150-9" }; 
    barPackedCodeList = Utility.convertCodeListToBarSeparatedString(codelist); 
    assertEquals("|19868-9|20150-9|", barPackedCodeList); 
 
    codelist = new String[]{ "19868-9","20150-9", "X", "Y", "ANDTHEEND" }; 
    barPackedCodeList = Utility.convertCodeListToBarSeparatedString(codelist); 
    assertEquals("|19868-9|20150-9|X|Y|ANDTHEEND|", barPackedCodeList); 
  } 
   
  @Test public void shouldConvertBarFormatIntoCodeList() { 
    String[] codelist; 
    String barPackedCodeList; 
     
    barPackedCodeList = "|19868-9|"; 
    codelist = Utility.convertBarSeparatedStringToCodeList(barPackedCodeList); 
    assertEquals( 1, codelist.length ); 
    assertEquals("19868-9", codelist[0]); 
 
    barPackedCodeList = "|19868-9|20150-9|X|Y|ANDTHEEND|"; 
    codelist = Utility.convertBarSeparatedStringToCodeList(barPackedCodeList); 
    assertEquals( 5, codelist.length ); 
    assertEquals("19868-9", codelist[0]); 
    assertEquals("X", codelist[2]); 
    assertEquals("ANDTHEEND", codelist[4]); 
     
  } 
} 
