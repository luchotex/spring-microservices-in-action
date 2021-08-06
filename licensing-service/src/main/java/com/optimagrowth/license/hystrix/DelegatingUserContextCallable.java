package com.optimagrowth.license.hystrix;

import com.optimagrowth.license.utils.UserContext;
import com.optimagrowth.license.utils.UserContextHolder;
import java.util.concurrent.Callable;

public class DelegatingUserContextCallable<V> implements Callable<V> {

  private Callable<V> delegate;
  private UserContext originalUserContext;

  public DelegatingUserContextCallable(Callable<V> delegate, UserContext userContext) {
    this.delegate = delegate;
    this.originalUserContext = userContext;
  }

  @Override
  public V call() throws Exception {
    UserContextHolder.setContext(this.originalUserContext);
    try {
      return delegate.call();
    } finally {
      this.originalUserContext = null;
    }
  }

  private static <V> Callable<V> create(Callable<V> delegate, UserContext context) {
    return new DelegatingUserContextCallable<>(delegate, context);
  }
}
