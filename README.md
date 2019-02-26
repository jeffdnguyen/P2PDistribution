# CSC 401 Networking Project 1

A P2P network for tranfering RFC files ([full description](https://moodle-courses1819.wolfware.ncsu.edu/pluginfile.php/1154226/mod_assign/introattachment/0/proj1.pdf))

## Created By
* Jeff Nguyen (jnguyen8)
* Mac Chaffee (machaffe)
* Wynne Plaga (rwplaga)

## How to Run Task 1
1. In the folder `rfcs/`, create a folder called `8000`.
2. Move all 60 RFC files into folder `8000`
3. In a terminal window, execute `edu.ncsu.NetworkingProject.proj1.java` and tell it to create 6 peers.
   ```
   What is the IP of the RegServer? (or enter 'localhost' if you wish to run it locally)
   localhost
   Registration server started successfully.
   How many peers do you wish to run?
   6
   ```
4. There should now be 6 folders in `rfcs/` and each folder should contain all 60 RFCs.
