import sys
import subprocess
import time

if sys.argv[1] == "server":
    try:
        #run rmiregistry in the background
        subprocess.call("rmiregistry &", shell=True)
        #create base server instance
        subprocess.call("java Game server client localhost", shell=True)
        print("Server created")
    except Exception as e:
        print(e)

elif sys.argv[1] == "clients":
    try:
        #number of peers to create in the session
        peer_count = int(sys.argv[2])
        for i in range(peer_count):
            #create instances named client0, client1, etc. in a new window
            print("Creating peer %d" % i)
            subprocess.call("gnome-terminal -x sh -c \"java Game client%d server localhost\"" % i, shell=True)
            #sleep to allow time for server to resolve connections
            time.sleep(5)
        print("%d peers created" % peer_count)
    except Exception as e:
        print(e)

else:
    print("Usage:\npython server - creates base server instance on localhost\npython clients [number] - creates [number] of clients to connect to server instance")
