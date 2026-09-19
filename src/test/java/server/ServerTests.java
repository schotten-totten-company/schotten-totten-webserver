package server;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ServerTests {

	@Test
	public void TestGetLocal() throws ClientProtocolException, IOException {
		TestGet("http://localhost:8080/");
	}
	@Test
	public void TestGetRemote() throws ClientProtocolException, IOException {
		TestGet("https://schotten-totten.herokuapp.com/");
	}
	
	private void TestGet(final String url) throws ClientProtocolException, IOException {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-2" + System.currentTimeMillis();
		final Boolean result = rest.getForObject(url + "createGame?gamename=" + gamename, Boolean.class);
		//		System.out.println(result);
		Assertions.assertTrue(result);
		
		final CloseableHttpClient httpclient = HttpClients.createDefault();
		final HttpGet httpGet = new HttpGet(url + "getMilestones?gamename=" + gamename);
		try (final CloseableHttpResponse response = httpclient.execute(httpGet)) {
		    System.out.println("status: " + new StatusLine(response));
		    HttpEntity entity = response.getEntity();
		    System.out.println("json: " + IOUtils.toString(entity.getContent()));
		    EntityUtils.consume(entity);
		}
	}
	
}
