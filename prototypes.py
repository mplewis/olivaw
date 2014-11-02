from random import randint


class Net:
    def __init__(self):
        self.ticks = 0
        self.commandNumber = 1
        self.bots = []
        self.messages = []

    def tick(self):
        self.ticks += 1
        #send all messages
        while len(self.messages)>0:
            m = self.messages[0]
            srcID = int(m.source)
            destID = int(m.destination)
            for bot in self.bots:
                if bot.id == destID:
                    bot.recieve(m)
            self.messages.pop(0)
        for bot in self.bots:
            bot.tick()

    def send(self, srcID, destID, msg):
        self.messages.append((srcID, destID, msg))

    def send(self, message):
        self.messages.append(message)

    def command(self):
        #send new command to a random bot
        bot = self.bots[randint(0, len(self.bots)-1)]
        bot.command = (self.commandNumber, "bitmine@{}".format(self.ticks))
        self.commandNumber+=1
        return bot.id



class Bot:
    # net === the common Net object between all bots
    def __init__(self, net):
        #peers == a list of bot ids.
        self.peers = []
        self.messages = []
        self.id = randint(10000, 99999)
        self.net = net
        self.command = (0, "v.0")
    def tick(self):
        err = ('Bot must implement a tick() method indicating what happens '
               'each timestep.')
        raise NotImplementedError(err)
    def recieve(self, srcID, message):
        srcID = int(srcID)
        self.messages.append((srcID, message))
        return
    def recieve(self, msg):
        self.messages.append(msg)
        return

class Message:
    def __init__(self, srcID, destID, msg):
        self.source = int(srcID)
        self.destination = int(destID)
        self.messageTitle = msg[:msg.index(":")]
        self.messageDetails = msg[msg.index(":")+1:].translate(str.maketrans("","","[]()\'\"")).split(", ")
