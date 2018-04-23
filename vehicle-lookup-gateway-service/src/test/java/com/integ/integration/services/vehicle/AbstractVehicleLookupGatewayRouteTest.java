package com.integ.integration.services.vehicle;

import com.integ.integration.services.vehicle.api.VehicleLookupRequest;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.TestSupport;
import org.apache.camel.test.spring.DisableJmx;
import org.apache.camel.test.spring.ShutdownTimeout;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

/**
 * Created by Saradhi on 12/09/2016.
 */

@DisableJmx
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/test-context.xml",
        "classpath:META-INF/spring/cxf-beans-vl.xml",
        "classpath:META-INF/spring/camel-context-vl.xml",
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ShutdownTimeout(120)
public abstract class AbstractVehicleLookupGatewayRouteTest extends TestSupport {
    @EndpointInject(uri = "direct-vm:startXml")
    protected ProducerTemplate startXml;
    @EndpointInject(uri = "direct-vm:startJson")
    protected ProducerTemplate startJson;
    @EndpointInject(uri = "direct-vm:startJsonRaw")
    protected ProducerTemplate startJsonRaw;

    @EndpointInject(uri = "mock:xmlResult")
    protected MockEndpoint xmlResult;
    @EndpointInject(uri = "mock:jsonResult")
    protected MockEndpoint jsonResult;
    @EndpointInject(uri = "mock:jsonRawResult")
    protected MockEndpoint jsonRawResult;

    @Autowired
    protected ApplicationContext applicationContext;

    protected static Object VEHICLE_LOOKUP_RESPONSE;

    protected static final int PORT = AvailablePortFinder.getNextAvailable(new Random().nextInt(60000) + 2000);

    @Autowired
    protected org.apache.camel.converter.jaxb.JaxbDataFormat vehicleLookupDataFormat;

    @Autowired
    private ModelCamelContext modelCamelContext;

    static {
        System.setProperty("org.apache.cxf.transports.http_jetty.DontClosePort", "true");
        System.setProperty("http.port", Integer.toString(PORT));
    }

    @Before
    public void addRoutes() throws Exception {
        MockEndpoint.resetMocks(modelCamelContext);

        if (isInitialised()) {
            return;
        }

        markInitialised();

        modelCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct-vm:startXml").convertBodyTo(String.class)
                        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                        .setHeader(Exchange.CONTENT_TYPE, constant("text/xml"))
                        .to("http://127.0.0.1:" + PORT + "/cxf/vehicleDetailsLookupGateway/retrieveVehicleDetails?throwExceptionOnFailure=false")
                        .unmarshal(vehicleLookupDataFormat)
                        .convertBodyTo(String.class)
                        .to("mock:xmlResult");

                from("direct-vm:startJson")
                        .convertBodyTo(VehicleLookupRequest.class)
                        .marshal().json(JsonLibrary.Jackson)
                        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader("Accept", constant("application/json"))
                        .to("http://127.0.0.1:" + PORT + "/cxf/vehicleDetailsLookupGateway/retrieveVehicleDetails?throwExceptionOnFailure=false")
                        .convertBodyTo(String.class)
                        .to("mock:jsonResult");


                from("direct-vm:startJsonRaw")
                        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                        .setHeader("Accept", constant("application/json"))
                        .setHeader(Exchange.HTTP_PATH, constant("/retrieveVehicleDetails"))
                        .convertBodyTo(VehicleLookupRequest.class)
                        .to("cxfrs:bean:testClient?throwExceptionOnFailure=false")
                        .convertBodyTo(String.class)
                        .to("mock:jsonRawResult");

                from("direct-vm:vehicle.details.lookup.ERS").process(exchange -> exchange.getIn().setBody(VEHICLE_LOOKUP_RESPONSE)).unmarshal(vehicleLookupDataFormat);
            }
        });
    }

    protected abstract boolean isInitialised();

    protected abstract void markInitialised();

    protected String loadFile(String fileName) throws IOException, URISyntaxException {
        URI uri = this.getClass().getResource(fileName).toURI();
        return FileUtils.readFileToString(new File(uri));
    }

}
