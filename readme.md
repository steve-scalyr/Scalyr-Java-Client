# Scalyr Java Client Library

This is a fork from Scalyrs Java client library https://github.com/steve-scalyr/Scalyr-Java-Client
with the Scalyr Appender added. The Scalyr java client official repo has been removed from github and they are not actively supporting 
their scalyr logback appender.
When upgrading to Java 17 the scalyr appender had a breaking bug caused by JVM changes to CompletableFutures & ThreadLocal variables 
which made the appender unusable with CompletableFutures. https://bugs.openjdk.org/browse/JDK-8285638

To solve this we forked the repository, bumped it to Java 17 and fixed the issue with ThreadLocals. 

## Build
There is no pipeline for this library since it wont be built often. Instead you can manually push it to the boss azure maven repo.