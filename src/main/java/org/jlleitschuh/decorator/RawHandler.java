package org.jlleitschuh.decorator;


import java.lang.reflect.Method;

@FunctionalInterface
public interface RawHandler {
  Object handle(ReturningDecoratedCaller decoratedCaller, Method method, Object ...args);
}
