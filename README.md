# CSC 401 Networking Project 1

A P2P network for tranfering RFC files ([full description](https://moodle-courses1819.wolfware.ncsu.edu/pluginfile.php/1154226/mod_assign/introattachment/0/proj1.pdf))

## Created By
* Jeff Nguyen (jnguyen8)
* Mac Chaffee (machaffe)
* Wynne Plaga (rwplaga)

## How to run Task 1
1. In the folder `rfcs/`, create a folder called `8000`.
2. Move all 60 RFC files into folder `8000`
3. In a terminal window, execute `edu.ncsu.NetworkingProject.proj1.java` and tell it to create 6 peers.
   ```
   What is the IP of the RegServer? (or enter 'localhost' if you wish to run it locally)
   localhost
   Registration server started successfully.
   Are you running the "Testing Scenario"? (y/n)
   n
   How many peers do you wish to run?
   6
   ```
4. There should now be 6 folders in `rfcs/` and each folder should contain all 60 RFCs.

## How to run Task 2
1. In the folder `rfcs/`, create folders called `8000`, `8001`, `8002`, `8003`, `8004`, `8005`
2. Put 10 different RFCs in each folder
3. Follow steps 3 and 4 of the above instructions.

## How to run the Testing Scenario
1. In the folder `rfcs/`, create folders called `8000` and `8001`. Put two RFCs in folder `8000` and none in folder `8001`
2. In a terminal window, execute `edu.ncsu.NetworkingProject.proj1.java` and tell it you want to run the testing scenario
   ```
   What is the IP of the RegServer? (or enter 'localhost' if you wish to run it locally)
   localhost
   Registration server started successfully.
   Are you running the "Testing Scenario"? (y/n)
   y
   ```
3. There should now be one RFC in folder `8001`

