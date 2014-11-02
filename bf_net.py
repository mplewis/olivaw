#!/usr/bin/env python3
#Baby's First Botnet

from prototypes import Net, Bot, Message
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

class PeerSwapBot(Bot):
    def __init__(self, net):
        self.peerSwapList = [] # contains PeerSwapStatus objects currently in use
        self.delayedMessages = []
        super().__init__(net)

    class PeerSwapStatus():
        """ A data class for keeping track of confirmed, offered, and traded peers """
        def __init__(self, parentID, peer):
            self.peer = peer # the peer this status object refers to
            self.timeout = 10 # timeout on this status. if < 0, will be deleted
            self.confirmed = False # this bot has confirmed this peer exists
            self.offered = False # this bot is offering this peer for PeerSwap
            self.tradeFor = -1 # this peer is an offer from another bot, and we're trading for this bot
            self.tradeFrom = parentID
        
        def confirm(self):
            # recieved message back from peer
            # consider deleting peer on double confirm?
            self.confirmed = True
        def offer(self):
            self.offered = True
        def expire(self):
            self.timeout = 0
        def refresh(self):
            self.timeout = 10
    
### UTILITIES
    def popRandomPeer(self):
        if len(self.peers)>0:
            return int(self.peers.pop(randint(0,len(self.peers)-1)))
        else:
            return -1

    def sendTo(self, peer, msgText):
        m = Message(self.id, peer, msgText)
        net.send(m)

    def addPeerSwapTradeOffer(self, peer, tradeFor, tradeFrom):
        tradeOffer = self.PeerSwapStatus(tradeFrom, peer)
        tradeOffer.tradeFor = tradeFor
        self.peerSwapList.append(tradeOffer)

    def getPeerSwapStatus(self, peerID):
        for status in self.peerSwapList:
            if status.peer == int(peerID):
                return status
        return None

### TICK OPERATIONS

    def tick(self):
        self.checkTimeout()
        self.retrieveDelayedMessages()
        self.processMessages()
        self.broadcastCommand()
        if(randint(1,20)) == 1:
            self.suggestTrade()

    def checkTimeout(self):
        i = 0
        while i < len(self.peerSwapList):
            status = self.peerSwapList[i]
            status.timeout -= 1
            if status.timeout < 0:
                # if confirmed, readd to peers
                if status.confirmed and status.tradeFor == -1:
                    self.peers.append(status.peer)
                # remove from peerSwapList
                self.peerSwapList.remove(status)
            else:
                i += 1

    def retrieveDelayedMessages(self):
        for message in self.delayedMessages:
            messages.append(message)
        self.delayedMessages.clear()

    def broadcastCommand(self):
        for peer in self.peers:
            self.sendTo(peer, "currentCommand:{}".format(self.command))

    def suggestTrade(self):
        # get peers to suggest and send message to
        suggestPeer = self.popRandomPeer()
        suggestTo = self.popRandomPeer()
        if(suggestPeer == -1 or suggestTo == -1):
            return
        self.peers.append(suggestTo)
        # append confirmed peer
        suggestion = self.PeerSwapStatus(self.id, suggestPeer)
        suggestion.confirm()
        suggestion.offer()
        self.peerSwapList.append(suggestion)
        # send suggestion along
        self.sendTo(suggestTo, "suggest:{}".format(suggestPeer))

    def respondToSuggestion(self, tradePeer, suggestedPeer):
        # get peers to offer in return
        suggestionResponsePeer = self.popRandomPeer()
        if(suggestionResponsePeer == -1):
            return
        i = 3
        while suggestionResponsePeer == tradePeer and i > 0:
            self.peers.append(suggestionResponsePeer)
            suggestionResponsePeer = self.popRandomPeer()
            i -= 1
        # append confirmed peer
        suggestion = self.PeerSwapStatus(self.id, suggestionResponsePeer)
        suggestion.confirm()
        suggestion.offer()
        self.peerSwapList.append(suggestion)
        # send response
        self.sendTo(tradePeer, "tradeOffer:{}, {}".format(suggestionResponsePeer, suggestedPeer))

    def acknowledgeTrade(self, tradePeer, incomingPeer, outgoingPeer):
        self.sendTo(tradePeer, "acknowledgeTrade:{}, {}".format( outgoingPeer, incomingPeer))

    def confirmPeer(self, peerID):
        # add peer to PeerSwapList if not already there
        status = self.getPeerSwapStatus(peerID)
        if status is None:
            self.peers.remove(peerID)
            self.peerSwapList.append(PeerSwapStatus(self.id, peerID))
        # ask for confirmation
        self.sendTo(peerID, "confirm:")

    def swapPeers(self, incomingPeer, outgoingPeer):
        foundOutgoing = False
        if outgoingPeer in self.peers:
            self.peers.remove(outgoingPeer)
            foundOutgoing = True
        else:
            status = self.getPeerSwapStatus(outgoingPeer)
            if status is not None:
                self.peerSwapList.remove(status)
                foundOutgoing = True
        if foundOutgoing:
            self.peers.append(int(incomingPeer))
    
    def sendRefocus(self, peer, refocusTo):
        self.sendTo(int(peer), "refocus:{}".format(refocusTo))

    def audit(self, message):
        auditFile = open("audit.txt", "a")
        auditFile.write("#{}->#{} = {}:{}\n".format(message.source, 
            message.destination, message.messageTitle, message.messageDetails))
        auditFile.close()

    def processMessages(self):
        while len(self.messages)>0:
            m = self.messages[0]
            if "currentCommand" not in m.messageTitle:    
                self.audit(m)

            #current command
            if "currentCommand" in m.messageTitle:
                if int(m.messageDetails[0]) > int(self.command[0]):
                    self.command = (int(m.messageDetails[0]), m.messageDetails[1])
            if "suggest" in m.messageTitle:
                if int(m.messageDetails[0]) not in self.peers:
                    self.respondToSuggestion(m.source, int(m.messageDetails[0]))
            if "tradeOffer" in m.messageTitle:
                #refresh PeerSwapStatus of appropriate peer
                for status in self.peerSwapList:
                    if status.peer == int(m.messageDetails[1]):
                        status.refresh()
                if int(m.messageDetails[0]) not in self.peers:
                    self.addPeerSwapTradeOffer(int(m.messageDetails[0]), int(m.messageDetails[1]), int(m.source))
                    self.confirmPeer(m.messageDetails[0])
            if "acknowledgeTrade" in m.messageTitle:
                self.addPeerSwapTradeOffer(int(m.messageDetails[0]), int(m.messageDetails[1]), int(m.source))
                self.confirmPeer(m.messageDetails[0])
            if "confirm" in m.messageTitle:
                self.sendTo(m.source, "roger:")
            if "roger" in m.messageTitle:
                # confirm the PeerSwapStatus
                status = self.getPeerSwapStatus(m.source)
                if status is not None:
                    status.confirm()
                    if status.tradeFor > 0:
                        # check for offer
                        offerStatus = self.getPeerSwapStatus(status.tradeFor)
                        if offerStatus is not None and offerStatus.offered:
                            # acknowledge trade, and do the thing
                            self.acknowledgeTrade(status.tradeFrom, m.source, status.tradeFor)
                            self.swapPeers(m.source, status.tradeFor)
                            self.sendRefocus(status.tradeFor, status.tradeFrom)
            if "refocus" in m.messageTitle:
                self.swapPeers(int(m.source), int(m.messageDetails[0]))

            self.messages.pop(0)






class PeerSeverBot(Bot):
    pass





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
    auditFile = open("audit.txt", "w")
    auditFile.close()
    bots = []
    botids = []
    net = TestNet()
    for x in range(20):
        bot = PeerSwapBot(net)
        bots.append(bot)
        botids.append(bot.id)
        print('Created bot: %s' % bot.id)
    for bot in bots:
        while len(bot.peers) < 6:
            peer = botids[randint(0, len(botids)-1)]
            if peer != bot.id:
                bot.peers.append(peer)
    net.bots = bots
    net.dumpbots()
    net.command()
    for x in range(10):
        for x in range(10):
            net.tick()
            auditFile = open("audit.txt", "a")
            auditFile.write("=== TICK {} ===\n".format(net.ticks))
            auditFile.close()
        net.command()
    net.dumpbots()
        
        
