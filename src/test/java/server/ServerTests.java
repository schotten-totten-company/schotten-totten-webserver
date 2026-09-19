package server;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ServerTests {

	public void TestGetLocal() throws IOException {
		TestCreateGameGet("http://localhost:8080/");
	}
	public void TestGetRemote() throws IOException {
		TestCreateGameGet("https://schotten-totten.herokuapp.com/");
	}
	
	private void TestCreateGameGet(final String url) throws IOException {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-2" + System.currentTimeMillis();
		final Boolean result = rest.getForObject(url + "createGame?gamename=" + gamename, Boolean.class);
		//		System.out.println(result);
		Assertions.assertTrue(result);
	}
	
}
