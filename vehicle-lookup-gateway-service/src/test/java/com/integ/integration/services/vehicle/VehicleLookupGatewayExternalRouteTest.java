package com.integ.integration.services.vehicle;

import org.junit.Test;

/**
 * Created by Saradhi on 12/09/2016.
 */

public class VehicleLookupGatewayExternalRouteTest extends AbstractVehicleLookupGatewayRouteTest {

    private static boolean initialised;

    static {
        System.setProperty("vehicle.details.lookup.cxfrs.bean", "vlExternalUiSecuredEndpoint");
    }

    @Test
    public void testVehicleLookup() throws Exception {
        jsonRawResult.expectedMessageCount(1);

        VEHICLE_LOOKUP_RESPONSE = loadFile("/samples/vehicle_lookup_response_motor_bike.xml");
        String expectedResponse = loadFile("/samples/vehicle_lookup_response_motor_bike_external.json");
        String request = loadFile("/samples/vehicle_lookup_request_Y717ADF.xml");

        String response = (String) startJsonRaw.requestBody(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);

        jsonRawResult.assertIsSatisfied();
        assertEquals(200, jsonRawResult.getExchanges().get(0).getIn().getHeader("CamelHttpResponseCode"));
    }

    @Test
    public void testVehicleLookup_NOT_FOUND() throws Exception {
        jsonRawResult.expectedMessageCount(1);

        VEHICLE_LOOKUP_RESPONSE = loadFile("/samples/vehicle_lookup_response_NOT_FOUND.xml");
        String expectedResponse = loadFile("/samples/vehicle_lookup_response_404.json");
        String request = loadFile("/samples/vehicle_lookup_request_Y717ADF.xml");

        String response = (String) startJsonRaw.requestBody(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);

        jsonRawResult.assertIsSatisfied();
        assertEquals(404, jsonRawResult.getExchanges().get(0).getIn().getHeader("CamelHttpResponseCode"));
    }

    @Test
    public void testVehicleLookup_VRM_WRONG() throws Exception {
        jsonRawResult.expectedMessageCount(1);

        VEHICLE_LOOKUP_RESPONSE = loadFile("/samples/vehicle_lookup_response_VRM_WRONG.xml");
        String expectedResponse = loadFile("/samples/vehicle_lookup_response_400.json");
        String request = loadFile("/samples/vehicle_lookup_request_Y717ADF.xml");

        String response = (String) startJsonRaw.requestBody(request);

        assertNotNull(response);
        assertEquals(expectedResponse, response);

        jsonRawResult.assertIsSatisfied();
        assertEquals(400, jsonRawResult.getExchanges().get(0).getIn().getHeader("CamelHttpResponseCode"));
    }

    /**
     * Not asserting response body in this test as this is being controlled by different project, which would
     * result in sudden test failures if generic validation error handler will be changed.
     * As such we are asserting only http code.
     * @throws Exception
     */
    @Test
    public void testVehicleLookup_VALIDATION_ERROR() throws Exception {
        jsonRawResult.expectedMessageCount(1);

        String request = loadFile("/samples/vehicle_lookup_request_VALIDATION_ERROR.xml");

        String response = (String) startJsonRaw.requestBody(request);

        assertNotNull(response);

        jsonRawResult.assertIsSatisfied();
        assertEquals(400, jsonRawResult.getExchanges().get(0).getIn().getHeader("CamelHttpResponseCode"));
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
