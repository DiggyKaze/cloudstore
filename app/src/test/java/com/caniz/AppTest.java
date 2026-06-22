package com.caniz;

//import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("stage")
@SpringBootTest
 class AppTests{
  //  @Disabled("Test is not ready yet")
    @Test
	public void contextLoads() throws Exception{}
}
