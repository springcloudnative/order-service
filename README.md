# Reactive clients with Spring WebClient
*WebClient* is the modern alternative to *RestTemplate*. It provides blocking and non-blocking I/O, making it the perfect candidate for both imperative and reactive applications. It can be
operated through a functional-style, fluent API that lets you configure any aspect of the HTTP interaction.

## Service-to-service communication in Spring
As per the 15-Factor methodology, any backing service should be attached to an application through resource binding. For databases, you relied on the configuration properties provided by
Spring Boot to specify credentials and the URL. **When a backing service is another application**, you need to provide its URL in a similar way. Following the externalized configuration principle,
the URL should be configurable, not hard-coded. In Spring, **you can achieve that through a @ConfigurationProperties bean**.