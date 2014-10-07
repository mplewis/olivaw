#!/usr/bin/env python3

from prototypes import Net, Bot

from random import randint


class TestBot(Bot):
    def __init__(self):
        self.id = randint(10000, 99999)

    def tick(self):
        if randint(1, 20) == 20:
            print('    Ticked: %s' % self.id)


class TestNet(Net):
    def tick(self):
        print('Tick %s' % (self.ticks + 1))
        super().tick()


if __name__ == '__main__':
    bots = []
    for x in range(20):
        bot = TestBot()
        bots.append(bot)
        print('Created bot: %s' % bot.id)
    net = TestNet()
    net.bots = bots
    for x in range(100):
        net.tick()
