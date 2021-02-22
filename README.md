## Java runtime metadata analysis

Reflections scans your classpath, indexes the metadata, allows you to query it on runtime and may save and collect that information for many modules within your project.

Using Reflections you can query your metadata such as:
  * get all subtypes of some type
  * get all types/members annotated with some annotation
  * get all resources matching a regular expression
  * get all methods with specific signature including parameters, parameter annotations and return type

### Intro
Add Reflections to your project. for maven projects just add this dependency:
```xml
<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```

A typical use of Reflections would be:
```java
Reflections reflections = new Reflections("my.project");

Set<Class<? extends SomeType>> subTypes = reflections.getSubTypesOf(SomeType.class);

Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(SomeAnnotation.class);
```

### Usage
Basically, to use Reflections first instantiate it with urls and scanners

```java
//scan urls that contain 'my.package', include inputs starting with 'my.package', use the default scanners
Reflections reflections = new Reflections("my.package");

//or using ConfigurationBuilder
new Reflections(new ConfigurationBuilder()
     .setUrls(ClasspathHelper.forPackage("my.project.prefix"))
     .setScanners(new SubTypesScanner(), 
                  new TypeAnnotationsScanner().filterResultsBy(optionalFilter), ...),
     .filterInputsBy(new FilterBuilder().includePackage("my.project.prefix"))
     ...);
```
Then use the convenient query methods: (depending on the scanners configured)

```java
//SubTypesScanner
Set<Class<? extends Module>> modules = 
    reflections.getSubTypesOf(com.google.inject.Module.class);
```
```java
//TypeAnnotationsScanner 
Set<Class<?>> singletons = 
    reflections.getTypesAnnotatedWith(javax.inject.Singleton.class);
```
```java
//ResourcesScanner
Set<String> properties = 
    reflections.getResources(Pattern.compile(".*\\.properties"));
```
```java
//MethodAnnotationsScanner
Set<Method> resources =
    reflections.getMethodsAnnotatedWith(javax.ws.rs.Path.class);
Set<Constructor> injectables = 
    reflections.getConstructorsAnnotatedWith(javax.inject.Inject.class);
```
```java
//FieldAnnotationsScanner
Set<Field> ids = 
    reflections.getFieldsAnnotatedWith(javax.persistence.Id.class);
```
```java
//MethodParameterScanner
Set<Method> someMethods =
    reflections.getMethodsMatchParams(long.class, int.class);
Set<Method> voidMethods =
    reflections.getMethodsReturn(void.class);
Set<Method> pathParamMethods =
    reflections.getMethodsWithAnyParamAnnotated(PathParam.class);
```
```java
//MethodParameterNamesScanner
List<String> parameterNames = 
    reflections.getMethodParamNames(Method.class)
```
```java
//MemberUsageScanner
Set<Member> usages = 
    reflections.getMethodUsages(Method.class)
```

  * If no scanners are configured, the default will be used - `SubTypesScanner` and `TypeAnnotationsScanner`. 
  * Classloader can also be configured, which will be used for resolving runtime classes from names.
  
### ReflectionUtils
ReflectionsUtils contains some convenient Java reflection helper methods for getting types/constructors/methods/fields/annotations matching some predicates, generally in the form of *getAllXXX(type, withYYY)

for example:

```java
import static org.reflections.ReflectionUtils.*;

Set<Method> getters = getAllMethods(someClass,
  withModifier(Modifier.PUBLIC), withPrefix("get"), withParametersCount(0));

//or
Set<Method> listMethodsFromCollectionToBoolean = 
  getAllMethods(List.class,
    withParametersAssignableTo(Collection.class), withReturnType(boolean.class));

Set<Field> fields = getAllFields(SomeClass.class, withAnnotation(annotation), withTypeAssignableTo(type));
```
