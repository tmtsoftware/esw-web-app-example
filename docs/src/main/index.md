# ESW Web Application Tutorial

In the ESW user interface design, a web application is used when the user interface has special requirements that are not
met by the interfaces to the control system provided by the User Interface Gateway. For instance, a web application
should be used if specific application processing is needed or the UI needs results from a database using CSW Database
Service.

Creating a web application is not trivial and requires the programmer to understand several technologies as well as the
ESW authentication and authorization system. Therefore, it is recommended that this approach be taken only when
necessary.

## Tutorial Welcome

This tutorial uses a set of smaller tutorials to create a Web Application using the ESW Web Application [template](https://github.com/tmtsoftware/esw-web-app-template.g8).

The application created in this tutorial is a coordinate formatter and application-specific backend service. The browser-based user interface (UI) has a form for inputting the 
right ascension and declination of an astronomical coordinate in decimal form. The coordinate is then submitted to a backend HTTP service (also created in this tutorial), 
using an application-specific route, where it is processed and returned as a formatted sexagesimal position. The returned result is then displayed in the UI.

The tutorial has been divided
into 4 main flows that incrementally demonstrate the construction of a full web application consisting of an
HTTP service backend and a React-based user interface.

The basic flow in "Creating a Web Application" will show you how to add a route to the backend application and consume them in your frontend.
The flows following the basic flow are optional but demonstrate additional features that may be needed to make an application more complete.

If at any point in time you want to see the completed tutorial, you can view the final code [here](https://github.com/tmtsoftware/esw-web-app-example).

@@toc { depth=1 }
@@@ index

- @ref:[Creating a Web Application](flows/base-flow.md)
- @ref:[Adding Authentication](flows/auth-flow.md)
- @ref:[Adding Database Persistence](flows/db-flow.md)
- @ref:[Using Paradox Documentation](flows/docs-flow.md)

@@@
