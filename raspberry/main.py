import box
import threading, time
import os
import pi_stuff


from threading import Timer, Thread, Event


class perpetualTimer:
    def __init__(self, t, hFunction):
        self.t = t
        self.hFunction = hFunction
        self.thread = Timer(self.t, self.handle_function)

    def handle_function(self):
        self.hFunction()
        self.thread = Timer(self.t, self.handle_function)
        self.thread.start()

    def start(self):
        self.thread.start()

    def cancel(self):
        self.thread.cancel()


valid_ids = pi_stuff.load_ids(os.path.dirname(__file__) + "/user_ids.json")
reader, led = pi_stuff.init_hardware()


box.update_deliveries()
t = perpetualTimer(20, box.update_deliveries)
t.start()

while True:
    try:

        id, text = reader.read_no_block()
        if not id:
            continue

        print("Detected token with id:", id, "and content: ", text)

        success = box.me.request_open(str(id), pi_stuff)

        if success:
            print("Access Granted!")
        else:
            print("Access denied!")
            pi_stuff.blink_red()

    except KeyboardInterrupt:
        t.cancel()
        pi_stuff.quit()
