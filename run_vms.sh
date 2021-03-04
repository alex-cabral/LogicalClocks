#!/bin/sh

javac VirtualMachine.java
java VirtualMachine $1 $2 0 &
java VirtualMachine $1 $2 1 &
java VirtualMachine $1 $2 2
