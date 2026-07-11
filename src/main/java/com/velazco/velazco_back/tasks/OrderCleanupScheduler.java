package com.velazco.velazco_back.tasks;

import com.velazco.velazco_back.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCleanupScheduler {

  private static final Logger logger = LoggerFactory.getLogger(OrderCleanupScheduler.class);

  private final OrderService orderService;

  @Scheduled(cron = "0 */5 * * * ?") // Cada 5 minutos
  public void cleanUpExpiredOrders() {
    orderService.cancelExpiredPendingOrders();
  }
}