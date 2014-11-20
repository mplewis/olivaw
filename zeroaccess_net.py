from prototypes import Net


class ZeroAccessNet(Net):
    def tick(self):
        version_count = {}
        total_peers = 0
        for bot in self.bots:
            total_peers += len(bot.peers)
            if bot.version not in version_count:
                version_count[bot.version] = 1
            else:
                version_count[bot.version] += 1
        avg_peers = total_peers * 1.0 / len(self.bots)
        latest_version = max(version_count.keys())
        print(self.ticks, latest_version, avg_peers, version_count)
        super().tick()
