package org.jlleitschuh.decorator;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public final class ProxyDecoratorFactory {

  private ProxyDecoratorFactory() {

  }

  /**
   * A simple decorator that allows you to add logic around the call for all methods in an
   * interface.
   */
  public static <T> T createSimple(T decorated, SimpleHandler handler, Class<T> tClass) {
    return createReturning(
        decorated,
        caller -> {
          handler.handle(caller::callDecorated);
          return null;
        },
        tClass);
  }

  /**
   * A decorator that will cache the value returned by the method returning that same value on
   * subsequent calls.
   */
  public static <T> T createCaching(T decorated, Class<T> tClass) {
    RawHandler rawHandler = new RawHandler() {
      private final Map<Method, Object> cache = new HashMap<>();

      @Override
      public Object handle(ReturningDecoratedCaller decoratedCaller,
                           Method method,
                           Object... args) {
        return cache.computeIfAbsent(method,
            (key) -> decoratedCaller.callDecorated());
      }
    };

    return createRaw(decorated, rawHandler, tClass);

  }

  /**
   * A decorator that allows you to configure behaviour based upon the method and arguments passed
   * to the method.
   */
  public static <T> T createRaw(T decorated, RawHandler handler, Class<T> tClass) {
    Object newProxy = Proxy.newProxyInstance(
        ProxyDecoratorFactory.class.getClassLoader(),
        new Class[] {tClass},
        (proxy, method, args) ->
            handler.handle(() -> {
              try {
                return method.invoke(decorated, args);
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to invoke on decorated class", e);
              }
            }, method, args));
    return tClass.cast(newProxy);
  }

  /**
   * A decorator that allows you access to the return value of the interface methods.
   */
  public static <T> T createReturning(T decorated, ReturningHandler handler, Class<T> tClass) {
    return createRaw(
        decorated,
        (caller, method, args) -> handler.handle(caller),
        tClass);
  }
}
