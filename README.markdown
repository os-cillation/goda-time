## Goda-time - fork for use with GWT and maven

[Goda-time](https://code.google.com/p/goda-time/) is a modified version of Joda-time, which works with the restricted Java API of GWT.
The project has been modified to be easily integratable in a project using maven.


#### Usage:

1. If for some reason you don't want to go through maven central, clone the repository and install the maven plugin:

    ```bash
    git clone git@github.com/os-cillation/goda-time.git
    cd goda-time
    mvn install
    ```

2. Add the following lines to your `pom.xml`:

    ```xml
    <dependency>
        <groupId>de.oscillation.gwt</groupId>
        <artifactId>goda-time</artifactId>
        <version>0.0.1</version>
    </dependency>
    ```

3. Add the dependency to your GWT config:

    ```xml
    <!-- Goda-time library -->
    <inherits name="org.goda.Goda" />
    ```
