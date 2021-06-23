## How to Use the Project
```bash
.
├── src
│   ├── main
│   │   └── scala
```

* The routes can be added in [LibraryRoute](src/main/scala/org/tmt/library/http/LibraryRoutes.scala).
Some example routes have been provided.
  
* The API implementation can be added in [LibraryImpl](./src/main/scala/org/tmt/library/core/LibraryImpl.scala).
This template provides an implementation that matches the example routes.

* Core models for supporting the APIs should be added in the [models](src/main/scala/org/tmt/library/models) package.
Codecs for these models should be added in [HttpCodecs](./src/main/scala/org/tmt/library/http/HttpCodecs.scala).

* [LibraryWiring](./src/main/scala/org/tmt/library/LibraryWiring.scala) is where the implementation wired up with the routes.

* [LibraryApp](./src/main/scala/org/tmt/library/LibraryApp.scala) is the main runnable application. The command line arguments 
for starting the app are defined in [LibraryAppCommand](./src/main/scala/org/tmt/library/LibraryAppCommand.scala). Any new command  
or option for command can be added like so:
```
 @CommandName("<command_name>")
  final case class <command_name>(
     @HelpMessage("<help message>")
     @ExtraName("<option>")
     option: <type>
   ) extends SampleAppCommand
```
* The newly added command/options need to handled in LibraryApp

* Any new application specific configuration can be added in [application.conf](./src/main/resources/application.conf)
