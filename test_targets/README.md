# Consensus Protocol Simulator & Visualizer Project
Author: Yingjing Lu

Andrew ID: yingjinl

## Project submission folder structure
***
```
.
+-- report.pdf                      // the project report
+-- presentation.pdf                // the project presentation slides
+-- 821-node-server.pem             // the SSH key to access the deployed simulator on AWS
+-- code                            // source code folder
|   +-- blockchain-simulator        // the simulator source code
|   +-- Blockchain-Backend          // the visualizer backend source code
|   +-- BlockChain-UI               // the visualizer UI source code
+-- javadoc                         // full javadoc for the simulator
|   +-- index.html                  // the access point of all javadoc
+-- cases                           // our preconfigured cases and their .zip file for you to do quick run on simulator or submit through UI
|   +-- dolevstrong_n_3_f_1_r_1     // one of the case folder
|   |   +-- config.json
|   +-- dolevstrong_n_3_f_1_r_1.zip // case folder zipped for uploading to web visualizer to run
```

# Run simulator and explore visualizer installation free

This is a mirror section in the report section "How to use the deployed simulator without any setup"

### Walk through video on how to use the simulator from deployed server
---
Web Interface
```url
https://www.youtube.com/watch?v=rMWJqBhDqgg
```

SSH to run simulator
```url
https://www.youtube.com/watch?v=-vBiecf7PV4
```

### To visit the web visualizer:

In your browser (chrome is preferred):
```
http://54.144.41.15/
```

### To use the interface to submit case for visualization or to get simulation results:

```
http://54.144.41.15/file
```

Or click the `Upload` tab on the navigation bar on the visualizer page.

### To Run the simulator in command line through SSH:
---
1. Download the ssh key file into a directory, we refer to the downloaded pem key file path `$SSH_KEY_PATH`.

Download link (or to use the one provided in the submission folder):
```bash
https://drive.google.com/file/d/1lW-6-fV1_pCMb7MidKXcXspLowtg1xXa/view?usp=sharing
```

2. change the permission of this file
```bash
chmod 400 $SSH_KEY_PATH
```

3. ssh to the server and cd to the simulator path:
```bash 
ssh -i $SSH_KEY_PATH ubuntu@ec2-54-144-41-15.compute-1.amazonaws.com
cd /home/ubuntu/project/blockchain-simulator
```
4. There are some sample configurations you can run:
```
ls /home/ubuntu/project/blockchain-simulator/src/config
```
5. run one of the sample by adding the folder name:
we use case `dolevstrong_n_3_f_1_r_2` as an example, config is 3 players 1 corrupt and protocols run round 0, 1, 2. So despite the fact that corrupt sender trys to attack, the honest players 0 and 1 will output FLOOR BIT:
```bash
java -cp target/blockchain-simulator-1.0-SNAPSHOT-jar-with-dependencies.jar com.blockchain.simulator.App 'src/config/dolevstrong_n_3_f_1_r_2'
```

6. See running results in `stdout`, or go to the folder to see traces. two honest players output floor bits.
```bash
Player : 0 output: F
Player : 1 output: F
```

# Installation (LINUX)
## Simulator
---
**This is meant to be a quick run introduction based on the submission folder structure. For full setup detail on setting up both the simulator and the Web interface from blank environment, we direct you to the report sections** 

Installing java and maven for compilation
```bash
sudo apt update
sudo apt install default-jre
sudo apt install maven
```

Compile the simulator and run a sample case
```bash
cd code/blockchain-simulator
mvn clean package assembly:single
java -cp target/blockchain-simulator-1.0-SNAPSHOT-jar-with-dependencies.jar com.blockchain.simulator.App ../../cases/dolevstrong_n_3_f_1_r_1
```

And you can see a result printed out in the command line and if you go to the `dolevstrong_n_3_f_1_r_1` folder you will see full dumped results

## Installing the Blockchain UI and backend
***

## Installing the backend
---
First, please make sure that the backend resides in the same folder as the blockchain simulator:

```
code
|-blockchain-simulator
|-Blockchain-Backend
|-BlockChain-UI
```

First install `Node.js`
```
curl -sL https://deb.nodesource.com/setup_14.x | sudo -E bash -
sudo apt-get install -y nodejs
```

```bash
cd Blockchain-Backend
sudo npm install
npm install archiver --save
```

Now you can run the server for local host:
```bash
npm start
```

## Installing the UI
Start another terminal
```bash
cd BlockChain-UI
sudo npm install
```

Change the line in `src/Global.js` from top to the bottom one:
```
const SERVER = 'http://54.144.41.15/';
const SERVER = 'http://localhost:4500/';
```

Now you can test the server from localhost using 
```bash
PORT=3000 npm start
```

Then when the browser pop up the window, you will find the home page posted
