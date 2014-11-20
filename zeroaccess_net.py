from prototypes import Net


class ZeroAccessNet(Net):
    def tick(self):
        # Print statistics on botnet each tick:
        # tick count, latest version, {version: bot count with version, ...}
        version_count = {}
        for bot in self.bots:
            if bot.version not in version_count:
                version_count[bot.version] = 1
            else:
                version_count[bot.version] += 1
        latest_version = max(version_count.keys())
        print(self.ticks, latest_version, version_count)
        super().tick()
