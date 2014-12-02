from prototypes import Net
from time import time

last_version_count = None


class ZeroAccessNet(Net):
    def __init__(self):
        super().__init__()
        self.start_time = time()

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
            now = round(time() - self.start_time, 2)
            print(now, self.ticks, latest_version, version_count)
        last_version_count = version_count
