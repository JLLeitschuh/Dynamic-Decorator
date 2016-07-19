package org.jlleitschuh.decorator;

@FunctionalInterface
public interface SimpleHandler {
  void handle(DecoratedCaller decoratedCaller);
}
