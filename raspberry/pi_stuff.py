from mfrc522 import SimpleMFRC522
import RPi.GPIO as GPIO
from time import sleep
import json
import os


led_red = 13 # GPIO: 27, Board: 13
led_green = 11 # GPIO: 17, Board: 11

photo_resistor = 16

def quit():
    # GPIO must be cleaned up once you exit the script
    # Otherwise , other scripts may not work as you expect
    GPIO.cleanup()
    raise

def init_hardware():
    reader = SimpleMFRC522()
    GPIO.setmode(GPIO.BOARD)
    GPIO.setwarnings(False)
    GPIO.setup(led_red, GPIO.OUT, initial=GPIO.LOW) 
    GPIO.setup(led_green, GPIO.OUT, initial=GPIO.LOW) 
    GPIO.setup(photo_resistor,GPIO.IN,pull_up_down=GPIO.PUD_UP)

    return reader, True


def get_brightness():
    return GPIO.input(photo_resistor)

def red_on():
    GPIO.output(led_red, GPIO.HIGH)

def red_off():
    GPIO.output(led_red, GPIO.LOW)

def green_on():
    GPIO.output(led_green, GPIO.HIGH)

def green_off():
    GPIO.output(led_green, GPIO.LOW)



def blink_green():
    GPIO.output(led_green, GPIO.HIGH)
    sleep(3)
    GPIO.output(led_green, GPIO.LOW)


def blink_red():
    GPIO.output(led_red, GPIO.HIGH)
    sleep(3)
    GPIO.output(led_red, GPIO.LOW)

def blink_red_short():
    GPIO.output(led_red, GPIO.HIGH)
    sleep(.5)
    GPIO.output(led_red, GPIO.LOW)


def write(asdf):
    try:
        reader.write(asdf)
    except Exception:
        quit()


def authenticate(user_id, valid_ids):
    for ele in valid_ids:
        if str(user_id) == ele["id"]:
            return True
    return False

def load_ids(file_name):
    tmp_file = open(file_name, "r")
    valid_ids = tmp_file.read()
    valid_ids = json.loads(valid_ids)
    return valid_ids