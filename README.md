# Simple Game of Life app

This repository contains a simple attempt to visualize and simulate a cellular automata following the rules
of Conway's Game of Life in a javafx desktop application.

Its my first attempt to write a javafx application and it served as an exercise to follow best-practises
and try out more advanced features, like dependency injection using guice.

## Features

The app includes the following set of features:

* visualize the current state of the cellular automata
* edit each cell using the mouse (left mouse -> cell alive, right mouse -> cell dead) 
* load and save models
* simulate the model at different speeds

## Getting Started

Run the app:

```
./gradlew run
```

## Examples

![Screenshot](https://raw.githubusercontent.com/netomi/simple-life/master/images/simple-life-example.png)
