# How to install and run the simulator

Walkthrough of installation and running example of the simulator on x86 Ubuntu as an example

**For configuring protocol and how to interpret the program output result, please refer to the section for that.**

## Installation

Installing java and maven for compilation
```bash
sudo apt update
sudo apt install default-jre
sudo apt install maven
```

Verify for installation
```bash
java -version
mvn -version
```

Create a root project folder (for both simulator and UI)
```bash
mkdir project && cd project
```

Clone the simulator repo and UI repo (UI installation and running is on another section)
```
git clone https://github.com/YingjingLu/blockchain-simulator.git
git clone https://github.com/YingjingLu/BlockChain-UI.git
git clone https://github.com/YingjingLu/Blockchain-Backend.git
```

Compile the project
```bash
cd blockchain-simulator
mvn clean package assembly:single
```

Run a sample case:

In the project folder there is a ```config``` folder, inside there is a folder named ```dolevstrong_n_4_f_2_r_2``` we denote the absolute path of this folder as DOLEV_PATH (this is a dolev strong protocol with 4 players 2 of them corrupt and runs for 2 rounds except for round 0 when sender sends the message to other nodes. In our case we picked the sender to be corrupt)

```bash
java -cp target/blockchain-simulator-1.0-SNAPSHOT-jar-with-dependencies.jar com.blockchain.simulator.App DOLEV_PATH
```

It will print out the output in the terminal, when you go to that folder, you will see an ```output.json``` generated with the output state
```json
{
    "0":"1",
    "1":"0"
}
```
Which stands for player ID 0 outputs 1 and player id 1 outputs 0, those two are the honest players

Similarly for streamlet you can find in ```config``` folder a case ```streamlet_n_4_f_0_r_4``` denote the absolute path to that folder as STREAMLET_PATH
```bash
java -cp target/blockchain-simulator-1.0-SNAPSHOT-jar-with-dependencies.jar com.blockchain.simulator.App STREAMLET_PATH
```
It will also print out the players' states at each round's end in the command line
for example at round 3 it prints:
```
Round 3
HonestPlayer:
Player: 0
G**-|
3*-2**-1**-0**-G**-|
Player: 1
G**-|
3*-2**-1**-0**-G**-|
Player: 2
G**-|
3*-2**-1**-0**-G**-|
Player: 3
G**-|
3*-2**-1**-0**-G**-|
```

Where each "-|" denotes a start of the chain and each line denotes a brach of the chain "*" one star denotes the block being notarized and "**" denotes the block being finalized number denotes epoch number also the block id.

Another way to fully view the execution and player output would to inspect the output files in that folder after the execution. Which would be talked about in the other section mentioning configuration and outputs.

