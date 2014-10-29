#!/usr/bin/env python3
#Baby's First Botnet

from prototypes import Net, Bot
from random import randint


class BFBot(Bot):
    def __init__(self, net):
        self.trade_suggestions = [] #[peer to trade, timeout]
        self.confirmed_trades = [] #[peer you're trading, peer to merge into list once ack recieved]
        super().__init__(net)

    def tick(self):
		#decrement and check trade_suggestions
		for i in range(len(self.trade_suggestions)):
			suggestion = self.trade_suggestions[i]
			suggestion[1] -= 1
			if suggestion[1] < 0:
				self.peers.append(int(suggestion[0]))
				self.trade_suggestions.remove(suggestion)
				i -= 1
			
			
		#select random action
        select = randint(1,20)
        if select == 1:
            address = self.peers[randint(0,len(self.peers)-1)]
            # print("This is {} pinging {}".format(self.id, address))
            net.send(self.id, address, "ping")
        if select == 2:
            self.getPeer()
        if select == 3:
			#Set up trade suggestion and send
			trade_peer = createTradeSuggestion()
			net.send(self.id, self.peers[randint(0,len(self.peers)-1)], "suggestTrade:{}".format(trade_peer))
        if select > 18:
            self.getCommand()
        #process all messages in queue
        while len(self.messages)>0:
            messageTuple = self.messages[0]
            srcID = int(messageTuple[0])
            message = messageTuple[1]
            if message == "ping":
                # print("Hi {}, this is {}".format(srcID, self.id))
                net.send(self.id, srcID, "pong")
                continue
            if message == "pong":
                #check tenative for srcID, if there, replace a peer
                if srcID in self.tenative:
                    self.peers[randint(0,len(self.peers)-1)] = srcID
                    self.tenative.remove(srcID)
                continue
            if message == "getPeer":
                net.send(self.id, srcID,
                 "getPeerResponse:{}".format(self.peers[randint(0,len(self.peers)-1)]))
                continue
            if "getPeerResponse" in message:
                #parse out address
                recievedPeer = int(message[message.index(":")+1:])
                #check peer list for duplication
                if recievedPeer not in self.peers:
                    #Ping this peer and see if they exist
                    self.tenative.append(recievedPeer)
                    net.send(self.id, recievedPeer, "ping")
			    continue
            if message == "getCommand":
                net.send(self.id, srcID, 
                    "getCommandResponse:{},{}".format(self.command[0], self.command[1]))
                continue
            if "getCommandResponse" in message:
                commandNumber = int(message[message.index(":")+1:message.index(",")])
                if commandNumber > self.command[0]:
                    self.command = (commandNumber, message[message.index(",")+1:])
                continue
            #suggested trade
            if "suggestTrade:" in message:
				#ensure suggestion is not already a peer
				recievedPeer = int(message[message.index(":")+1:])
				if recievedPeer not in self.peers:
					#create your own trade suggestion, send confirm
					trade_peer = createTradeSuggestion()
					net.send(self.id, srcID, "confirmTrade:{},{}".format(trade_peer, recievedPeer))
				continue
            #confirm trade desire and offer peer
            if "confirmTrade:" in message:
				#ensure suggestion is not already a peer
				recievedPeer = int(message[message.index(":")+1:message.index(",")])
				if recievedPeer not in self.peers:
					#move peer to confirmed, send ack on pong response
					suggestionID = message[message.index(",")+1:]
					for suggestion in self.trade_suggestions:
						if suggestion[0] == suggestionID:
							self.trade_suggestions.remove(suggestion)
							self.confirmed_trades.append([suggestionID, recievedPeer])
							net.send(self.id, recievedPeer, "ping")
							break
				continue
            #acknowledge
            
            #acknowledge back
            
            self.messages.pop(0)

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
    
    def createTradeSuggestion(self):
		trade_peer = int(self.peers.pop(randint(0,len(self.peers)-1)))
		self.trade_suggestions.append([trade_peer, 10])
		return trade_peer

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
        for x in range(20):
            net.tick()
            print("====")
        net.command()
    net.dumpbots()
        
        
