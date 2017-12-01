import sys
import subprocess
import time
import random
import threading

def client_thread(i, move):
    #Create client, execute move on repeat
    subprocess.call("gnome-terminal -x sh -c \"java Game client%d server localhost; sleep 5; while true; do %d; done\"" % (i, move), shell=True)
    

if sys.argv[1] == "server":
    try:
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
            #create instance threads named client0, client1, etc. in a new window
            print("Creating peer thread %d" % i)
            move = random.randint(0,8)
            c_thread = threading.Thread(target=client_thread, args=(i, move))
            c_thread.start()
            #subprocess.call("gnome-terminal -x sh -c \"java Game client%d server localhost\"" % i, shell=True)
            #sleep to allow time for server to resolve connections
            time.sleep(5)
        print("%d peers created" % peer_count)
    except Exception as e:
        print(e)

else:
    print("Usage:\npython server - creates base server instance on localhost\npython clients [number] creates [number] of clients to connect to server instance")
