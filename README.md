# MyRMI - Kotlin impl of JRMI

This project is an assignment for SUSTech CS328,
 targeting to implement a simple RMI framework.

The whole design is based on the Java RMI framework,
 and the interface is designed to be compatible with Java RMI.

Different from Java RMI, this RMI framework allows exported objects
to be registered in a remote registry, as long as the registry server
holds the same interface as the exported object in the classpath.
To make exported objects accessible for other clients,
the host ip should be specified when exporting the objects.

For more information, please refer to the [assignment report](Assignment_2_report.pdf).

While the project is written in Kotlin, the interface is designed to be Java-friendly.
Usages for both Java and Kotlin have been tested in the `src/test` directory.