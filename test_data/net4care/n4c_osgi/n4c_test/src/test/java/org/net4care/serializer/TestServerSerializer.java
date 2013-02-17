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
 
package org.net4care.serializer;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.net4care.helper.HelperMethods;
import org.net4care.observation.ClinicalQuantity;
import org.net4care.observation.Codes;
import org.net4care.observation.DeviceDescription;
import org.net4care.observation.ObservationSpecifics;
import org.net4care.observation.StandardTeleObservation;
import org.net4care.serializer.delegate.ServerJSONSerializer;
import org.net4care.storage.delegate.FakeObjectExternalDataSource;
import org.net4care.utility.Net4CareException;


public class TestServerSerializer {

  private ServerJSONSerializer serializer;

  @Before 
  public void setup() throws Net4CareException {
    serializer = new ServerJSONSerializer();
  }

  @Test
  public void ServerSerializerShouldBeIdempotent() {
    StandardTeleObservation sto = HelperMethods.defineSpirometryObservation("210689-1111", 2.2, 1.1);
    String res = serializer.serialize(sto);
    sto = serializer.deserialize(res);
    sto.setTime(0);
    String str1 = serializer.serialize(sto);
    sto = serializer.deserialize(res);
    sto.setTime(0);
    String str2 = serializer.serialize(sto);
    assertEquals(str1, str2);
    sto = serializer.deserialize(res); 
    sto.setTime(0);
    str1 = serializer.serialize(sto);
    assertEquals(str2, str1);
  }

  @Test
  public void shouldDeserializeSpirometryJSONStringRight() {
    StandardTeleObservation sto = HelperMethods.defineSpirometryObservation(FakeObjectExternalDataSource.NANCY_CPR, 0.1, 0.01);
    String res = serializer.serialize(sto);
    sto = serializer.deserialize(res);
    sto.setTime(4);
    assertEquals(Codes.LOINC_OID ,sto.getCodeSystem());
    assertEquals("" ,sto.getComment());
    assertEquals("org.net4care.org.mycompany" ,sto.getOrganizationUID());
    assertEquals(FakeObjectExternalDataSource.NANCY_CPR ,sto.getPatientCPR());
    assertEquals(4 ,sto.getTime());
    assertEquals("treatment-id" ,sto.getTreatmentID());
    DeviceDescription deviceDesc = sto.getDeviceDescription();
    assertEquals("2.1",deviceDesc.getHardwareRevision());
    assertEquals("1.1",deviceDesc.getSoftwareRevision());
    assertEquals("MyCompany",deviceDesc.getManufacturer());
    assertEquals("SpiroCraft-II",deviceDesc.getModel());
    assertEquals("Spirometer",deviceDesc.getType());
    assertEquals("69854",deviceDesc.getPartNumber());
    assertEquals("584216",deviceDesc.getSerialId());
    ObservationSpecifics obsSpec = sto.getObservationSpecifics();
    Iterator<ClinicalQuantity> iter = obsSpec.iterator();
    ClinicalQuantity quantity = iter.next();
    assertEquals("0.1",""+quantity.getValue());
    assertEquals("L",""+quantity.getUnit());
    assertEquals("FVC",""+quantity.getDisplayName());
    assertEquals("19868-9",""+quantity.getCode());
    quantity = iter.next();
    assertEquals("0.01",""+quantity.getValue());
    assertEquals("L",""+quantity.getUnit());
    assertEquals("FEV1",""+quantity.getDisplayName());
    assertEquals("20150-9",""+quantity.getCode());
  }

  @Test
  public void shouldDeserializeBloodPressureJSONStringRight() {
    StandardTeleObservation sto = HelperMethods.defineBloodPressureObservation(FakeObjectExternalDataSource.CARSTEN_CPR, 5.9, 2.4, "atest", "007");
    String res = serializer.serialize(sto);
    sto = serializer.deserialize(res);
    sto.setTime(4);
    assertEquals(Codes.UIPAC_OID ,sto.getCodeSystem());
    assertEquals("" ,sto.getComment());
    assertEquals("atest" ,sto.getOrganizationUID());
    assertEquals(FakeObjectExternalDataSource.CARSTEN_CPR ,sto.getPatientCPR());
    assertEquals(4 ,sto.getTime());
    assertEquals("007" ,sto.getTreatmentID());
    DeviceDescription deviceDesc = sto.getDeviceDescription();
    assertEquals("2.1",deviceDesc.getHardwareRevision());
    assertEquals("1.1",deviceDesc.getSoftwareRevision());
    assertEquals("MyOtherCompany",deviceDesc.getManufacturer());
    assertEquals("BloodCraft-II",deviceDesc.getModel());
    assertEquals("BloodMeter",deviceDesc.getType());
    assertEquals("69854",deviceDesc.getPartNumber());
    assertEquals("584216",deviceDesc.getSerialId());
    ObservationSpecifics obsSpec = sto.getObservationSpecifics();
    Iterator<ClinicalQuantity> iter = obsSpec.iterator();
    ClinicalQuantity quantity = iter.next();
    assertEquals("5.9",""+quantity.getValue());
    assertEquals("mm(Hg)",""+quantity.getUnit());
    assertEquals("Systolisk BT",""+quantity.getDisplayName());
    assertEquals("MSC88019",""+quantity.getCode());
    quantity = iter.next();
    assertEquals("2.4",""+quantity.getValue());
    assertEquals("mm(Hg)",""+quantity.getUnit());
    assertEquals("Diastolisk BT",""+quantity.getDisplayName());
    assertEquals("MSC88020",""+quantity.getCode());
  }

  @Test
  public void badNamingOfObservationSpecificClassShouldNotCauseDeserializingProblems() {
    DeviceDescription device_blood =
        new DeviceDescription("BloodMeter", "BloodCraft-II", 
            "MyOtherCompany", "584216", "69854", "2.1", "1.1");

    ObservationSpecifics obsSpec = new ObservationSpecificsStub();
    StandardTeleObservation sto = 
        new StandardTeleObservation(
            FakeObjectExternalDataSource.NANCY_CPR, 
            "12399", 
            "111", 
            Codes.UIPAC_OID, 
            device_blood, 
            obsSpec );
    String res = serializer.serialize(sto);
    sto = serializer.deserialize(res);
    obsSpec = sto.getObservationSpecifics();
    Iterator<ClinicalQuantity> iter = obsSpec.iterator();
    ClinicalQuantity quantity = iter.next();
    assertEquals("2.3",""+quantity.getValue());
    assertEquals("L",""+quantity.getUnit());
    assertEquals("value",""+quantity.getDisplayName());
    assertEquals("007",""+quantity.getCode());
    quantity = iter.next();
    assertEquals("1.1",""+quantity.getValue());
    assertEquals("M",""+quantity.getUnit());
    assertEquals("unit",""+quantity.getDisplayName());
    assertEquals("001",""+quantity.getCode());
  }

  private class ObservationSpecificsStub implements ObservationSpecifics {

    private List<ClinicalQuantity> quantities;

    private ClinicalQuantity value, unit;

    @SuppressWarnings("unused")
    public ClinicalQuantity getValue() {
      return value;
    }
    @SuppressWarnings("unused")
    public ClinicalQuantity getUnit() {
      return unit;
    }

    public ObservationSpecificsStub() {
      quantities = new ArrayList<ClinicalQuantity>();
      value =  new ClinicalQuantity(2.3, "L", "007", "value");
      unit =  new ClinicalQuantity(1.1, "M", "001", "unit");
      quantities.add(value);
      quantities.add(unit);
    }

    @SuppressWarnings("unused")
    public Iterator<ClinicalQuantity> getQuantities() {
      return quantities.iterator();
    }

    @Override
    public String getObservationAsHumanReadableText() {
      return "This is a stub!";
    }

    @Override
    public Iterator<ClinicalQuantity> iterator() {
      return quantities.iterator();
    }

  }
}
