#!/usr/bin/env python3


"""
This simple botnet simulator:
    * starts with 20 bots
    * simulates 10,000 ticks
    * creates a new bot every 250 ticks

Each bot:
    * has a 1 percent chance of discovering another bot
    * only adds a bot to its peer list if it hasn't discovered that bot yet

Every tick, the distribution of peer counts between all bots is printed.
"""


from prototypes import Net, Bot

from random import randint, choice


num_bots = 20  # initial bot count
SIM_TICKS = 10000  # total simulation length in ticks
DISCOVERY_PROB = 100  # greater DISCOVERY_PROB = lower prob
NEW_BOT_EVERY = 250  # introduce a bot every x ticks


class DiscoverBot(Bot):
    def __init__(self, parent_net):
        super().__init__()
        self.parent_net = parent_net
        self.id = randint(10000, 99999)
        parent_net.bots.append(self)

    def tick(self):
        if randint(1, DISCOVERY_PROB) == 1:
            discovered = choice(self.parent_net.bots)
            if discovered not in self.peers:
                self.peers.append(discovered)


class DiscoverNet(Net):
    def tick(self):
        discovery_count = [0 for i in range(num_bots + 1)]
        for bot in self.bots:
            discovery_count[len(bot.peers)] += 1
        print(discovery_count)
        super().tick()


if __name__ == '__main__':
    net = DiscoverNet()
    bots = []
    for x in range(num_bots):
        bot = DiscoverBot(net)
    for tick in range(SIM_TICKS):
        if tick % NEW_BOT_EVERY == 0:
            bot = DiscoverBot(net)
            num_bots += 1
        net.tick()
