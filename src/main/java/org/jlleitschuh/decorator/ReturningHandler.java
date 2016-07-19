package org.jlleitschuh.decorator;

public interface ReturningHandler {

  Object handle(ReturningDecoratedCaller decoratedCaller);
}
