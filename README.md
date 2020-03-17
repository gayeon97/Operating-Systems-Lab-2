Scheduling
===============
**Gayeon Park**
*Operating Systems, Spring 2020*

For this Java program, we are simulating scheduling to see how time requried for process is allocated, depending on the scheduling algorithm and the request patterns. 


***
Here, a process is characterized by just four non-negative integers: A, B, C and M.
A is the arrival time of the process. B is used for calculating the CPU Burst time for a process, using the randomOS(B) function. C is the total CPU time needed by the process. M is used for calculating the IO Burst time for a process.

Using these four information about the process, we are trying to replicate how an operating system would schedule multiple processes to run in real life.


***
The Scheduling.java program reads its input from a file, whose name is given as a command line argument. The program send its output to the screen as System.out in Java.

The program accepts an optional "-verbose" flag. If present, it precedes the file name. When "-verbose" is given, the program produce detailed output that's helpful for debugging. 

The program also accepts an optional "-show-random" flag. If present, it precedes the file name. When "-show-random" is given, the program produce detailed output along with the random number chosen each time. This is a more verbose version.

Three possible invocations of the program are:
* "program-name" "input-filename"
* "program-name" --verbose "input-filename"
* "program-name" --show-random "input-filename"
     

For a detailed description of the output, please go to the *expectedOutputs* folder and read the README.md file there.


***
Inside of the "src" folder, Scheduling.java file is located.
To execute the program using the contents of a text file, please type the name of the java file (Scheduling), the name of any flag (-verbose or -show-random), if any, and the name of the input file. 

Type the below instruction into the Terminal to compile the Scheduling.java program.
When compiling, you have to make sure that you are compiling both the Scheduling.java file and the Process.java file because Scheduling.java uses an instance of Process class.

ONE IMPORTANT THING TO NOTE: the input files you are testing the Scheduling.java file against MUST be in the same folder as the Scheduling.java file
So Scheduling.java file, Process.java file, and whatever input file you want to test HAVE to be in the same folder (namely, src folder inside of the Lab2 folder).

### Compiling
```
javac Scheduling.java Process.java
```

### Running
To execute the code, type following:
```
java Scheduling input-number                    //for a regular output



java Scheduling --verbose input-number          //for a detailed output



java Scheduling --show-random input-number      //for a even more detailed output


```

***
Once again, the input text files and Scheduling.java file are located in same folder, "src" inside of Lab2 folder. 
If you want to execute the Scheduling.java file with the contents of "input-7" file, do the following: 

### Compiling
```
javac Scheduling.java Process.java
```

### Running
To execute the code, type following:
```
java Scheduling input-7                     //for a regular output



java Scheduling --verbose input-7           //for a detailed output



java Scheduling --show-random input-7       //for a even more detailed output


```
