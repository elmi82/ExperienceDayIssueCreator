package edic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
        model.addAttribute("issue", issue);
        return "result";
    }
}
