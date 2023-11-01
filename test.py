import cv2; print(cv2.getBuildInformation())

from datetime import datetime
from time import sleep, time

import cv2
import numpy as np

fps = 30
width = 640
height = 480
colors = [
    (0, 0, 255),
    (255, 0, 0),
    (0, 255, 0),
]

# out = cv2.VideoWriter('appsrc ! videoconvert ! video/x-raw,format=I420 ! x264enc speed-preset=ultrafast tune=zerolatency bitrate=600 key-int-max=' + str(fps * 2) + ' ! video/x-h264,profile=baseline' + ' ! rtspclientsink location=rtsp://localhost:8554/mystream',
#     cv2.CAP_GSTREAMER, 0, fps, (width, height), True)
out = cv2.VideoWriter('appsrc ! videoconvert ! x264enc interlaced=false ! mp4mux ! filesink location=test4.mp4',
    cv2.CAP_GSTREAMER, 0, fps, (width, height), True)
if not out.isOpened():
    raise Exception("can't open video writer")

curcolor = 0
start = time()

# frame = cv2.imread("/home/matt/Downloads/mychild.png")

cap = cv2.VideoCapture(0)

cap.set(cv2.CAP_PROP_FRAME_WIDTH, width)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, height)
cap.set(cv2.CAP_PROP_FPS, fps)

i = 0

while True:
    i = i + 1
    ret, frame = cap.read()

    if frame is None:
        continue

    out.write(frame)

    now = time()
    diff = (1 / fps) - now - start
    if diff > 0:
        sleep(diff)
    start = now

    print(i)
    if i > 200:
        break

# now open http://localhost:8889/mystream/publish
