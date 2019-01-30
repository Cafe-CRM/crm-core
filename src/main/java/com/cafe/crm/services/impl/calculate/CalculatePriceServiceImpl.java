package com.cafe.crm.services.impl.calculate;

import com.cafe.crm.configs.property.PriceNameProperties;
import com.cafe.crm.models.client.Client;
import com.cafe.crm.models.client.LayerProduct;
import com.cafe.crm.models.client.TimerOfPause;
import com.cafe.crm.models.property.Property;
import com.cafe.crm.services.interfaces.calculate.CalculatePriceService;
import com.cafe.crm.services.interfaces.calculate.TimerOfPauseService;
import com.cafe.crm.services.interfaces.property.PropertyService;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@Transactional
public class CalculatePriceServiceImpl implements CalculatePriceService {

	private final TimeManager timeManager;
	private final PropertyService propertyService;
	private final TimerOfPauseService timerOfPauseService;
	private final PriceNameProperties priceNameProperties;

	@Autowired
	public CalculatePriceServiceImpl(TimerOfPauseService timerOfPauseService,
									 PropertyService propertyService,
									 TimeManager timeManager,
									 PriceNameProperties priceNameProperties) {
		this.timerOfPauseService = timerOfPauseService;
		this.propertyService = propertyService;
		this.timeManager = timeManager;
		this.priceNameProperties = priceNameProperties;
	}

	@Override
	public void calculatePriceTimeIfWasPause(Client client) {
		TimerOfPause timer = timerOfPauseService.findTimerOfPauseByIdOfClient(client.getId());
		Long timeOfPause = timer.getWholeTimePause();
		LocalTime timeStart = client.getTimeStart().toLocalTime().withSecond(0).withNano(0);
		LocalTime timeNow = timeManager.getTime().withSecond(0).withNano(0);
		LocalTime timePassed = timeNow.minusHours(timeStart.getHour()).minusMinutes(timeStart.getMinute() + timeOfPause);
		client.setPassedTime(timePassed);
		double priceTime;
		long passedHours = (long) timePassed.getHour();
		long passedMinutes = (long) timePassed.getMinute();
		Property priceFirstHourProperty = propertyService.findByName(priceNameProperties.getFirstHour());
		Property priceNextHoursProperty = propertyService.findByName(priceNameProperties.getNextHours());
		double firstHour = Double.parseDouble(priceFirstHourProperty.getValue());
		double secondHour = Double.parseDouble(priceNextHoursProperty.getValue());
		if (passedHours == 0) {
			priceTime = firstHour;
		} else {
			priceTime = firstHour + ((passedHours - 1.0) * secondHour) + (passedMinutes / 60.0) * secondHour;
		}
		client.setPriceTime((double) Math.round(priceTime));
	}

	@Override
	public void calculatePriceTime(Client client) {
		LocalTime timeStart = client.getTimeStart().toLocalTime().withSecond(0).withNano(0);
		LocalTime timeNow = timeManager.getTime().withSecond(0).withNano(0);
		LocalTime timePassed = timeNow.minusHours(timeStart.getHour()).minusMinutes(timeStart.getMinute());
		client.setPassedTime(timePassed);
		double priceTime;
		long passedHours = (long) timePassed.getHour();
		long passedMinutes = (long) timePassed.getMinute();
		Property priceFirstHourProperty = propertyService.findByName(priceNameProperties.getFirstHour());
		Property priceNextHoursProperty = propertyService.findByName(priceNameProperties.getNextHours());
		double firstHour = Double.parseDouble(priceFirstHourProperty.getValue());
		double secondHour = Double.parseDouble(priceNextHoursProperty.getValue());
		if (passedHours == 0) {
			priceTime = firstHour;
		} else {
			priceTime = firstHour + ((passedHours - 1.0) * secondHour) + (passedMinutes / 60.0) * secondHour;
		}
		client.setPriceTime((double) Math.round(priceTime));
	}


	@Override
	public void addDiscountOnPriceTime(Client client) {
		double priceTime = client.getPriceTime();
		priceTime -= (priceTime * (client.getDiscount() + client.getDiscountWithCard())) / 100;
		client.setPriceTime((double) Math.round(priceTime));
	}

	@Override
	public void getAllPrice(Client client) {
		client.setAllPrice((double) Math.round(client.getPriceMenu() + client.getPriceTime()));
	}

	@Override
	public void payWithCardAndCache(Client client) {
		Double payWithCard = client.getPayWithCard();
		Double allPrice = client.getAllPrice();
		client.setPayWithCard(allPrice < client.getPayWithCard() ? allPrice : payWithCard);
		if (client.getCard() == null) {
			client.setPayWithCard(0D);
		}
		client.setCache(allPrice - (double) client.getPayWithCard());
	}

}
