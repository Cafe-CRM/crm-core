package com.cafe.crm.controllers.card;

import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.models.card.Card;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.calculate.CalculateControllerService;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.card.CardControllerService;
import com.cafe.crm.services.interfaces.card.CardService;
import com.cafe.crm.services.interfaces.email.EmailService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.utils.PatternStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
@RequestMapping("/manager")
public class CardProfileController {

	private final Logger logger = LoggerFactory.getLogger(CardProfileController.class);

	private final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(PatternStorage.EMAIL);

	private final CardService cardService;
	private final CalculateService calculateService;
	private final CardControllerService cardControllerService;
	private final BoardService boardService;
	private final CalculateControllerService calculateControllerService;
	private final PropertyService propertyService;
	private final EmailService emailService;
	private final PriceNameProperties priceNameProperties;


	@Autowired
	public CardProfileController(CardService cardService, CalculateControllerService calculateControllerService, PropertyService propertyService, CardControllerService cardControllerService, BoardService boardService, CalculateService calculateService, EmailService emailService, PriceNameProperties priceNameProperties) {
		this.cardService = cardService;
		this.calculateControllerService = calculateControllerService;
		this.propertyService = propertyService;
		this.cardControllerService = cardControllerService;
		this.boardService = boardService;
		this.calculateService = calculateService;
		this.emailService = emailService;
		this.priceNameProperties = priceNameProperties;
	}

	@RequestMapping(value = {"/card/{id}"}, method = RequestMethod.GET)
	public ModelAndView getCard(@PathVariable Long id) {
		ModelAndView modelAndView = new ModelAndView("card/card");
		modelAndView.addObject("card", cardService.getOne(id));
		modelAndView.addObject("listCalculate", calculateService.getAllOpen());
		modelAndView.addObject("boards", boardService.getAll());
		return modelAndView;
	}

	@RequestMapping(value = {"/card/add-card-to-calculate"}, method = RequestMethod.POST)
	public String addCardToCalculate(@RequestParam("idCard") Long idCard,
									 @RequestParam("idCalculate") Long idCalculate) {
		cardControllerService.addCardToCalculate(idCard, idCalculate);
		return "redirect:/manager";
	}

	@RequestMapping(value = {"/card/edit"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editCard(@RequestParam("idCard") Long idCard,
									  @RequestParam("name") String name,
									  @RequestParam("surname") String surname,
									  @RequestParam("phone") String phone,
									  @RequestParam("email") String email) {
		Card card = cardService.getOne(idCard);
		if (card != null) {
			Card testPhone = cardService.findByPhone(phone);
			if (testPhone != null && !Objects.equals(testPhone.getPhoneNumber(), phone)) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This phone is exist");
			}
			card.setName(name);
			card.setSurname(surname);
			card.setPhoneNumber(phone);
			card.setEmail(email);
			cardService.save(card);
		}
		return ResponseEntity.status(HttpStatus.OK).body(card);
	}

	@RequestMapping(value = {"/card/registration"}, method = RequestMethod.POST)
	public ResponseEntity registrationNewUser(@RequestParam("idCard") Long idCard,
											  @RequestParam("name") String name,
											  @RequestParam("surname") String surname,
											  @RequestParam("phone") String phone,
											  @RequestParam("email") String email,
											  @RequestParam(value = "invited", required = false) Long invited) {
		Card card = cardService.getOne(idCard);
		if (card != null) {
			Card testPhone = cardService.findByPhone(phone);
			if (testPhone != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This phone is exist");
			}
			card.setName(name);
			card.setSurname(surname);
			card.setPhoneNumber(phone);
			card.setEmail(email);
			card.setActivatedCard(true);
			if (invited != null) {
				Card ourInvited = cardService.getOne(invited);
				card.setWhoInvitedMe(invited);
				Property refBonusProperty = propertyService.findByName(priceNameProperties.getRefBonus());
				Double balance = Double.valueOf(refBonusProperty.getValue());
				card.setBalance(balance);
				ourInvited.getIdMyInvitedUsers().add(idCard);
				cardService.save(ourInvited);
			}
			cardService.save(card);
		}
		return ResponseEntity.status(HttpStatus.OK).body(card);
	}

	@RequestMapping(value = {"/card/addMoney"}, method = RequestMethod.POST)
	public String addMoneyToBalance(@RequestParam("id") Long idCard,
									@RequestParam("money") Long money,
									HttpServletRequest request) {
		Card card = cardService.getOne(idCard);
		if (card != null && money >= 0) {
			Double balance = card.getBalance();
			card.setBalance(balance + money);
			cardService.save(card);
			UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			logger.info("Administrator " + userDetails.getUsername() + "  has credited " + money + " to the card # " + idCard);
			if (card.getEmail() != null) {
				emailService.sendBalanceInfoAfterRefill(balance + money, Double.valueOf(money), card.getEmail());
			}
		}

		String referrer = request.getHeader("Referer");
		return "redirect:" + referrer;
	}

	@RequestMapping(value = {"/card/add-calculate-with-card"}, method = RequestMethod.POST)
	public String createCalculate(@RequestParam("boardId") Long id,
								  @RequestParam("number") Double number,
								  @RequestParam("description") String description,
								  @RequestParam("idCard") Long idCard,
								  HttpServletRequest request) {
		calculateControllerService.createCalculateWithCard(id, number.longValue(), description, idCard);
		String referrer = request.getHeader("Referer");
		return "redirect:" + referrer;
	}

	@RequestMapping(value = {"/card/uploadPhoto"}, method = RequestMethod.POST)
	public String uploadPhoto(@RequestParam("file") MultipartFile file,
							  @RequestParam("id") Long idCard,
							  HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		Card card = cardService.getOne(idCard);
		try {
			byte[] arra = file.getBytes();
			card.setPhoto(arra);

		} catch (IOException e) {
			return "redirect:" + referer;
		}
		cardService.save(card);
		return "redirect:" + referer;
	}

	@RequestMapping(value = "/card/image/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> getImage(@PathVariable("id") Long id) throws IOException {

		byte[] imageContent = cardService.getOne(id).getPhoto();
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
	}

	@RequestMapping(value = {"/card/checkUser"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> checkInvited(@RequestParam("searchParam") String searchParam, @RequestParam("ourId") Long ourId) {
		Card card = cardService.checkWhoInvitedMe(searchParam);
		List<Card> list = cardService.findByListSurname(searchParam);
		if (card != null && !Objects.equals(card.getId(), ourId) && list.size() <= 1) {
			return ResponseEntity.status(HttpStatus.OK).body(card);
		}
		if (list.size() > 1) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(list.size());
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("nope");
	}

	@RequestMapping(value = {"/card/validateEmail"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> validateEmail(@RequestParam(name = "email") String email) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
		if (matcher.find()) {
			return ResponseEntity.status(HttpStatus.OK).body("done");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("nope");
		}

	}
}
