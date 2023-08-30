package org.photonvision.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.photonvision.common.util.file.JacksonUtils;

public class ConfigSeralizationTest {
    @Test
    public void testMeme() throws IOException {
        PhotonConfiguration config1 = new PhotonConfiguration();

        var config1_str = JacksonUtils.serializeToString(config1);
        System.out.println("Before:\n" + config1_str);

        ObjectMapper mapper = new JsonMapper().setDefaultMergeable(true);

        String hardwareConfig =
                "{\"hardwareConfig\":{\"deviceName\" : \"Limelight 2+\",\"supportURL\" : \"https://limelightvision.io\",\n\"ledPins\" : [ 13, 18 ],\"ledsCanDim\" : true,\"ledPWMRange\" : [ 0, 100 ],\"ledPWMFrequency\" : 30000,\"vendorFOV\" : 75.76079874010732}}";

        PhotonConfiguration updatedConfig =
                mapper
                        .readerFor(PhotonConfiguration.class)
                        .withValueToUpdate(config1)
                        .readValue(hardwareConfig);

        config1_str = JacksonUtils.serializeToString(updatedConfig);
        System.out.println("After:\n" + config1_str);

        String hardwareConfig2 =
                "{\"hardwareConfig\":{\"ledDimCommand\": \"hello\"}}";

        updatedConfig =
                mapper
                        .readerFor(PhotonConfiguration.class)
                        .withValueToUpdate(config1)
                        .readValue(hardwareConfig2);

        config1_str = JacksonUtils.serializeToString(updatedConfig);
        System.out.println("And after merge part 2:\n" + config1_str);
    }
}
