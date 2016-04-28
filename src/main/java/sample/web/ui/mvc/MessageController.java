
package sample.web.ui.mvc;

import groovyjarjarasm.asm.tree.IntInsnNode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import sample.web.ui.Message;
import sample.web.ui.MessageRepository;
import sample.web.ui.HttpRequest;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ResponseBody;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;

@Controller
@RequestMapping("/")
public class MessageController {

	private final MessageRepository messageRepository;

	public static final String client_id = "2683253229";
	public static final String client_secret = "ad67b6de9b650688c57b0910ad0fd670";
	public static final String redirect_uri = "http://123.206.59.138/oauth";
	
	
	public MessageController(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	@RequestMapping
	public String list() {
		return "index";
	}

	@RequestMapping(value = "oauth", params = "code", method = RequestMethod.GET)
	public ModelAndView oauth(@ModelAttribute("code") String code) {
		System.out.println(code);
		
		String access_token_json = HttpRequest.sendPost("https://api.weibo.com/oauth2/access_token?client_id=" + client_id
													+ "&client_secret=" + client_secret
													+ "&grant_type=authorization_code&redirect_uri=" + redirect_uri
													+ "&code=" + code
													, "");
		System.out.println(access_token_json);
		ObjectMapper objectMapper = new ObjectMapper();
		String access_token = " ";
		try {
			access_token = (String) objectMapper.readValue(access_token_json, Map.class).get("access_token");
		} catch (IOException e) {
			return new ModelAndView("oauth", "texts", null);
		}
		String user_timeline_json = HttpRequest.sendGet("https://api.weibo.com/2/statuses/user_timeline.json"
														+ "?access_token=" + access_token);
		System.out.println(user_timeline_json);
		String[] texts = null;
		try {
	
			ArrayList statuses =(ArrayList) objectMapper.readValue(user_timeline_json, Map.class).get("statuses");

			System.out.println(statuses.size());
			
			int statuses_size = statuses.size();
			texts = new String[statuses_size + 1];
			for(int i = 0; i < statuses_size; i++)
				texts[i] = String.valueOf(((Map) statuses.get(i)).get("text"));

			if(statuses_size > 0 && texts[0].length() >= texts[statuses_size - 1].length())
				texts[statuses_size] = "幸福";
			else
				texts[statuses_size] = "不幸福";
			
			
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		return new ModelAndView("oauth", "texts", texts);
	}



}
