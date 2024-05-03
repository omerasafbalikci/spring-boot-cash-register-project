package com.toyota.reportservice.service.concretes;

import com.toyota.reportservice.service.abstracts.ReportService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.TreeMap;


@Service
@Transactional
@AllArgsConstructor
public class ReportManager implements ReportService {
    private final Logger logger = LogManager.getLogger(ReportService.class);
    private final WebClient.Builder webClientBuilder;


}
