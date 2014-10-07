# Olivaw

Simulate botnet behavior in Python.

# Usage

Import `Net` and `Bot` from `prototypes`. Then override them and change their behavior as necessary.

The example in `sample_net.py` does the following:

* Creates a TestNet net
* Creates 20 TestBots and adds them to the net
* Runs the net for 100 ticks

The TestBot does the following:

* Assigns itself a random 5-digit ID
* On each net tick, has a 5% chance of printing its ID

The TestNet does the following:

* Every tick, prints its tick number

# About

Named after Isaac Asimov's robot character, R. Daneel Olivaw.
