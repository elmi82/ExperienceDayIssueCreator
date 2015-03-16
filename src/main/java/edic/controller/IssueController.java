package edic.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import edic.model.Issue;

@Controller
public class IssueController {
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String greetingForm(Model model) {
        model.addAttribute("issue", new Issue());
        return "issues";
    }

    @RequestMapping(value="/issue", method=RequestMethod.POST)
	public String greetingSubmit(@ModelAttribute Issue issue, Model model) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization",
				"Token 45a8e3de4036d202e3b97f33a6203b290cf32466");

		HttpEntity<Issue> request = new HttpEntity<Issue>(issue, headers);
		RestTemplate restTemplate = new RestTemplate();

		String url = "https://api.github.com/repos/elmi82/ExperienceDayIssueCreator/issues";
		ResponseEntity<Issue> response = restTemplate.exchange(url,
				HttpMethod.POST, request, Issue.class);
		Issue resultingIssue = response.getBody();

		model.addAttribute("issue", resultingIssue);
		return "result";
    }
}
