package org.jlleitschuh.decorator;

@FunctionalInterface
public interface ReturningDecoratedCaller {
  Object callDecorated();
}
