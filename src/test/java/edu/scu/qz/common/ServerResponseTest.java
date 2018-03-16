package edu.scu.qz.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(BlockJUnit4ClassRunner.class)
public class ServerResponseTest {

    @Test
    public void createBySuccess() {
        ServerResponse response = ServerResponse.createBySuccess();
        assertEquals(response.getStatus(), 0);
    }

    @Test
    public void createBySuccessMessage() {
        ServerResponse response = ServerResponse.createBySuccessMessage("success");
        assertEquals(response.getMsg(), "success");
    }

    @Test
    public void createBySuccessData() {
        ServerResponse response = ServerResponse.createBySuccess("sss");
        assertEquals("sss", response.getData());
    }
}