# changelog

* 0.11
  * issue #39: Adapt FilemanagerConfig to Filemanager 2.5.
  * issue #40: Integrate the filemanager source into the jar file
  * issue #41: New directory with name equals to Parent directory
  * issue #43: The default implementation of FilemanagerCapability should respect if a file is protected 
  * issue #44: updated filemanager to 2.5.0 
  * issue #45: wrong path in getinfo for folders 

* 0.10
  * issue #36: Client and adapter tight coupling (refactored the fetching of the default configuration of the filemanager)
  * issue #37: Implementation of 'baseUrl' property
  * issue #38: Build a bundle with the filemanager source
  * updated Jackson to 2.5.4

* 0.9
  * issue #33: Implementation of 'version' property
  * issue #34: Implementation of the 'protected' flag for get-responses
  * issue #35: Implement sorting files in 'getfolder'
  * updated Jackson to 2.5.0
  * updated slf4j to 1.7.12
  * updated servlet-api to 3.1.0

* 0.8
  * issue #30: filemanager.config.js.default isn't loaded
  * issue #32: Adapt FilemanagerConfig to Filemanager 2.0

* 0.7
  * issue #29: extend the shared-config with option#theme 
  * internal: global refactoring, refactored the upload code 
  * issue #25: Implementation of the mode 'preview'
  * issue #26: introduce a property to hold the max. preview dimension
  * issue #28: introduce a remover for EXIF data

* 0.6
  * issue #21: Implementation of 'replace'
  * internal: refactored the Dispatcher
  * issue #22: change name of the base package to codes.thischwa
  * issue #23: replacing an image doesn't have any effect on preview/thumbnails
  * updated slf4j to 1.7.7
  
* 0.5
  * issue #20: All regex from the 'shared' config have to be moved to the properties file

* 0.4
  * issue #7:  Implementation of the excludes#unallowed properties
  * issue #17: Implementation of thumbnailing 
  * issue #18: implementation of 'savefile'
  * issue #19: implementation of 'editfile'

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
  
