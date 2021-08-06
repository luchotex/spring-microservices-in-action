package com.optimagrowth.license.hystrix;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadLocalConfiguration {

  @Autowired(required = false)
  private HystrixConcurrencyStrategy existingConcurrencyStrategy;

  @PostConstruct
  public void init() {
    HystrixEventNotifier notifier = HystrixPlugins.getInstance().getEventNotifier();
    HystrixMetricsPublisher publisher = HystrixPlugins.getInstance().getMetricsPublisher();
    HystrixPropertiesStrategy propertiesStrategy =
        HystrixPlugins.getInstance().getPropertiesStrategy();
    HystrixCommandExecutionHook commandExecutionHook =
        HystrixPlugins.getInstance().getCommandExecutionHook();

    HystrixPlugins.reset();

    HystrixPlugins.getInstance()
        .registerConcurrencyStrategy(new ThreadLocalAwareStrategy(existingConcurrencyStrategy));
    HystrixPlugins.getInstance().registerEventNotifier(notifier);
    HystrixPlugins.getInstance().registerMetricsPublisher(publisher);
    HystrixPlugins.getInstance().registerPropertiesStrategy(propertiesStrategy);
    HystrixPlugins.getInstance().registerCommandExecutionHook(commandExecutionHook);
  }
}
