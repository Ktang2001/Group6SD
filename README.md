# Instructions

Project is currently bare bones. You will need 2 Terminal or Command line windows to run it. 

## *Make sure MySQL is running before you try to run the program.*

After you clone the repository and have the program loaded into your IDE do the following:

- In DatabaseManager.java, Change USER and PASS to your personal login information for MySQL.
- Save All
- Open two Terminal or Command line windows and navigate to the project folder.
# In the first window run the following command:
## Windows: 
```
  gradlew.bat runHost 
```
## Mac/Linux
```
  ./gradlew runHost
```
# In the Second window run the following command:
## Windows: 
```
  gradlew.bat runClient 
```
## Mac/Linux
```
  ./gradlew runClient
```

You should have two GUIs now. You can create two user accounts and then log in to them. Once logged in, you will see a space where it shows Stats, and two buttons at the bottom
of the GUI. Select Create Match in one window, and then select Join Match in the other window. The Game will launch.

# Note

- If you are testing the game on one machine, you will notice that you can only control whichever window you select, and at the moment only one character moves. I believe the functionality
for both characters to move is coded but i am unable to test using one machine.

- At the moment the only functionality is that you can move a player left and right, and it will show the movement updates in both GUIs.

