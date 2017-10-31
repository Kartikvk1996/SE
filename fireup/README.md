## Fireup 
*a process to manage processes on different nodes in network*

Fireup is an utility which can __create, kill, manage__ processes in different machines in the same network. It uses a simple protocol (explained below). The main use of this piece of this software is to automate the creation and management of processes distributed for computing.

### How to use.
1. Set up *__Fireup__* on all machines where you want to manage distributed processes.
2. Assign one of the node(machine) as *__master__*.
3. Start the *__master__* process with appropriate commandline arguments by choosing the executable or executing the *__fup__* protocol.
4. Now connect the all *__Fireup__* applications to *__master__*.
5. Let the master enjoy managing of the processes in different nodes.


### fup protocol.

Well *__master__* needs to understand a simple protocol called *__fup__*. The following are the features.

1. command based protocol (commands are exchanged)
2. One command per line.
3. command has following format 
    > `command arg1 arg2 arg3`  
    Here command can be one of the following (Case Insensitive).

    | Command | Meaning |
    |---------|---------|
    | create  | Create a process first argument is process name followed by arguments to the process itself |
    | kill    | Kill all processes in the system |
4. the application returns a status defined by the protocol back to master.

