# Pluto 
[![Build Status](https://secure.travis-ci.org/toberkoe/pluto.png)](http://travis-ci.org/toberkoe/pluto) 
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.toberkoe%3Apluto&metric=coverage)](https://sonarcloud.io/dashboard?id=de.toberkoe%3Apluto) 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de.toberkoe%3Apluto&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.toberkoe%3Apluto)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.toberkoe/pluto/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.toberkoe/pluto)

*JUnit5 Extensions for easier unit and integration tests*

## Latest release

The most recent release is [pluto 0.2][current release], released 2018-03-15.

The Maven group ID is `de.toberkoe`, and the artifact ID is `pluto`. Use
version `0.3` for the current version.

To add a dependency on fluent-assertions using Maven, use the following:

```xml
<dependency>
  <groupId>de.toberkoe</groupId>
  <artifactId>pluto</artifactId>
  <version>0.2</version>
  <scope>test</scope>
</dependency>
```

To add a dependency using Gradle:

```
dependencies {
  testImplementation 'de.toberkoe:pluto:0.2'
}
```

## Features

#### MockExtension

This extension helps you with injecting mocks. 
Before each test, the extension calls MockitoAnnotations.initMocks.

``` java
@ExtendWith(MockExtension.class)
class MockExtensionTest {

    @Mock
    private EntityManager manager;

    @InjectMocks
    private PersonRepository repository;

    @Test
    void testCreatePerson() {
        doReturn(new Person()).when(manager).merge(any(Person.class));
        assertThat(repository.create(new Person())).isNotNull();
    }
}
```

#### PersistenceExtension

This extension offers an alternative to Arquillian and should be used for integration tests.
It is configurable via annotations and creates a standalone persistence container with an in-memory database.
The following minimalistic example shows some of the available features.

###### Code to be tested

**Entity Class:**
```java
@Entity
public class Person {

    @Id
    private long id;
    
    @NotNull
    private String name;
    
    //constructor, getter, setter
}
```

**Entity Repository:**
```java
public class PersonRepository {
    
    @PersistenceContext
    EntityManager manager;
    
    public Person save(Person person) {
        return manager.merge(person);
    }
}
```

**Person Service:**
```java
@Stateless
public class PersonService {

    @Inject
    PersonRepository repository;
    
    public Person create(String name) {
        Person person = new Person(name);
        return repository.save(person);
    }
    
}
```

###### Integration test with PersistenceExtension 

```java
@ExtendWith(PersistenceExtension.class)
class PersonServiceIT {

    @Inject
    PersonService service;
    
    @Test
    void testCreatePerson() {
        Person person = service.create("Duke");
        assertThat(person).isNotNull();
        assertThat(person.getId()).isPositive();
    }
    
}
```

The extension automatically resolves the required entities and starts the persistence container.
For more complex test cases some of the following annotations might help:

[//]: # (Link rows in table to wiki pages)

Annotation | Description
--- | ---
Log | Change the log level. Default is Log.Level.OFF.
UseDatabase | Change the used database. Default is Database.HSQLDB.
EntityDiscoveryMode | Change the entity discovery mode. Default is Strategy.DYNAMIC.
EntityClassProvider | Annotation to flag static methods, that are providing the required entity classes.

For further information you can check the wiki or the source. This lib is still in development. If you miss a feature please provide a new issue.

[//]: # (Wiki)

[current release]: https://github.com/toberkoe/pluto/releases/tag/0.2