# -*- coding: utf-8 -*-

import requests
import RPi.GPIO as GPIO
import time

# Initial GPIO configuration
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)  # Disable warnings
LED_PIN2 = 4  # LED for true response
LED_PIN = 3   # LED for false response
GPIO.setup(LED_PIN2, GPIO.OUT)
GPIO.setup(LED_PIN, GPIO.OUT)

# Define the roomNumber variable
roomNumber = 2

def send_number(number, room):
    url = 'http://192.168.1.5:5000/receivenumber'
    try:
        response = requests.post(url, json={'number': number, 'roomNumber': room})
        if response.status_code == 200:
            return response.json().get('result', False)  # Assumes the server responds with a JSON containing 'result'
        else:
            print("Error sending the number")
            return False
    except requests.RequestException as e:
        print("Error connecting to the server: ", e)
        return False

if __name__ == '__main__':
    while True:
        input_str = raw_input("Enter a number to send (or 'exit' to quit): ")
        if input_str.lower() == 'exit':
            GPIO.cleanup()
            break
        try:
            number = int(input_str)
            result = send_number(number, roomNumber)
            if result:
                print(result)
                GPIO.output(LED_PIN2, GPIO.HIGH)
		print("access allowed.")
                time.sleep(5)
                GPIO.output(LED_PIN2, GPIO.LOW)
            else:
                print(result)
                GPIO.output(LED_PIN, GPIO.HIGH)
		print("access denied")
                time.sleep(5)
                GPIO.output(LED_PIN, GPIO.LOW)
        except ValueError:
            print("Please enter a valid number or 'exit' to quit.")
