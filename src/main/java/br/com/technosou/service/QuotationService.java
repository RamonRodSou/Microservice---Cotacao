package br.com.technosou.service;

import br.com.technosou.client.CurrencyPriceClient;
import br.com.technosou.dto.CurrencyPriceDTO;
import br.com.technosou.dto.QuotationDTO;
import br.com.technosou.entity.QuotationEntity;
import br.com.technosou.message.KafkaEvents;
import br.com.technosou.repository.QuotationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository repository;

    @Inject
    KafkaEvents kafkaEvents;

    public void getCurrancyPrice() {
        CurrencyPriceDTO currencyPriceInfo = currencyPriceClient.getPriceByPair("USD-BRL");
        System.out.println(currencyPriceInfo.toString());
        if(updateCurrantInfPrice(currencyPriceInfo)){
            kafkaEvents.sendNewKafkaEvent(QuotationDTO
                    .builder()
                    .currencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()))
                    .date(new Date())
                    .build());
        }

    }

    private boolean updateCurrantInfPrice(CurrencyPriceDTO currencyInfo) {

        BigDecimal currentPrice = new BigDecimal(currencyInfo.getUSDBRL().getBid());
        boolean updatePrice = false;

        List<QuotationEntity> quotationList = repository.findAll().list();

        if(quotationList.isEmpty()) {

            saveQuotation(currencyInfo);
            updatePrice = true;

        } else {

            QuotationEntity lastDollarPrice = quotationList
                    .get(quotationList.size()-1);

            if(currentPrice.floatValue() > lastDollarPrice.getCurrencyPrice().floatValue()) {
                updatePrice = true;
                saveQuotation(currencyInfo);
            };
        }
        return updatePrice;
    }

    private void saveQuotation(CurrencyPriceDTO currencyPriceInfo) {
        QuotationEntity quotation = new QuotationEntity();
        quotation.setDate(new Date());
        quotation.setCurrencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()));
        quotation.setPctChange(currencyPriceInfo.getUSDBRL().getPctChange());
        quotation.setPair("USD-BRL");
    }
}
