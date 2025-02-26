# Open source code repository for JSCAPE MFT Server 
 
 This repository contains the source files for functions and actions which are used in JSCAPE-MFT-Server.
 
 ## Note
 
 All the functions and actions are promoted and available from 12.5 version of JSCAPE MFT Server. 
 
 Please download the installer file here - https://www.jscape.com/downloads/jscape-mft-server 

 ## Getting Started

Below links help you in understanding custom Trigger Actions and Functions

```
- https://www.jscape.com/blog/how-to-create-custom-trigger-actions


- https://www.jscape.com/blog/how-to-create-custom-trigger-functions

```
### Prerequisites

What things you need to install the software and how to install them

```
- JDK 1.7 or JDK 1.8 - Download JDK 1.8 from here http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html 
- Ant 1.8 or above - Download the latest version from here https://ant.apache.org/
- JSCAPE MFT Server - Download the latest version from here https://www.jscape.com/downloads/jscape-mft-server
```


### Steps to clone the repository and build the source
```

Clone the repository from - https://github.com/jscape/mftserver.git after Clone the directory mftserver will contain folder structure as below;

- .git (D)
- src (D)
- libs (D)
- build.properties (F)
- build.xml (F)
- README.md (F)

D - Directory and F - File

Exceute the build using ant, On execute and build success two jars will be created in "dist" folder named below;

1. customfunctions.jar

2. customactions.jar

```

### Deloyment and Service restart
```

If you need only custom functions for JSCAPE MFT Server Copy the customfunctions.jar from "dist" folder to jscape-mft-server-installation-path/libs/ext.

If you need custom actions for JSCAPE MFT Server Copy the customactions.jar from "dist" folder to jscape-mft-server-installation-path/libs/actions.

Need both, Copy both the files as mentioned above.

Restart the server to get Custom actions and Custom functions inside JSCAPE MFT Server.

Note : client.cfg is mandatory and check if the file is located in jscape-mft-server-installation-path/etc folder. If the file does not exist execute the below command

./js-client-configuration -host [host] -port [port] -timeout [timeout in seconds] -user [username] -password [password] (Linux)

js-client-configuration.exe -host [host] -port [port] -timeout [timeout in seconds] -user [username] -password [password] (Windows)


```


### Functions and Actions in current Repository
```

The current Source has the below functions and actions;

Functions
*********

1.  IsHoliday

2.  AppendDateOrTimeFileName
 
3.  IsEndOfMonth

4.  IsServerKeyValid

5.  IsCertificateValid

6.  FileContentCompare
 
7.  GetDirectorySize

8.  GetDiskUsageInPercentile

9.  IsFileNewer

10. IsFileNewerByDate

11. IsFileNewerByTimeStamp

12. GetMaximumHeapInMb

13. GetUsedHeapInMb

14. GetFreeHeapInPercentile

15. GetHeapUsageInPercentile

16. FileContains

17. IsFileCreatedToday

Actions
******

1.  Clam Av Virus Scan

2.  Clam Av Virus update

3.  Avast Virus Scan

3.  Avast Virus update

4.  Kasper Sky Virus update

5.  Kasper Sky Virus Scan 

6.  Split Large Text File

7.  Export Users 

8.  Export Keys

9.  Get Logs

10. Copy Url To File

11. AS2 Messages Report

12. OFTP Messages Report

13. Failed Trigger Report

14. Trigger Report By Name

15. Set Active Instance Id

16. Replace Users Path

17. Trigger Report By Custom Date


```

### Documentation
```
Refer the link https://www.jscape.com/marketplace

```

## Authors

* **Hari Prasad** - *Initial work* - [hariprasadpkd](https://github.com/hariprasadpkd)

See also the list of [contributors](https://github.com/jscape/mftserver/graphs/contributors) who participated in this project.
