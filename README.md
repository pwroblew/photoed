
# PhotoEd — Command-Line Image Editor

PhotoEd is a command-line image editor written in Scala using Cats Effect.

The project is based on the photo editor application proposed by Daniel Ciocîrlan in the Rock the JVM Scala Projects course. Unlike the original version, PhotoEd is implemented as an `IOApp`, with application logic and state management expressed using purely functional programming techniques.

The main goal of the project is not to provide a comprehensive image-editing toolkit. Instead, it demonstrates how Cats Effect's `IO`, `Ref`, `Resource`, and other functional abstractions can be used to build an interactive application that combines:

- command-line input,
- mutable application state represented safely with `Ref`,
- image loading and saving,
- Swing windows,
- resource management,
- and communication between the Cats Effect runtime and the Swing Event Dispatch Thread (EDT).

Additional image-processing operations may be added in the future.

## Inspiration

The project was inspired by:

- PhotoScala source code: https://github.com/rockthejvm/scala-projects/tree/master/photoscala/src/main/scala/com/rockthejvm/photoscala
- Rock the JVM – Scala Projects: https://rockthejvm.com/courses/scala-projects

## Building the application

To create an executable assembly JAR, run:

```bash
sbt assembly
````

Other useful SBT commands include:

```bash
sbt clean
sbt scalafmtCheckAll
sbt compile
sbt test
sbt package
sbt assembly
```

## Running the application

Run the application directly with SBT:

```bash
sbt run
```

Or execute the assembled JAR:

```bash
java -jar target/scala-3.x.x/photoed-assembly-x.y.z.jar
```

For example:

```bash
java -jar target/scala-3.8.3/photoed-assembly-0.1.0-SNAPSHOT.jar
```

## Usage

PhotoEd is an interactive command-line application. Commands are entered in the terminal, parsed, and executed sequentially.

The application maintains an internal state containing loaded images and their associated windows. This state evolves as commands are executed.

To get started, enter:

```text
help
```

Most commands also support a `help` subcommand that explains their syntax.

### Example session

```text
❯ java -jar target/scala-3.8.3/photoed-assembly-0.1.0-SNAPSHOT.jar
>>    Please provide a command: load birdie2.png
>>    An error encountered. Details: syntax: load <filename> <img-id>
>>    Please provide a command: load birdie2.png b2
>>    Please provide a command: load flowers.png f1
>>    Please provide a command: invert b2
>>    Please provide a command: greyscale f1
>>    Please provide a command: hide f1
>>    Please provide a command: close b2
>>    Please provide a command: status
>>    img-id:[b2]  |  img-loaded:[YES]  |  window:[NO]   |  being-shown:[NO]
>>    img-id:[f1]  |  img-loaded:[YES]  |  window:[YES]  |  being-shown:[false]
>>    Please provide a command: show b2
>>    Please provide a command: show f1
>>    Please provide a command: save b2
>>    An error encountered. Details: syntax: save <img-id> <filename>
>>    Please provide a command: save b2 birdie-inverted.jpg
>>    Please provide a command: save f1 grey-flowers.gif
>>    Please provide a command: history
>>        load birdie2.png
>>        load birdie2.png b2
>>        show b2
>>        display b2
>>        load flowers.png f1
>>        show f1
>>        display f1
>>        invert b2
>>        display b2
>>        greyscale f1
>>        display f1
>>        hide f1
>>        close b2
>>        status
>>        show b2
>>        display b2
>>        show f1
>>        display f1
>>        save b2
>>        save b2 birdie-inverted.jpg
>>        save f1 grey-flowers.gif
>>    Please provide a command: exit
```

## Supported image formats

PhotoEd can read image formats supported by Java's `ImageIO` API.

Images can currently be written in the following formats:

* JPEG
* PNG
* GIF

The output format is inferred from the target file extension.

```
```
