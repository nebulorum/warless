WarLess
========

This is a simple module to solve a small problem. I have a Swing application that has a part that is handled as a web
application. I distribute all of it's modules as Jar files. However to run the application with a War it will be
expanded into a temporary directory, normally outside of my application directory. I'd like to have a warless
environment that can understand and expand only the needed files to a specific directory, while maintaining the War like
application embedded in my larger Swing application.

## The goals for this module are:

* Allow extraction of static content and not all classes from Jar archive;
* Control target directory and avoid extracting when not needed;
* Selectively run from development environment (directly from IDE) or from archive;
* Allows launching of web server within and embedded application.

## Usage

In order to use this module in a project:

1. Package your application as you would War, however generate a Jar and include in the file all the static
files as resources.
<pre>
    myArchive.jar
    |---org
    |   `-- exnebula
    |       `-- app
    |           `-- SomeClass.class
    `-- webapp
        |-- index.html
        |-- WEB-INF
        |   `-- web.xml
        |-- lib
        |   `-- coollib.js
        `-- images
            `-- logo.png
</pre> 

1. Create a Warless object passing it the archive handle (from a class in the archive) and the target directory.
```java
  WarLess warLess = new WarLess(
      WarArchive.create(SomeClassInJar.class, "webapp"),
      new WarTarget(new File("my/target/directory")));
```
   The `"webapp"` paramater refers to the subdirectory in the jar that houses all the static field. You can have any
   directory but remember that
1. Ask warless to resolve the target (this will expand if needed)
```java    
    warLess.resolve();
```
  Resolving will unzip the Jar or point to the directory in the build target. To know the final directory use: `warless.getAppDirectory()`

1. Setup you jetty as if it where a dev environment, in this case we assume we have a wrapper class called `server`.
```java 
    server.configureWar(warLess.getTargetDirectory().getAbsoluteFile());
```