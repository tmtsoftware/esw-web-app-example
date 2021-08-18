# Web Application Tutorial

This tutorial consists of a set of tutorials to create a Web Application using the ESW Web App 
[template](https://github.com/tmtsoftware/esw-web-app-template.g8).  

The application created in this tutorial is an coordinate formatter, and which will provide a form for inputting the 
right ascension and declination of a coordinate in decimal form, process it (in the backend HTTP service), and display
the formatted sexagesimal result in the UI.

The tutorial has been divided
into 4 main flows that incrementally demonstrated the constuction of a full web application, consisting of an
HTTP service backend and a React-based user interface.

The basic flow in "Creating a Web Application" will show you how to add a routes to backend application and consume them in your frontend.
The following flows are optional but demonstrate additional features to make your application more complete.

At any point in time if you want to see the completed code, you can refer final [example app](https://github.com/tmtsoftware/esw-web-app-example).

@@toc { depth=1 }
@@@ index

- @ref:[Creating a Web Application](flows/base-flow.md)
- @ref:[Adding Authentication](flows/auth-flow.md)
- @ref:[Adding Database Persistence](flows/db-flow.md)
- @ref:[Using Paradox Documentation](flows/docs-flow.md)

@@@
