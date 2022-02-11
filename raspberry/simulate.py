import box
import threading, time
import os


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


class pi_stuff:
    def blink_green():
        print("Green Blink!")
        time.sleep(0.5)
        return

    def get_brightness():
        return round(time.time()) % 2

    def blink_red_short():
        print("Red_blink!")
        time.sleep(0.5)
        return


box.update_deliveries()
t = perpetualTimer(5, box.update_deliveries)
t.start()

# success = box.me.request_open(str(id), pi_stuff)

def deliver_a():
    box.me.request_open("520413243569", pi_stuff)

def deliver_b():
    box.me.request_open("RFIDToken3", pi_stuff)
    
def pickup():
    box.me.request_open("108560888149", pi_stuff)


def info():
    box.me.info()