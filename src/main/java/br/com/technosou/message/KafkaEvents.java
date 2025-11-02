package br.com.technosou.message;

import br.com.technosou.dto.QuotationDTO;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KafkaEvents {

    private final Logger logger = LoggerFactory.getLogger(KafkaEvents.class);

    @Channel("quotation-channel")
    Emitter<QuotationDTO> quotationRequestEmitter;

    public void sendNewKafkaEvent(QuotationDTO quotation) {
        logger.info("Sending message to Quotation Channel");
        quotationRequestEmitter.send(quotation).toCompletableFuture().join();
    }

}
