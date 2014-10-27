#!/usr/bin/env python3
#Baby's First Botnet

from prototypes import Net, Bot
from random import randint


class BFBot(Bot):
    def __init__(self, net):
        self.tenative = []
        super().__init__(net)

    def tick(self):
        select = randint(1,20)
        if select == 1:
            #address = self.peers[randint(0,len(self.peers)-1)]
            #print("This is {} pinging {}".format(self.id, address))
            #net.send(self.id, address, "ping")
            return
        if select == 2:
            self.getPeer()
        if select > 18:
            self.getCommand()

    def getPeer(self):
        #select random peer
        address = self.peers[randint(0,len(self.peers)-1)]
        #ask for one new peer
        net.send(self.id, address, "getPeer")

    def getCommand(self):
        #select random peer
        address = self.peers[randint(0,len(self.peers)-1)]
        #ask for its current command
        net.send(self.id, address, "getCommand")

    def recieve(self, srcID, message):
        srcID = int(srcID)
        if message == "ping":
            net.send(self.id, srcID, "pong")
            return
        if message == "pong":
            #check tenative for srcID, if there, replace a peer
            if srcID in self.tenative:
                self.peers[randint(0,len(self.peers)-1)] = srcID
                self.tenative.remove(srcID)
            return
        if message == "getPeer":
            net.send(self.id, srcID,
             "getPeerResponse:{}".format(self.peers[randint(0,len(self.peers)-1)]))
            return
        if "getPeerResponse" in message:
            #parse out address
            recievedPeer = int(message[message.index(":")+1:])
            #check peer list for duplication
            if recievedPeer in self.peers:
                return
            #Ping this peer and see if they exist
            self.tenative.append(recievedPeer)
            net.send(self.id, recievedPeer, "ping")
        if message == "getCommand":
            net.send(self.id, srcID, 
                "getCommandResponse:{},{}".format(self.command[0], self.command[1]))
            return
        if "getCommandResponse" in message:
            commandNumber = int(message[message.index(":")+1:message.index(",")])
            if commandNumber > self.command[0]:
                self.command = (commandNumber, message[message.index(",")+1:])
            return

class TestNet(Net):
    def tick(self):
        #print('Tick %s' % (self.ticks + 1))
        super().tick()
    def dumpbots(self):
        for bot in self.bots:
            print("{}({}) : {}".format(bot.id, bot.command, bot.peers))
        print("---------------------------")
    def command(self):
        print("New command sent to {}.".format(super().command()))


if __name__ == '__main__':
    bots = []
    botids = []
    net = TestNet()
    for x in range(200):
        bot = BFBot(net)
        bots.append(bot)
        botids.append(bot.id)
        print('Created bot: %s' % bot.id)
    for bot in bots:
        for x in range(10):
            bot.peers.append(botids[randint(0, len(botids)-1)])
    net.bots = bots
    net.dumpbots()
    net.command()
    for x in range(1000):
        for x in range(20):
            net.tick()
        net.command()
    net.dumpbots()
        
        
