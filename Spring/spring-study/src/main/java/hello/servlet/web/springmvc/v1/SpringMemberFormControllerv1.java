package hello.servlet.web.springmvc.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SpringMemberFormControllerv1 {

    @RequestMapping("/springmvc/v1/members/new-form")
    public ModelAndView process(){
        System.out.println("SpringMemberFormControllerv1.process");
        return new ModelAndView("new-form");
    }
}