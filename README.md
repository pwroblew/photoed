# Command line image editor

## Short description

This is a version of photo editor suggested by Danie Ciocirlan in his course (link below).
Here however, the application is an `IOApp` application and everything is purely functional.
The main goal of this project was to demonstrate using of cats-effect's IO for this kind of applications
rather than an extensive set of image manipulation functions.

Links:

- https://github.com/rockthejvm/scala-projects/tree/master/photoscala/src/main/scala/com/rockthejvm/photoscala
- https://rockthejvm.com/courses/scala-projects

## Usage

To build it, use standard sbt commands:

```
sbt clean
sbt scalafmt
sbt scalafmtCheckAll
sbt compile
sbt test
sbt package
sbt assembly
```

### Running it

Either by sbt:

```
sbt run
```

or from assebled jar:

```
java -jar target/scala-3.x.x/photoed-assembly-x.y.z.jar
```

You provide commands to the application and then each command is parsed and executed.
The application keeps its changing state, so it evolves while you provide more commands.

Sample usage:

```
java -jar target/scala-3.8.3/photoed-assembly-0.1.0-SNAPSHOT.jar
Please provide a command: load src/main/resources/birdie.png
Please provide a command: invert
Please provide a command: grayscale
Please provide a command: invert
Please provide a command: exit
```

## Input format

The app can read standard format of image files.
However it can write to a file only: jpeg, png or gif.
