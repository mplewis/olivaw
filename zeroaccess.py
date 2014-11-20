from zeroaccess_bot import ZeroAccessBot
from zeroaccess_net import ZeroAccessNet

import random


"""
ZeroAccess botnet simulator. See the Kindsight paper for details.

Each bot keeps a list of 256 peers max. Every second, it sends a UDP packet to
one of these peers. The peer being queried responds with the following:

* A list of 16 of its peers' addresses
* The version (file name and creation date) of its payload

The bot updates its peer list with other known peers. If its payload is out
of date, it updates its payload. This is represented by the version number
increasing to match the peer with the more recent payload.
"""


INITIAL_BOTS = 100
INITIAL_PEERS = 3
SIM_TICKS = 60 * 60 * 4  # 4 hours
NEW_VER_EVERY = 60 * 10  # 5 minutes
NEW_BOT_EVERY = 60 * 2  # 2 minutes

if __name__ == '__main__':
    # Create the net
    net = ZeroAccessNet()
    # Create x initial bots
    for x in range(INITIAL_BOTS):
        bot = ZeroAccessBot()
        net.bots.append(bot)
    # Give each bot one peer to start
    for bot in net.bots:
        for x in range(INITIAL_PEERS):
            peer = bot
            while peer == bot:
                peer = random.choice(net.bots)
            bot.peers.append(peer)

    # Run the botnet
    latest_version = 0
    for tick in range(SIM_TICKS):
        net.tick()
        # Every n ticks, give a random bot a newly-released version
        if net.ticks % NEW_VER_EVERY == 0:
            latest_version += 1
            bot = random.choice(net.bots)
            bot.version = latest_version
        # Every n ticks, add a new bot to the net
        if net.ticks % NEW_BOT_EVERY == 0:
            bot = ZeroAccessBot()
            for x in range(INITIAL_PEERS):
                peer = bot
                while peer == bot:
                    peer = random.choice(net.bots)
                bot.peers.append(peer)
            net.bots.append(bot)
