from random import randint


class Net:
    def __init__(self):
        self.ticks = 0
        self.commandNumber = 1
        self.bots = []

    def tick(self):
        self.ticks += 1
        for bot in self.bots:
            bot.tick()

    def send(self, srcID, destID, msg):
        for bot in self.bots:
            if bot.id == destID:
                bot.recieve(srcID, msg)

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
        self.id = randint(10000, 99999)
        self.net = net
        self.command = (0, "v.0")
    def tick(self):
        err = ('Bot must implement a tick() method indicating what happens '
               'each timestep.')
        raise NotImplementedError(err)
    def recieve(self, srcID, message):
        err = ('Bot must implement a recieve() method which indicates '
                'how the bot takes action on messages.')
        raise NotImplementedError(err)