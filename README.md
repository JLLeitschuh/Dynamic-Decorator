# Dynamic-Decorator

The decorator pattern can get very repetitive and lead to a lot of the same code.

Let's say that you want to benchmark the time it takes all of the methods in an interface to run.

You can either:

 1. Put that timing code in the implementation class. (Bad, as it adds another thing for the class
  to do).
 2. Decorate the class with an interface and put all of the benchmarking there. (This is very 
 repetitive code to write).
 3. Use a Java proxy object. (The API is messy and unwieldy).
 4. Use this simple library to make your life easier.

A simple example:

```java
interface SimpleInterface {
  void methodToCall();
}

class SimpleImplementer implements SimpleInterface {

  @Override
  public void methodToCall() {
    callCount++;
  }
}


ProxyDecoratorFactory.createSimple(
    new SimpleImplementer(),
    handler -> {
      System.out.println("Start time: " + System.currentTimeMillis());
      handler.callDecorated();
      System.out.println("Stop time: " + System.currentTimeMillis());
    },
    SimpleInterface.class);

```

There are several different types of handler that you can take advantage of that
will give you more information about the method being called as needed.

Additionally, there is a decorator that will cache values so that they are only computed once.
