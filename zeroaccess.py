from zeroaccess_bot import ZeroAccessBot
from zeroaccess_net import ZeroAccessNet

import random


INITIAL_BOTS = 100
SIM_TICKS = 10000
NEW_VER_EVERY = 50

if __name__ == '__main__':
    # Create the net
    net = ZeroAccessNet()
    # Create x initial bots
    for x in range(INITIAL_BOTS):
        address = random.randint(10000000, 99999999)
        bot = ZeroAccessBot(address)
        net.bots.append(bot)
    # Give each bot one peer to start
    for bot in net.bots:
        peer = bot
        while peer == bot:
            peer = random.choice(net.bots)
        bot.peers.append(peer)
    # Run the botnet
    for tick in range(SIM_TICKS):
        net.tick()
        # Every n ticks, give a random bot a new version
        if net.ticks % NEW_VER_EVERY == 0:
            bot = random.choice(net.bots)
            bot.version += 1
