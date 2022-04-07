# Reactive clients with Spring WebClient
*WebClient* is the modern alternative to *RestTemplate*. It provides blocking and non-blocking I/O, making it the perfect candidate for both imperative and reactive applications. It can be
operated through a functional-style, fluent API that lets you configure any aspect of the HTTP interaction.

## Service-to-service communication in Spring
As per the 15-Factor methodology, any backing service should be attached to an application through resource binding. For databases, you relied on the configuration properties provided by
Spring Boot to specify credentials and the URL. **When a backing service is another application**, you need to provide its URL in a similar way. Following the externalized configuration principle,
the URL should be configurable, not hard-coded. In Spring, **you can achieve that through a @ConfigurationProperties bean**.

# Understandig how to use timeouts effectively
Setting a good value for the timeout can be tricky. You should consider your system architecture as a whole.
You should carefully design a time-limiting strategy for all the integration points in your system to meet your software SLAs and guarantee a good user experience.

For read/query operations, it doesn’t matter much because they are idempotent. For write/command operations, you want to ensure proper handling when a
timeout expires, including providing the user with the correct status about the operation outcome.

## Retries
When a service downstream doesn’t respond within a specific time limit or replies with a server error related to its momentary inability to process the request, you can configure your client to
try it again.
Starting a sequence of retry attempts one after the other risks making the system even more unstable.
A better approach is using an *exponential backoff* to perform each retry attempt with a growing delay. By waiting for more and more time between one attempt and the next, it’s more likely the
backing service has had the time to recover and become responsive again. The strategy for computing the delay can be configured.

## Defining retries for WebClient
Project Reactor provides a retryWhen() operator to retry an operation when it fails. The position where you apply it to the reactive stream matters.
* Placing the *retryWhen()* operator after *timeout()* means that the timeout is applied to each retry attempt.
* Placing the *retryWhen()* operator before *timeout()* means that the timeout is applied to the overall operation (that is, the whole sequence of initial request and retries has to
  happen within the given time limit).

You can define the number of attempts and the minimum duration for the first backoff. The delay is computed for each retry as the current attempt number multiplied by the minimum
backoff period. A jitter factor can be used to add randomness to the exponential to each backoff.
By default, a jitter of at most 50% of the computed delay is used.

## Understanding how to use retries effectively
Retries increase the chance of getting a response back from a remote service when it’s momentarily overloaded or unresponsive. Use them wisely. In the context of timeouts, I
highlighted the need for handling read and write operations differently. When it comes to retries, it’s even more critical.
**You shouldn’t retry non-idempotent requests, or you’ll risk generating inconsistent states.**

When retries are configured in a flow where the user is involved, remember to **balance resilience and user experience**. You don’t want users to wait too much while retrying the request behind the
scenes. If you can’t avoid that, make sure you inform the users and give them feedback about the status of the request.

## Fallbacks and error handling
A system is resilient if it keeps providing its services in the face of faults without the user noticing it.
A fallback behavior can help you limit the fault to a small area while preventing the rest of the system from misbehaving or entering a faulty state.
You want to include fallbacks into your general strategy to make your system resilient, not just for a specific case like timeouts. A fallback function can be
triggered when some errors or exceptions occur, but they’re not all the same.

# Testing reactive applications with Spring, Reactor, and Testcontainers
- Test the data persistence layer with sliced tests using the *@DataR2dbcTest* annotation and Testcontainers, similarly to what is done with *@DataJdbcTest*.
- Tests for the web layer using the *@WebFluxTest* annotation, which works in the same way as *@WebMvcTest* but for reactive applications.

## Testing REST clients with a mock web server
The OkHTTP3 project provides a mock web server that you can use to test HTTP-based request/response interactions with a service downstream. 
You can use the convenient utilities provided by Project Reactor for testing reactive applications. The *StepVerifier* object lets you process reactive streams and write assertions in
steps through a fluent API.