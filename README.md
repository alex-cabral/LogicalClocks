# LogicalClocks
A Java implementation of Lamport's logical clocks on a set of virtual machines.

To run the program, download all of the .java files and the run_vms.sh file.

Then open two terminal windows - one for the server and one for the virtual machines, and navigate to the directory storing the downloaded files.

In the server terminal window, type the following line:

``
javac Server.java && java Server <port>
``
For the port number, any integer value between 5000 and 9000 should work.

In the other terminal window, type the following line:

```
./run_vms.sh  <host> <port>
```

where the host is your local host, found by typing `ifconfig` in the terminal window and copying the IP address listed at `lo0`. The port must be the same port as you chose for the server.

At this point, you should see output telling you that the Virtual Machine instances are running. You will also get console messages when they are done running. At this point, you can quit the server program as well.

The logs for each VM are stored in text files, with the number in the file matching the id of the VM. All of the log files we generated can be found in the folder LogFiles.
