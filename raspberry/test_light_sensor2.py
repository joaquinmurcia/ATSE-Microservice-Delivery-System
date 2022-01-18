#Libraries
import RPi.GPIO as GPIO
from time import sleep
#Set warnings off (optional)
GPIO.setwarnings(False)
#GPIO.setmode(GPIO.BCM)
GPIO.setmode(GPIO.BOARD)

#Set Button and LED pins
photo_resistor = 16
#Setup Button and LED
GPIO.setup(photo_resistor,GPIO.IN,pull_up_down=GPIO.PUD_UP)
#flag = 0

while True:
    button_state = GPIO.input(photo_resistor)
    print(button_state)
