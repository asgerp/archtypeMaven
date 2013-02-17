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
 
package org.net4care.observation;

import java.util.Iterator;
import java.util.List;

/** An implementation of ObservationSpecifics that is used on the server
 * side to avoid that the server need to know the specific type of
 * implementing class on the client side.
 * 
 * This class is not intended for general use on the client side, however
 * it may be use full as baseclass for an SMBs own implementation.
 * 
 * @author Net4Care, Morten Larsson, Aarhus University
 *
 */
 import com.apkc.archtype.quals.*;
 @ArchTypeComponent(
           patterns = {@Pattern(name="testLayered", kind = "Layered", role="Layer{2}")}
           ) 
public class DefaultObservationSpecifics implements ObservationSpecifics {

  private List<ClinicalQuantity> quantities;
  private String readableText = "";
  
  public Iterator<ClinicalQuantity> getQuantities() {
    return quantities.iterator();
  }
  
  public DefaultObservationSpecifics(List<ClinicalQuantity> q) {
    quantities = q; 
  }
  
  public DefaultObservationSpecifics(List<ClinicalQuantity> quantities, String r) {
    this(quantities);
    readableText = r;
  }
  
  public String getObservationAsHumanReadableText() {
    return readableText;
  }

  public Iterator<ClinicalQuantity> iterator() {
    return quantities.iterator();
  }
  
  @Override
  public String toString() {
    return readableText;
  }
}
