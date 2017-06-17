# Axon 3.x CDI support

This is a small library to use Axon 3.x inside a (JEE) CDI container

## CDI Parameter resolving

Axon has a builtin DI (dependency injection) concept for command handlers called parameter resolving. Parameter
resolving takes place before your command handler is called. For each parameter following the command message payload,
Axon tries to resolve the actual value and injects it into the method call. For a deeper explanation see the axon
documentation for command handlers:

[Axon documentation on command handlers](https://docs.axonframework.org/v/3.0/part2/command-model.html#handling-commands-in-an-aggregate)

To use the Axon parameter resolving in a CDI environment you have add the `CdiParameterResolverFactory` to the
configuration.

Let's take an example. Given a standard command handler:

```Java
public class MyCommandHandler {
    @CommandHandler
    public void handle(MyCommand command, MyDependecy myDependecy) {
        myDependecy.doSomething();
    }
}
```

And the following CDI bean in your application:

```Java
public class MyDependency {
    public void doSomething() {
        System.out.println("Done");
    }
}
```

You can then add the `CdiParameterResolverFactory` to your Axon configuration by doing something like this:

```Java
import javax.enterprise.inject.Produces;
import javax.enterprise.context.ApplicationScoped;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import de.novity.axon.cdi.CdiParameterResolverFactory;

public class AxonConfigurationProducer {

    @ApplicationScoped
    @Produces
    public ParameterResolverFactory factory(CdiParameterResolverFactory cdiParameterResolverFactory) {
        return MultiParameterResolverFactory.ordered(
            cdiParameterResolverFactory,
            ClasspathParameterResolverFactory.forClass(getClass()),
            new ConfigurationParameterResolverFactory(configuration)
         );
    }
    
    @ApplicationScoped
    @Produces
    public Configuration configuration(ParameterResolverFactory factory) {
        return DefaultConfigurer
            .defaultConfiguration()
            .registerComponent(ParameterResolverFactory.class, config -> factory)
            .buildConfiguration();
    } 
}
```

The `ClasspathParameterResolverFactory` is optional. It its not officially documented, but analyzing the source code
reveals, that this factory tries to load further implementations using the service loading concept (SPI) of Java SE.