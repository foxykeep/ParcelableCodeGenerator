ParcelableCodeGenerator
=======================

This project is a code generator written in Java used to generate Android code. Given a JSON definition file, it will generate the corresponding Parcelable class.


Architecture of the project
---------------------------

The java project contains the following folders :
 * src : The code used to generate your Parcelable classes. Normally you shouldn't have to modify it.
 * res : This folder contains snippets of code used by the generator.
 * input : This folder contains your JSON definition files. You can have multiple JSON files next to each other as well as in subfolders. An example and the JSON format are available in the "example" folder.
 * output : This is where the generated code will be written. Each generate code will be stored in a input subfolder . 


How to use ParcelableCodeGenerator
--------------------------------

In order to use it, here are the steps to follow :

1. Download the current version of the project on your computer using git (`git clone git@github.com:foxykeep/ParcelableCodeGenerator.git`). 
2. Import the project in your IDE. 
3. Write your JSON definition file. You can use the format file and the example file given in the folder "example" to help you.
4. Run the project as a Java application.
5. Your classes are available in the output folder

Building/Running from the CLI
-----------------------------
```
    mkdir bin
    mkdir bin/input
    cd bin
    ln -s ../res .
    javac $(find ../src -name *.java) -d .
    cp ../example/fullgen.json ./input
    java com/foxykeep/parcelablecodegenerator/Main
```

Credits and License
-------------------

Foxykeep ([http://www.foxykeep.com](http://www.foxykeep.com/))

Licensed under the Beerware License :

> You can do whatever you want with this stuff. If we meet some day, and you think this stuff is worth it, you can buy me a beer in return.
