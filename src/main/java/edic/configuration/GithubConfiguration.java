package edic.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import edic.exception.InvalidConfigurationException;

@Component
@ConfigurationProperties(prefix="github")
public class GithubConfiguration {
	private String baseUrl;
	private String token;
	private Map<String, String> endpoints = new HashMap<>();

	public String getBaseUrl() {
		return baseUrl;
	}

	public Map<String, String> getEndpoints() {
		return endpoints;
	}

	public URL getEndpointURL(String endpoint)
			throws InvalidConfigurationException {
		if (!endpoints.containsKey(endpoint)) {
			throw new InvalidConfigurationException("Endpoint " + endpoint + " does not exist.");
		}

		try {
			URL url = new URL(this.baseUrl + this.endpoints.get(endpoint));
			return url;
		} catch (MalformedURLException e) {
			throw new InvalidConfigurationException(
					"Base URL and endpoint " + endpoint + " do not define a valid url.");
		}
	}

	public String getToken() {
		return token;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setEndpoints(Map<String, String> endpoints) {
		this.endpoints = endpoints;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
