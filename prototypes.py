class Net:
    def __init__(self):
        self.ticks = 0
        self.bots = []

    def tick(self):
        self.ticks += 1
        for bot in self.bots:
            bot.tick()


class Bot:
    def __init__(self):
        self.peers = []

    def tick(self):
        err = ('Bot must implement a tick() method indicating what happens '
               'each timestep.')
        raise NotImplementedError(err)
