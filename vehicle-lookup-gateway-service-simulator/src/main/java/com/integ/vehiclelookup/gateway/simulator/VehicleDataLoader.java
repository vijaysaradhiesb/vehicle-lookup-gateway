package com.integ.vehiclelookup.gateway.simulator;

import com.integ.integration.services.contracts.common.FaultDetails;
import com.integ.integration.services.contracts.common.Message;
import com.integ.integration.services.vehicle.api.VRMInvalidFailure;
import com.integ.integration.services.vehicle.api.VehicleNotFoundFailure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

/**
 * Created by Saradhi on 28/09/2016.
 */
public class VehicleDataLoader {
    private static final Logger LOG = LoggerFactory.getLogger(VehicleDataLoader.class);

    public String loadSampleVehicleDetails(String searchValue) throws IOException, URISyntaxException {
        return loadFile("/samples/vehicle_lookup_response_" + searchValue + ".xml");
    }

    public VehicleNotFoundFailure generateNotFound(String vrm) {
        VehicleNotFoundFailure vehicleLookupResponse = new VehicleNotFoundFailure();
        FaultDetails faultDetails = new FaultDetails();
        faultDetails.setMessage(new Message("Vehicle not found", "EN"));
        faultDetails.getHint().add(vrm);
        vehicleLookupResponse.setFaultDetails(faultDetails);
        return vehicleLookupResponse;
    }

    public VRMInvalidFailure generateVrmWrong(String vrm) {
        VRMInvalidFailure vehicleLookupResponse = new VRMInvalidFailure();
        FaultDetails faultDetails = new FaultDetails();
        faultDetails.setMessage(new Message("Registration number invalid", "EN"));
        faultDetails.getHint().add(vrm);
        vehicleLookupResponse.setFaultDetails(faultDetails);
        return vehicleLookupResponse;
    }

    private String loadFile(String fileName) throws IOException, URISyntaxException {
        InputStream inputStram = (InputStream) this.getClass().getResource(fileName).getContent();
        return StreamUtils.copyToString(inputStram, Charset.defaultCharset());
    }
}
