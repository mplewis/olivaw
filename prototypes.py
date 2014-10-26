from random import randint


class Net:
    def __init__(self):
        self.ticks = 0
        self.bots = []

    def tick(self):
        self.ticks += 1
        for bot in self.bots:
            bot.tick()

    def send(self, srcID, destID, msg):
        for bot in self.bots:
            if bot.id == destID:
                bot.recieve(srcID, msg)


class Bot:
    # net === the common Net object between all bots
    def __init__(self, net):
        #peers == a list of bot ids.
        self.peers = []
        self.id = randint(10000, 99999)
        self.net = net
    def tick(self):
        err = ('Bot must implement a tick() method indicating what happens '
               'each timestep.')
        raise NotImplementedError(err)
    def recieve(self, srcID, message):
        err = ('Bot must implement a recieve() method which indicates '
                'how the bot takes action on messages.')
        raise NotImplementedError(err)