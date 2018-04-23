package com.integ.vehiclelookup.gateway.simulator;

import com.integ.integration.services.vehicle.api.VehicleLookupRequest;
import com.integ.integration.services.vehicle.api.VehicleLookupResponse;
import com.integ.integration.services.vehicle.api.VehicleNotFoundFailure;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.junit4.TestSupport;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.ShutdownTimeout;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Saradhi on 29/09/2016.
 */

@DisableJmx
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:test-context.xml",
        "classpath:META-INF/spring/camel-context.xml"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ShutdownTimeout(120)
public class VehicleLookupSimulatorTest extends TestSupport {

    @EndpointInject(uri = "direct-vm:vehicle.lookup.endpoint.simulator")
    protected ProducerTemplate start;

    @Autowired
    private CamelContext context;

    @Before
    public void addRoutes() throws Exception {
        context.setUseMDCLogging(true);
        context.setTracing(true);
    }

    @Test
    public void testVehicleLookupSimulator_Car() throws Exception {
        VehicleLookupRequest request = new VehicleLookupRequest();
        request.setVrm("BX10BVH");

        VehicleLookupResponse response = (VehicleLookupResponse)start.requestBody(request);
        assertNotNull(response.getVehicleInfo());
        assertEquals("96",response.getVehicleInfo().getMake().getCode());
        assertEquals("128",response.getVehicleInfo().getModel().getCode());
        assertEquals("2000",response.getVehicleInfo().getEngineType().getCode());

    }

    @Test
    public void testVehicleLookupSimulator_Bike() throws Exception {
        VehicleLookupRequest request = new VehicleLookupRequest();
        request.setVrm("LF02VWP");

        VehicleLookupResponse response = (VehicleLookupResponse)start.requestBody(request);
        assertNotNull(response.getVehicleInfo());
        assertEquals("LM",response.getVehicleInfo().getMake().getCode());
        assertEquals("128",response.getVehicleInfo().getModel().getCode());
        assertEquals("125_CC",response.getVehicleInfo().getEngineType().getCode());

    }

    @Test
    public void testVehicleLookupSimulator_Fail() throws Exception {
        VehicleLookupRequest request = new VehicleLookupRequest();
        request.setVrm("VLFAIL");

        VehicleNotFoundFailure response = (VehicleNotFoundFailure)start.requestBody(request); //Just testing casting
        assertNotNull(response);
    }
}
