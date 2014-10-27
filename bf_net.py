#!/usr/bin/env python3
#Baby's First Botnet

from prototypes import Net, Bot
from random import randint


class BFBot(Bot):
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
		if message == "ping":
			print('Hi {}, this is {}.'.format(srcID, self.id))
			return
		if message == "getPeer":
			net.send(self.id, srcID,
			 "getPeerResponse:{}".format(self.peers[randint(0,len(self.peers)-1)]))
			return
		if "getPeerResponse" in message:
			#parse out address
			recievedPeer = int(message[message.index(":")+1:])
			#check peer list for duplication
			for peer in self.peers:
				if peer == recievedPeer:
					return
			#select random peer to replace
			self.peers[randint(0,len(self.peers)-1)] = recievedPeer
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
    for x in range(20):
        bot = BFBot(net)
        bots.append(bot)
        botids.append(bot.id)
        print('Created bot: %s' % bot.id)
    for bot in bots:
    	for x in range(5):
    		bot.peers.append(botids[randint(0, len(botids)-1)])
    net.bots = bots
    net.dumpbots()
    net.command()
    for x in range(10):
	    for x in range(50):
	        net.tick()
	    net.command()
	    net.dumpbots()
	    
	    
