package com.integ.integration.services.vehicle;

import org.junit.Test;

/**
 * Created by Saradhi on 12/09/2016.
 */

public class VehicleLookupGatewayInternalRouteTest extends com.integ.integration.services.vehicle.AbstractVehicleLookupGatewayRouteTest {

    private static boolean initialised;

    static {
        System.setProperty("vehicle.details.lookup.cxfrs.bean", "vlInternalUnsecuredEndpoint");
    }

    @Test
    public void testVehicleLookup_positive_one() throws Exception {
        VEHICLE_LOOKUP_RESPONSE = loadFile("/samples/vehicle_lookup_response_motor_bike.xml");
        String request = loadFile("/samples/vehicle_lookup_request_LF02VWP.xml");

        String response = (String) startXml.requestBody(request);

        assertNotNull(response);
        assertEquals(VEHICLE_LOOKUP_RESPONSE, response);
    }

    @Test
    public void testVehicleLookup_positive_two() throws Exception {
        VEHICLE_LOOKUP_RESPONSE = loadFile("/samples/vehicle_lookup_response_van.xml");
        String request = loadFile("/samples/vehicle_lookup_request_BX10BVH.xml");

        String response = (String) startXml.requestBody(request);

        assertNotNull(response);
        assertEquals(VEHICLE_LOOKUP_RESPONSE, response);

    }

    @Test
    public void testVehicleLookup_vehicle_search_error() throws Exception {
        VEHICLE_LOOKUP_RESPONSE = loadFile("/samples/vehicle_lookup_response_NOT_FOUND.xml");
        String request = loadFile("/samples/vehicle_lookup_request_Y717ADF.xml");

        String response = (String) startXml.requestBody(request);

        assertNotNull(response);
        assertEquals(VEHICLE_LOOKUP_RESPONSE, response);
    }

    @Test
    public void testVehicleLookup_warning_invalid_registration() throws Exception {
        VEHICLE_LOOKUP_RESPONSE = loadFile("/samples/vehicle_lookup_response_warning_invalid_registration.xml");
        String request = loadFile("/samples/vehicle_lookup_request_ABC.xml");

        String response = (String) startXml.requestBody(request);

        assertNotNull(response);
        assertEquals(VEHICLE_LOOKUP_RESPONSE, response);
    }

    @Test(expected = Exception.class)
    public void testVehicleLookup_validation_error() throws Exception {
        String request = loadFile("/samples/vehicle_lookup_invalid_request_LF02VWP.xml");
        startXml.requestBody(request);

    }

    @Override
    protected boolean isInitialised() {
        return initialised;
    }

    @Override
    protected void markInitialised() {
        initialised = true;
    }
}
