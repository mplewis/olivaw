from prototypes import Net

last_version_count = None

class ZeroAccessNet(Net):
    def tick(self):
        global last_version_count
        super().tick()
        # Print statistics on botnet each tick:
        # tick count, latest version, {version: bot count with version, ...}
        version_count = {}
        for bot in self.bots:
            if bot.version not in version_count:
                version_count[bot.version] = 1
            else:
                version_count[bot.version] += 1
        latest_version = max(version_count.keys())
        if last_version_count != version_count:
            print(self.ticks, latest_version, version_count)
        last_version_count = version_count
