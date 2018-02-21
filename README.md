# Pluto 
[![Build Status](https://secure.travis-ci.org/toberkoe/pluto.png)](http://travis-ci.org/toberkoe/pluto) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=de.toberkoe.tools%3Apluto&metric=coverage)](https://sonarcloud.io/dashboard?id=de.toberkoe.tools%3Apluto) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=de.toberkoe.tools%3Apluto&metric=alert_status)](https://sonarcloud.io/dashboard?id=de.toberkoe.tools%3Apluto)

[//]: # (add badges for maven central) 

*JUnit5 Extensions for easier unit and integration tests*

## Examples

### MockExtension

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


[//]: # (PersistenceExtension)

[//]: # (Latest News)

[//]: # (About)

[//]: # (Quickstart Maven and Gradle)

[//]: # (Wiki)
