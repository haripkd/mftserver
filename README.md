# Open source code repository for JSCAPE MFT Server 
 
 This repository contains the source files for functions and actions which are used in JSCAPE-MFT-Server.

 ## Getting Started

Below links help you in understanding custom Triggers and Functions

```
- https://www.jscape.com/blog/how-to-create-custom-trigger-actions


- https://www.jscape.com/blog/how-to-create-custom-trigger-functions

```
### Prerequisites

What things you need to install the software and how to install them

```
- JDK 1.7 or above
- Ant 1.8 or above
- JSCAPE MFT Server - Download the latest version from here https://www.jscape.com/downloads/jscape-mft-server
```


### Steps to clone the repository and build the source
```

Clone the repository from - https://github.com/jscape/mftserver.git after Clone the directory mftserver will contain folder structure as below;

- .git (D)
- com (D)
- libs (D)
- build.properties (F)
- build.xml (F)
- README.md (F)

D - Directory and F - File

After Cloning make a Directory named "src" and move the direcory "com" to "src"; Once done the Final folder structure will be as below;

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

```


### Functions and Actions in current Repository
```

The current Source has the below functions and actions;

Functions
*********

1. IsHoliday

Actions
******

1. ClamAV Virus Scan

2. ClamAV Virus update

3. Avast Virus Scan

3. Avast Virus update

4. KasperSky Virus update

5. KasperSky Virus Scan 


```

## Authors

* **Hari Prasad** - *Initial work* - [hariprasadpkd](https://github.com/hariprasadpkd)

See also the list of [contributors](https://github.com/jscape/mftserver/graphs/contributors) who participated in this project.
