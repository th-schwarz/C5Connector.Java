# C5Connector.Java

The Java backend for the [filemanager of corefive](http://github.com/simogeo/Filemanager).

C5Connector.Java is distributed under the [MPL](http://www.mozilla.org/MPL/2.0/) Open Source licenses. The Open Source license is intended for:

* Integrating C5Connector.Java into Open Source software.
* Personal and educational use of C5Connector.Java.
* Integrating C5Connector.Java in commercial software while at the same time satisfying the terms of the Open Source license.

## Changes 

* 0.3
  * updated jackson to 2.2.2
  * updated slf4j to 1.7.5
  * fixed concurrency issues with reading image properties
  * issue #15 FilemanagerIconResolver isn't thread-save 
  * issue #14 IconResolver must respect the properties of 'icons'-section 
  * issue #13 Implementation of the upload properties
  * issue #11 Simplify the connector API 
  * issue #10 Revision of the JSON-responses
  * issue #9 A directory couldn't be deleted
  * issue #5 Configuration of the filemanager in an Java-style approach

* 0.2
  * restructured FilemanagerIconResolver to get an easier access for embedded applications
  * restructured the resolving of messages for the filemanager to get an easier access for embedded applications
  
* 0.1
  * compatible with the Servlet 3.0 specification
  * fixed scope of the dependency slf4j-nop
  * extended the UserActionMessageHolder to work with user-defined properties files

* 0.0.2
  * issue#4: Implementation of a 'UserPathBuilder' 
  * updated slf4j to 1.7.2
  * updated test-jetty-servlet to 8.1.9 
  
