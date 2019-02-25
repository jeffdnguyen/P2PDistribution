# CSC 401 Networking Project 1

A P2P network for tranfering RFC files ([full description](https://moodle-courses1819.wolfware.ncsu.edu/pluginfile.php/1154226/mod_assign/introattachment/0/proj1.pdf))

## Created By
* Jeff Nguyen (jnguyen8)
* Mac Chaffee (machaffe)
* Wynne Plaga (rwplaga)

## How to Run Task 1
1. In the folder `rfcs/`, create folders called `8000`, `8001`, `8002`, `8003`, `8004`.
2. Move all 60 RFC files into folder `8000`
3. In one terminal window, execute `edu.ncsu.NetworkingProject.proj1.java` and run the Registration Server
   ```
   Which service would you like to run?
   1) Registration Server
   2) Peer(s) 
   1
   ```
4. In another terminal window, execute `edu.ncsu.NetworkingProject.proj1.java` and run five Peers.
   ```
   Which service would you like to run?
   1) Registration Server
   2) Peer(s) 
   2
   How many peers do you wish to run?
   5
   ```
5. Inspect the 5 folders you created. Each folder should contain all 60 RFCs.
