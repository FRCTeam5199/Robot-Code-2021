#!/usr/bin/env python3
import time
import speech_recognition as sr


def callback(recognizer, audio):
    try:
        print(recognizer.recognize_google(audio))
    except sr.UnknownValueError:
        pass
    except sr.RequestError as e:
        pass


r = sr.Recognizer()
m = sr.Microphone()
with sr.Microphone() as source:
    r.adjust_for_ambient_noise(source)

stop_listening = r.listen_in_background(m, callback)
print("Initialized")
while True:
    time.sleep(0.1)
