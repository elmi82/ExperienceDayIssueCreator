package edic.controller;

import java.net.URL;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import edic.configuration.GithubConfiguration;
import edic.exception.InvalidConfigurationException;
import edic.model.Issue;

@Controller
public class IssueController {
	private static final Logger log = LoggerFactory
			.getLogger(IssueController.class);

	private static final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private GithubConfiguration githubConfiguration;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String issueForm(Model model) throws InvalidConfigurationException {
		try {
			model.addAttribute("issueList", getIssueList());
		} catch (RestClientException e) {
			log.error("There was an error retrieving a list of issues on GitHub");
		}

		model.addAttribute("issue", new Issue());
		return "issues";
	}

	@RequestMapping(value = "/issue", method = RequestMethod.POST)
	public String issueSubmit(@ModelAttribute Issue issue, Model model)
			throws InvalidConfigurationException {
		Issue resultingIssue = createIssue(issue);

		model.addAttribute("issue", resultingIssue);
		return "result";
	}

	private Issue createIssue(Issue issue) throws InvalidConfigurationException {
		HttpHeaders headers = getAuthorizationHeaders();
		HttpEntity<Issue> request = new HttpEntity<Issue>(issue, headers);

		URL url = githubConfiguration.getEndpointURL("issues");

		try {
			ResponseEntity<Issue> response = restTemplate.exchange(url.toString(),
													HttpMethod.POST, request, Issue.class);
			Issue resultingIssue = response.getBody();
			return resultingIssue;
		} catch (RestClientException e) {
			log.error("There was an error creating the issue on GitHub");
			throw e;
		}
	}

	private HttpHeaders getAuthorizationHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Token " + githubConfiguration.getToken());
		return headers;
	}

	private Set<Issue> getIssueList() throws InvalidConfigurationException, RestClientException {
		HttpHeaders headers = getAuthorizationHeaders();
		HttpEntity<Issue> request = new HttpEntity<Issue>(headers);

		URL url = githubConfiguration.getEndpointURL("issues");
		ParameterizedTypeReference<Set<Issue>> responseType = new ParameterizedTypeReference<Set<Issue>>() {};
		ResponseEntity<Set<Issue>> response = restTemplate.exchange(url.toString(),
												HttpMethod.GET, request, responseType);
		Set<Issue> issueList = response.getBody();
		return issueList;
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, // 500
			reason = "Application is not properly configured")
	@ExceptionHandler(InvalidConfigurationException.class)
	public ModelAndView internalServerError(HttpServletRequest request,
			Exception exception) throws Exception {
		if (AnnotationUtils.findAnnotation(exception.getClass(),
				ResponseStatus.class) != null)
			throw exception;

		log.error("Endpoint for issues was not defined in application configuration");

		ModelAndView mav = new ModelAndView();
		mav.addObject("exception", exception);
		mav.addObject("url", request.getRequestURL());
		mav.addObject("timestamp", new Date().toString());
		mav.addObject("status", 500);
		mav.setViewName("invalidConfig");

		return mav;
	}
}
