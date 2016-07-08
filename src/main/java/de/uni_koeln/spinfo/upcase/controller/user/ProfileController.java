package de.uni_koeln.spinfo.upcase.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import de.uni_koeln.spinfo.upcase.mongodb.data.document.future.UpcaseUser;
import de.uni_koeln.spinfo.upcase.mongodb.repository.future.UpcaseUserRepository;

@Controller
public class ProfileController {

	@Autowired
	public UpcaseUserRepository upcaseUserRepository;


	@PreAuthorize("hasRole('USER')")
	@RequestMapping(value = "/user/profile/{email}", method = RequestMethod.GET)
	public String userInfo(@PathVariable("email") String email, Model model) {
		UpcaseUser user = upcaseUserRepository.findByEmail(email);	
		model.addAttribute("user", user);
		return "profile";
	}
	
	
	
}
