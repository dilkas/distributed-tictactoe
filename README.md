# How To Run

First compile the code:

`javac *.java`

Then, using separate terminals, run the RMI Registry and several clients:

`rmiregistry`

`java Game server client localhost`

`java Game client server localhost`

`java Game client2 server localhost`

N.B.: A new player can connect to any running instance, so a 4th
instance could connect to either one of server, client, and client2.
