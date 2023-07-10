1.Open 2 cmds in each folder and run below command individually.

    javac Server.java
    javac Client.java
    
2.After compilation process, run below command in conrresponding terminals to start the each once

Start the server (command): java Server <port> 

        java Server 5000
        
Start the client (command): java Client <serverIP> <port> 

        java Client 192.168.10.2 5000

3.How to find the <serverIP>

"ipconfig" - type in terminal and get the IPv4 Address as serverIP

4.Type messages in the client CLI and press Enter to send them to the server.

5.To terminate the client, type "terminate" and press Enter.
