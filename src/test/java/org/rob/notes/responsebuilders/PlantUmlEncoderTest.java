package org.rob.notes.responsebuilders;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlantUmlEncoderTest {

    @Test
    public void test() throws IOException {
        //Example from here: http://plantuml.com/text-encoding
        String uml = "Alice -> Bob: Authentication Request\n"
                + "Bob --> Alice: Authentication Response";

        assertEquals("Syp9J4vLqBLJSCfFib9mB2t9ICqhoKnEBCdCprC8IYqiJIqkuGBAAUW2rJY256DHLLoGdrUS2W00", PlantUmlEncoder.encode(uml.getBytes(StandardCharsets.UTF_8)));
    }
}
