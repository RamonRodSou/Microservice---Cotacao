package br.com.technosou.scheduler;

import br.com.technosou.service.QuotationService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class QuotationScheduler {

    @Inject
    private QuotationService quotationService;

    private final Logger logger = LoggerFactory.getLogger(QuotationScheduler.class);

    @Transactional
    @Scheduled(every = "35s", identity = "task-job")
    void schedule() {
        logger.info("------- RUNNING SCHEDULER -------");
        quotationService.getCurrancyPrice();
    }
}
