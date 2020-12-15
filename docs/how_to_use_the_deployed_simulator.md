# How to use the deployed simulator without any setup

## To visit the web visualizer:

In your browser (chrome is preferred):
```
http://54.144.41.15/
```

## To use the interface to submit case for visualization or to get simulation results:

```
http://54.144.41.15/file
```

Or click the `Upload` tab on the navigation bar on the visualizer page.

## To Run the simulator in command line through SSH:
1. Download the ssh key file into a directory, we refer to the downloaded pem key file path `$SSH_KEY_PATH`.
Download link:
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