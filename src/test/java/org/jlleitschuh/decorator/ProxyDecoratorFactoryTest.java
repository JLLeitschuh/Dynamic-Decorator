package org.jlleitschuh.decorator;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProxyDecoratorFactoryTest {
  private int callCount = 0;

  interface SimpleInterface {
    void methodToCall();
  }

  class SimpleImplementer implements SimpleInterface {

    @Override
    public void methodToCall() {
      callCount++;
    }
  }

  interface ReturningInterface {
    int callCount();
  }

  class ReturningImplementer implements  ReturningInterface {

    @Override
    public int callCount() {
      return ++callCount;
    }
  }

  private SimpleInterface createSimple(SimpleHandler caller) {
    return ProxyDecoratorFactory.createSimple(
        new SimpleImplementer(),
        caller,
        SimpleInterface.class);
  }

  private ReturningInterface createReturning(ReturningHandler caller) {
    return ProxyDecoratorFactory.createReturning(
      new ReturningImplementer(),
      caller,
      ReturningInterface.class);
  }

  private ReturningInterface createCaching() {
    return ProxyDecoratorFactory.createCaching(
        new ReturningImplementer(),
        ReturningInterface.class);
  }

  @Test
  public void callInterfaceMethod() {
    SimpleInterface decorated = createSimple(DecoratedCaller::callDecorated);

    decorated.methodToCall();

    assertEquals("Method should have been called once", 1, callCount);
  }

  @Test
  public void callUsingDecorator() {
    SimpleInterface decorated = createSimple(call -> {
      callCount++;
      call.callDecorated();
    });

    decorated.methodToCall();

    assertEquals("Method should called handler then decorated class.", 2, callCount);
  }

  @Test
  public void doNotCallDecorated() {
    SimpleInterface decorated = createSimple(call -> {

    });
    decorated.methodToCall();

    assertEquals("The count should not have been incremented", 0, callCount);
  }

  @Test
  public void checkReturnValue() {
    ReturningInterface decorated = createReturning(call -> (Integer) call.callDecorated() + 1);
    int returnValue = decorated.callCount();
    assertEquals("The count should have been incremented by one by the decorator", 2, returnValue);
  }

  @Test
  public void nestedDecorator() {
    ReturningHandler incrementer = call -> (Integer) call.callDecorated() + 1;
    ReturningInterface decoratedTwice =
        ProxyDecoratorFactory.createReturning(
            createReturning(incrementer),
            incrementer,
            ReturningInterface.class);
    int returnValue = decoratedTwice.callCount();

    assertEquals("The count should have been incremented by one by the decorator", 3, returnValue);
  }

  @Test
  public void cachingDecorator() {
    ReturningInterface decorator = createCaching();
    // Call 1 time to store in cache
    decorator.callCount();
    assertEquals("Value should have been cached", 1, decorator.callCount());
  }
}
